package com.sysacad.backend.config;

import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.enums.*;
import com.sysacad.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

@Configuration
public class DbSeeder {

    @Bean
    @Transactional
    CommandLineRunner initDatabase(
            UTNSeeder utnSeeder,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            FacultadRegionalRepository facultadRepository,
            SalonRepository salonRepository,
            MateriaRepository materiaRepository,
            ComisionRepository comisionRepository,
            AsignacionMateriaRepository asignacionMateriaRepository,
            HorarioCursadoRepository horarioCursadoRepository) {

        return args -> {

            utnSeeder.seed();

            Usuario profeNicolas = null;

            if (usuarioRepository.count() == 0) {
                System.out.println(">> DbSeeder: Creando usuarios de prueba...");

                // ADMIN
                Usuario admin = new Usuario();
                admin.setLegajo("1");
                admin.setNombre("Homero");
                admin.setApellido("Simpson");
                admin.setDni("11111111");
                admin.setPassword(passwordEncoder.encode("123456"));
                admin.setTipoDocumento(TipoDocumento.DNI);
                admin.setMail("admin@sysacad.com");
                admin.setRol(RolUsuario.ADMIN);
                admin.setGenero(Genero.M);
                admin.setEstado("ACTIVO");
                admin.setFechaNacimiento(LocalDate.of(1980, 1, 1));
                admin.setFechaIngreso(LocalDate.now());
                usuarioRepository.save(admin);

                // PROFESOR
                Usuario profe = new Usuario();
                profe.setLegajo("51111");
                profe.setNombre("Nicolas");
                profe.setApellido("Cabello");
                profe.setDni("22222222");
                profe.setPassword(passwordEncoder.encode("123456"));
                profe.setTipoDocumento(TipoDocumento.DNI);
                profe.setMail("nic@sysacad.com");
                profe.setRol(RolUsuario.PROFESOR);
                profe.setTituloAcademico("Dr. en Ciencias de la Computación");
                profe.setGenero(Genero.M);
                profe.setEstado("ACTIVO");
                profe.setFechaNacimiento(LocalDate.of(1990, 6, 23));
                profe.setFechaIngreso(LocalDate.now());
                profeNicolas = usuarioRepository.save(profe);

                // ESTUDIANTE
                Usuario alumno = new Usuario();
                alumno.setLegajo("55555");
                alumno.setNombre("Agustin");
                alumno.setApellido("Santinelli");
                alumno.setDni("33333333");
                alumno.setPassword(passwordEncoder.encode("123456"));
                alumno.setTipoDocumento(TipoDocumento.DNI);
                alumno.setMail("agus@sysacad.com");
                alumno.setRol(RolUsuario.ESTUDIANTE);
                alumno.setGenero(Genero.M);
                alumno.setEstado("ACTIVO");
                alumno.setFechaNacimiento(LocalDate.of(2004, 11, 17));
                alumno.setFechaIngreso(LocalDate.now());
                usuarioRepository.save(alumno);

                System.out.println(">> Usuarios creados con éxito.");
            } else {
                // Recuperamos al profesor si ya existía para asignarle materias
                profeNicolas = usuarioRepository.findByLegajo("51111").orElse(null);
            }

            // 3. Cargar Datos de Cursada (Comisiones, Horarios) - Solo si no hay comisiones
            if (comisionRepository.count() == 0 && profeNicolas != null) {
                System.out.println(">> DbSeeder: Creando infraestructura de cursada (Salones, Comisiones, Horarios)...");

                // A. Buscar Facultad (Creada en UTNSeeder)
                FacultadRegional frro = facultadRepository.findAll().stream()
                        .filter(f -> f.getCiudad().equalsIgnoreCase("Rosario"))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Error: Facultad Rosario no encontrada. Ejecuta UTNSeeder primero."));

                // B. Crear Salon
                Salon laboratorio305 = new Salon();
                laboratorio305.setFacultad(frro);
                laboratorio305.setNombre("Lab. de Computación 305");
                laboratorio305.setPiso("3");
                laboratorio305 = salonRepository.save(laboratorio305);

                // C. Buscar Materias Clave (ISI Nivel 1)
                Materia algoritmos = materiaRepository.findByNombre("Algoritmos y Estructuras de Datos")
                        .orElseThrow(() -> new RuntimeException("Materia Algoritmos no encontrada"));
                Materia sistemas = materiaRepository.findByNombre("Sistemas y Procesos de Negocio")
                        .orElseThrow(() -> new RuntimeException("Materia Sistemas no encontrada"));
                Materia ingles = materiaRepository.findByNombre("Inglés I")
                        .orElseThrow(() -> new RuntimeException("Materia Inglés I no encontrada"));

                // D. Crear Comisión "1K1"
                Comision comision1k1 = new Comision();
                comision1k1.setNombre("1K1");
                comision1k1.setAnio(1);
                comision1k1.setTurno("MAÑANA");
                comision1k1.setSalon(laboratorio305);
                comision1k1.setMaterias(Arrays.asList(algoritmos, sistemas, ingles)); // Materias que se dictan aquí
                comision1k1.setProfesores(List.of(profeNicolas)); // Plantel docente inicial
                comision1k1 = comisionRepository.save(comision1k1);

                // E. Asignar Cargos (Jefe de Cátedra)
                // Esto permite probar que Nicolas puede editar "Algoritmos" pero no "Inglés"
                AsignacionMateria cargoJefe = new AsignacionMateria();
                cargoJefe.setId(new AsignacionMateria.AsignacionMateriaId(profeNicolas.getId(), algoritmos.getId()));
                cargoJefe.setCargo(RolCargo.JEFE_CATEDRA);
                cargoJefe.setProfesor(profeNicolas);
                cargoJefe.setMateria(algoritmos);
                asignacionMateriaRepository.save(cargoJefe);

                // F. Crear Horarios de Cursada

                // Horario 1: Algoritmos - Lunes 08:00 a 12:00
                HorarioCursado horarioAlgo = new HorarioCursado();
                HorarioCursado.HorarioCursadoId idH1 = new HorarioCursado.HorarioCursadoId(
                        comision1k1.getId(),
                        algoritmos.getId(),
                        DiaSemana.LUNES,
                        LocalTime.of(8, 0)
                );
                horarioAlgo.setId(idH1);
                horarioAlgo.setHoraHasta(LocalTime.of(12, 0));
                horarioAlgo.setComision(comision1k1);
                horarioAlgo.setMateria(algoritmos);
                horarioCursadoRepository.save(horarioAlgo);

                // Horario 2: Sistemas - Martes 10:00 a 13:00
                HorarioCursado horarioSys = new HorarioCursado();
                HorarioCursado.HorarioCursadoId idH2 = new HorarioCursado.HorarioCursadoId(
                        comision1k1.getId(),
                        sistemas.getId(),
                        DiaSemana.MARTES,
                        LocalTime.of(10, 0)
                );
                horarioSys.setId(idH2);
                horarioSys.setHoraHasta(LocalTime.of(13, 0));
                horarioSys.setComision(comision1k1);
                horarioSys.setMateria(sistemas);
                horarioCursadoRepository.save(horarioSys);

                System.out.println(">> Seeding Académico Finalizado: Comisión 1K1 lista con horarios y profesor asignado.");
            }
        };
    }
}