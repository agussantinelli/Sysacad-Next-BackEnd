package com.sysacad.backend.config;

import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.enums.*;
import com.sysacad.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder; // Importar
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

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
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder // Inyectamos el encoder
    ) {
        return args -> {
            if (facultadRepository.count() > 0) {
                System.out.println(">> La base de datos ya tiene datos. Omitiendo seed.");
                return;
            }

            System.out.println(">> Iniciando Seeding de Datos (Seguro)...");

            // --- Facultad ---
            FacultadRegional frro = new FacultadRegional();
            frro.setCiudad("Rosario");
            frro.setProvincia("Santa Fe");
            frro = facultadRepository.save(frro);

            // --- Carrera ---
            Carrera isi = new Carrera();
            isi.setId(new Carrera.CarreraId(frro.getId(), "ISI"));
            isi.setNombre("Ingeniería en Sistemas de Información");
            isi.setFacultad(frro);
            isi = carreraRepository.save(isi);

            // --- Materias ---
            Materia am1 = crearMateria("Análisis Matemático I", TipoMateria.BASICA, DuracionMateria.ANUAL, (short) 5, materiaRepository);
            Materia aga = crearMateria("Álgebra y Geometría Analítica", TipoMateria.BASICA, DuracionMateria.ANUAL, (short) 5, materiaRepository);
            Materia syo = crearMateria("Sistemas y Organizaciones", TipoMateria.ESPECIFICA, DuracionMateria.ANUAL, (short) 3, materiaRepository);

            // --- Plan ---
            PlanDeEstudio plan2023 = new PlanDeEstudio();
            plan2023.setId(new PlanDeEstudio.PlanId(frro.getId(), isi.getId().getIdCarrera(), "Plan 2023"));
            plan2023.setFechaInicio(LocalDate.of(2023, 3, 1));
            plan2023.setEsVigente(true);
            plan2023 = planRepository.save(plan2023);

            asociarMateria(plan2023, am1, "95-0001", (short) 1, planMateriaRepository);
            asociarMateria(plan2023, aga, "95-0002", (short) 1, planMateriaRepository);
            asociarMateria(plan2023, syo, "95-0003", (short) 1, planMateriaRepository);

            // --- Usuarios (CON HASH) ---

            // ADMIN
            Usuario admin = new Usuario();
            admin.setLegajo("1");
            admin.setNombre("Administrador");
            admin.setApellido("Sistema");
            admin.setDni("11111111");
            // ENCRIPTAMOS LA CONTRASEÑA
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
            // ENCRIPTAMOS
            profe.setPassword(passwordEncoder.encode("123456"));
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
            alumno.setLegajo("55555");
            alumno.setNombre("Marty");
            alumno.setApellido("McFly");
            alumno.setDni("33333333");
            // ENCRIPTAMOS
            alumno.setPassword(passwordEncoder.encode("123456"));
            alumno.setTipoDocumento(TipoDocumento.DNI);
            alumno.setMail("marty@sysacad.com");
            alumno.setRol(RolUsuario.ESTUDIANTE);
            alumno.setGenero(Genero.M);
            alumno.setEstado("ACTIVO");
            alumno.setFechaNacimiento(LocalDate.of(1968, 6, 12));
            alumno.setFechaIngreso(LocalDate.now());
            usuarioRepository.save(alumno);

            System.out.println(">> Seeding Completado (Con contraseñas encriptadas).");
        };
    }

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