package com.sysacad.backend.config;

import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.enums.*;
import com.sysacad.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class DbSeeder {

    @Bean
    @Transactional
    CommandLineRunner initDatabase(
            FacultadRegionalRepository facultadRepository,
            CarreraRepository carreraRepository,
            MateriaRepository materiaRepository,
            PlanDeEstudioRepository planRepository,
            PlanMateriaRepository planMateriaRepository,
            UsuarioRepository usuarioRepository
    ) {
        return args -> {
            // 1. Verificar si ya existen datos para no duplicar
            if (facultadRepository.count() > 0) {
                System.out.println(">> La base de datos ya tiene datos. Omitiendo seed.");
                return;
            }

            System.out.println(">> Iniciando Seeding de Datos...");

            // --- 2. Crear Facultad ---
            FacultadRegional frro = new FacultadRegional();
            frro.setCiudad("Rosario");
            frro.setProvincia("Santa Fe");
            frro = facultadRepository.save(frro);

            // --- 3. Crear Carrera (Ingeniería en Sistemas) ---
            Carrera isi = new Carrera();
            // Clave Compuesta
            isi.setId(new Carrera.CarreraId(frro.getId(), "ISI"));
            isi.setNombre("Ingeniería en Sistemas de Información");
            isi.setFacultad(frro); // Relación JPA
            isi = carreraRepository.save(isi);

            // --- 4. Crear Materias ---
            Materia am1 = crearMateria("Análisis Matemático I", TipoMateria.BASICA, DuracionMateria.ANUAL, (short) 5, materiaRepository);
            Materia aga = crearMateria("Álgebra y Geometría Analítica", TipoMateria.BASICA, DuracionMateria.ANUAL, (short) 5, materiaRepository);
            Materia syo = crearMateria("Sistemas y Organizaciones", TipoMateria.ESPECIFICA, DuracionMateria.ANUAL, (short) 3, materiaRepository);

            // --- 5. Crear Plan de Estudio (Plan 2023) ---
            PlanDeEstudio plan2023 = new PlanDeEstudio();
            plan2023.setId(new PlanDeEstudio.PlanId(frro.getId(), isi.getId().getIdCarrera(), "Plan 2023"));
            plan2023.setFechaInicio(LocalDate.of(2023, 3, 1));
            plan2023.setEsVigente(true);
            plan2023 = planRepository.save(plan2023);

            // --- 6. Asociar Materias al Plan (PlanMateria) ---
            asociarMateria(plan2023, am1, "95-0001", (short) 1, planMateriaRepository);
            asociarMateria(plan2023, aga, "95-0002", (short) 1, planMateriaRepository);
            asociarMateria(plan2023, syo, "95-0003", (short) 1, planMateriaRepository);

            // --- 7. Crear Usuarios ---

            // ADMIN
            Usuario admin = new Usuario();
            admin.setLegajo("1");
            admin.setNombre("Administrador");
            admin.setApellido("Sistema");
            admin.setDni("11111111");
            admin.setPassword("1234");
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
            profe.setLegajo("DOC-2024");
            profe.setNombre("Nicolas");
            profe.setApellido("Cabello");
            admin.setPassword("1234");
            profe.setDni("22222222");
            profe.setTipoDocumento(TipoDocumento.DNI);
            profe.setMail("nic@sysacad.com");
            profe.setRol(RolUsuario.PROFESOR);
            profe.setTituloAcademico("Dr. en Matemáticas");
            profe.setGenero(Genero.M);
            profe.setEstado("ACTIVO");
            profe.setFechaNacimiento(LocalDate.of(1912, 6, 23));
            profe.setFechaIngreso(LocalDate.now());
            usuarioRepository.save(profe);

            // ESTUDIANTE
            Usuario alumno = new Usuario();
            alumno.setLegajo("45123");
            alumno.setNombre("Marty");
            alumno.setApellido("McFly");
            alumno.setDni("33333333");
            alumno.setTipoDocumento(TipoDocumento.DNI);
            alumno.setMail("marty@sysacad.com");
            alumno.setRol(RolUsuario.ESTUDIANTE);
            alumno.setGenero(Genero.M);
            alumno.setEstado("ACTIVO");
            admin.setPassword("1234");
            alumno.setFechaNacimiento(LocalDate.of(1968, 6, 12));
            alumno.setFechaIngreso(LocalDate.now());
            usuarioRepository.save(alumno);

            System.out.println(">> Seeding Completado con Éxito.");
        };
    }

    // Métodos auxiliares para no repetir código

    private Materia crearMateria(String nombre, TipoMateria tipo, DuracionMateria duracion, Short horas, MateriaRepository repo) {
        Materia m = new Materia();
        m.setNombre(nombre);
        m.setTipoMateria(tipo);
        m.setDuracion(duracion);
        m.setHorasCursado(horas);
        m.setOptativa(false);
        m.setRendirLibre(true);
        return repo.save(m);
    }

    private void asociarMateria(PlanDeEstudio plan, Materia materia, String codigo, Short nivel, PlanMateriaRepository repo) {
        PlanMateria pm = new PlanMateria();
        // Construimos la clave compuesta
        PlanMateria.PlanMateriaId id = new PlanMateria.PlanMateriaId(
                plan.getId().getIdFacultad(),
                plan.getId().getIdCarrera(),
                plan.getId().getNombre(),
                materia.getId()
        );
        pm.setId(id);
        pm.setMateria(materia);
        pm.setPlan(plan);
        pm.setCodigoMateria(codigo);
        pm.setNivel(nivel);
        repo.save(pm);
    }
}