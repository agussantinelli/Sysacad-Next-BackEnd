package com.sysacad.backend.config;

import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.Inscripcion.InscripcionId;
import com.sysacad.backend.modelo.Calificacion.CalificacionId;
import com.sysacad.backend.modelo.enums.*;
import com.sysacad.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
            HorarioCursadoRepository horarioCursadoRepository,
            InscripcionRepository inscripcionRepository,
            CalificacionRepository calificacionRepository) {

        return args -> {

            // 1. Carga Estructural (Materias, Planes)
            utnSeeder.seed();

            Usuario profeNicolas = null;
            Usuario profeLaura = null;
            Usuario alumnoAgustin = null;
            Usuario alumnoMaria = null;
            Usuario alumnoJuan = null;

            // 2. Cargar Usuarios
            if (usuarioRepository.count() == 0) {
                System.out.println(">> DbSeeder: Creando usuarios de prueba...");

                // --- ADMIN ---
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

                // --- PROFESOR 1 (Nicolas) ---
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

                // --- PROFESOR 2 (Laura) ---
                Usuario profe2 = new Usuario();
                profe2.setLegajo("52222");
                profe2.setNombre("Laura");
                profe2.setApellido("Gomez");
                profe2.setDni("22222223");
                profe2.setPassword(passwordEncoder.encode("123456"));
                profe2.setTipoDocumento(TipoDocumento.DNI);
                profe2.setMail("laura@sysacad.com");
                profe2.setRol(RolUsuario.PROFESOR);
                profe2.setTituloAcademico("Ingeniera en Sistemas");
                profe2.setGenero(Genero.F);
                profe2.setEstado("ACTIVO");
                profe2.setFechaNacimiento(LocalDate.of(1985, 3, 15));
                profe2.setFechaIngreso(LocalDate.now());
                profeLaura = usuarioRepository.save(profe2);

                // --- ESTUDIANTE 1 (Agustin) ---
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
                alumnoAgustin = usuarioRepository.save(alumno);

                // --- ESTUDIANTE 2 (Maria) ---
                Usuario alumno2 = new Usuario();
                alumno2.setLegajo("56666");
                alumno2.setNombre("Maria");
                alumno2.setApellido("Rodriguez");
                alumno2.setDni("33333334");
                alumno2.setPassword(passwordEncoder.encode("123456"));
                alumno2.setTipoDocumento(TipoDocumento.DNI);
                alumno2.setMail("maria@sysacad.com");
                alumno2.setRol(RolUsuario.ESTUDIANTE);
                alumno2.setGenero(Genero.F);
                alumno2.setEstado("ACTIVO");
                alumno2.setFechaNacimiento(LocalDate.of(2003, 5, 20));
                alumno2.setFechaIngreso(LocalDate.now());
                alumnoMaria = usuarioRepository.save(alumno2);

                // --- ESTUDIANTE 3 (Juan) ---
                Usuario alumno3 = new Usuario();
                alumno3.setLegajo("57777");
                alumno3.setNombre("Juan");
                alumno3.setApellido("Perez");
                alumno3.setDni("33333335");
                alumno3.setPassword(passwordEncoder.encode("123456"));
                alumno3.setTipoDocumento(TipoDocumento.DNI);
                alumno3.setMail("juan@sysacad.com");
                alumno3.setRol(RolUsuario.ESTUDIANTE);
                alumno3.setGenero(Genero.M);
                alumno3.setEstado("ACTIVO");
                alumno3.setFechaNacimiento(LocalDate.of(2004, 1, 10));
                alumno3.setFechaIngreso(LocalDate.now());
                alumnoJuan = usuarioRepository.save(alumno3);

                System.out.println(">> Usuarios creados con éxito.");
            } else {
                // Recuperar referencias si ya existían
                profeNicolas = usuarioRepository.findByLegajo("51111").orElse(null);
                profeLaura = usuarioRepository.findByLegajo("52222").orElse(null);
                alumnoAgustin = usuarioRepository.findByLegajo("55555").orElse(null);
                alumnoMaria = usuarioRepository.findByLegajo("56666").orElse(null);
                alumnoJuan = usuarioRepository.findByLegajo("57777").orElse(null);
            }

            // 3. Infraestructura y Cursada
            if (comisionRepository.count() == 0 && profeNicolas != null) {
                System.out.println(">> DbSeeder: Creando infraestructura de cursada...");

                FacultadRegional frro = facultadRepository.findAll().stream()
                        .filter(f -> f.getCiudad().equalsIgnoreCase("Rosario"))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Error: Facultad Rosario no encontrada."));

                // Salones
                Salon lab305 = new Salon();
                lab305.setFacultad(frro);
                lab305.setNombre("Aula 305");
                lab305.setPiso("3");
                lab305 = salonRepository.save(lab305);

                Salon aula401 = new Salon();
                aula401.setFacultad(frro);
                aula401.setNombre("Aula 401");
                aula401.setPiso("4");
                aula401 = salonRepository.save(aula401);

                Salon aula402 = new Salon();
                aula402.setFacultad(frro);
                aula402.setNombre("Aula 402");
                aula402.setPiso("4");
                aula402 = salonRepository.save(aula402);

                Salon aula101 = new Salon();
                aula101.setFacultad(frro);
                aula101.setNombre("Aula 101");
                aula101.setPiso("1");
                aula101 = salonRepository.save(aula101);

                Salon aula301 = new Salon();
                aula301.setFacultad(frro);
                aula301.setNombre("Aula 301");
                aula301.setPiso("3");
                aula301 = salonRepository.save(aula301);

                Salon sum = new Salon();
                sum.setFacultad(frro);
                sum.setNombre("SUM");
                sum.setPiso("1");
                sum = salonRepository.save(sum);

                Salon aula201 = new Salon();
                aula201.setFacultad(frro);
                aula201.setNombre("Aula 201");
                aula201.setPiso("2");
                aula201 = salonRepository.save(aula201);

                // Materias
                Materia algoritmos = materiaRepository.findByNombre("Algoritmos y Estructuras de Datos").orElseThrow();
                Materia sistemas = materiaRepository.findByNombre("Sistemas y Procesos de Negocio").orElseThrow();
                Materia ingles = materiaRepository.findByNombre("Inglés I").orElseThrow();
                Materia analisis1 = materiaRepository.findByNombre("Análisis Matemático I").orElseThrow();

                // --- COMISIÓN 1K1 (MAÑANA) ---
                Comision comision1k1 = new Comision();
                comision1k1.setNombre("1K1");
                comision1k1.setAnio(1);
                comision1k1.setTurno("MAÑANA");
                comision1k1.setSalon(lab305);
                comision1k1.setMaterias(Arrays.asList(algoritmos, sistemas, ingles));
                comision1k1.setProfesores(List.of(profeNicolas));
                comision1k1 = comisionRepository.save(comision1k1);

                // Horarios 1K1
                crearHorario(horarioCursadoRepository, comision1k1, algoritmos, DiaSemana.LUNES, 8, 12);
                crearHorario(horarioCursadoRepository, comision1k1, sistemas, DiaSemana.MARTES, 10, 13);

                // --- COMISIÓN 1K2 (NOCHE) ---
                Comision comision1k2 = new Comision();
                comision1k2.setNombre("1K2");
                comision1k2.setAnio(1);
                comision1k2.setTurno("NOCHE");
                comision1k2.setSalon(aula401);
                comision1k2.setMaterias(Arrays.asList(algoritmos, analisis1));
                comision1k2.setProfesores(List.of(profeNicolas, profeLaura)); // Nicolas da Algoritmos, Laura da Analisis
                comision1k2 = comisionRepository.save(comision1k2);

                // Horarios 1K2
                crearHorario(horarioCursadoRepository, comision1k2, algoritmos, DiaSemana.MIERCOLES, 18, 22);
                crearHorario(horarioCursadoRepository, comision1k2, analisis1, DiaSemana.JUEVES, 19, 23);

                // --- ASIGNACIONES / JEFATURAS ---
                // Nicolas es Jefe de Cátedra de Algoritmos (en general)
                asignarCargo(asignacionMateriaRepository, profeNicolas, algoritmos, RolCargo.JEFE_CATEDRA);
                // Laura es Jefe de Cátedra de Análisis Matemático I
                asignarCargo(asignacionMateriaRepository, profeLaura, analisis1, RolCargo.JEFE_CATEDRA);
                // Nicolas es Docente en Sistemas (no jefe)
                asignarCargo(asignacionMateriaRepository, profeNicolas, sistemas, RolCargo.DOCENTE);

                // --- INSCRIPCIONES ---
                if (alumnoAgustin != null && alumnoMaria != null && alumnoJuan != null) {
                    // Agustin y Maria a la 1K1
                    inscribirAlumno(inscripcionRepository, alumnoAgustin, comision1k1);
                    inscribirAlumno(inscripcionRepository, alumnoMaria, comision1k1);

                    // Juan a la 1K2
                    Inscripcion inscJuan = inscribirAlumno(inscripcionRepository, alumnoJuan, comision1k2);

                    // --- CALIFICACIONES (Simulacro) ---
                    // Cargamos una nota a Agustin en la 1K1
                    Inscripcion inscAgus = inscripcionRepository.findByIdIdUsuarioAndIdIdComision(alumnoAgustin.getId(), comision1k1.getId())
                            .stream().findFirst().orElseThrow();

                    Calificacion nota1 = new Calificacion();
                    CalificacionId idNota = new CalificacionId(
                            inscAgus.getId().getIdUsuario(),
                            inscAgus.getId().getIdComision(),
                            inscAgus.getId().getTipo(),
                            inscAgus.getId().getVecesTipo(),
                            "1er Parcial - Algoritmos"
                    );
                    nota1.setId(idNota);
                    nota1.setNota(new BigDecimal("8.50"));
                    nota1.setInscripcion(inscAgus); // Relación JPA
                    calificacionRepository.save(nota1);

                    System.out.println(">> Seeding Académico Finalizado: 2 Comisiones, Horarios, Inscripciones y Notas cargadas.");
                }
            }
        };
    }

    private void crearHorario(HorarioCursadoRepository repo, Comision com, Materia mat, DiaSemana dia, int hDesde, int hHasta) {
        HorarioCursado horario = new HorarioCursado();
        HorarioCursado.HorarioCursadoId id = new HorarioCursado.HorarioCursadoId(
                com.getId(), mat.getId(), dia, LocalTime.of(hDesde, 0)
        );
        horario.setId(id);
        horario.setHoraHasta(LocalTime.of(hHasta, 0));
        horario.setComision(com);
        horario.setMateria(mat);
        repo.save(horario);
    }

    private void asignarCargo(AsignacionMateriaRepository repo, Usuario profe, Materia mat, RolCargo cargo) {
        AsignacionMateria asignacion = new AsignacionMateria();
        asignacion.setId(new AsignacionMateria.AsignacionMateriaId(profe.getId(), mat.getId()));
        asignacion.setCargo(cargo);
        asignacion.setProfesor(profe);
        asignacion.setMateria(mat);
        repo.save(asignacion);
    }

    private Inscripcion inscribirAlumno(InscripcionRepository repo, Usuario alumno, Comision comision) {
        Inscripcion insc = new Inscripcion();
        InscripcionId id = new InscripcionId(alumno.getId(), comision.getId(), TipoInscripcion.CURSADO, 1);
        insc.setId(id);
        insc.setCondicion("REGULAR");
        insc.setFechaInscripcion(LocalDateTime.now());
        insc.setUsuario(alumno);
        insc.setComision(comision);
        return repo.save(insc);
    }
}