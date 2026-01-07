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

            // Referencias para uso posterior
            Usuario profeNicolas = null;
            Usuario profeLaura = null;
            Usuario profeRoberto = null;
            Usuario profeAna = null;

            Usuario alumnoAgustin = null;
            Usuario alumnoMaria = null;
            Usuario alumnoJuan = null;
            Usuario alumnoSofia = null;
            Usuario alumnoMiguel = null;

            // 2. Cargar Usuarios
            if (usuarioRepository.count() == 0) {
                System.out.println(">> DbSeeder: Creando población de usuarios...");

                // --- ADMIN ---
                Usuario admin = createUsuario(usuarioRepository, passwordEncoder, "1", "Homero", "Simpson", "11111111", "admin@sysacad.com", RolUsuario.ADMIN, Genero.M, "Rector");

                // --- PROFESORES ---
                profeNicolas = createUsuario(usuarioRepository, passwordEncoder, "51111", "Nicolas", "Cabello", "22222222", "nic@sysacad.com", RolUsuario.PROFESOR, Genero.M, "Dr. en Ciencias de la Computación");
                profeLaura = createUsuario(usuarioRepository, passwordEncoder, "52222", "Laura", "Gomez", "22222223", "laura@sysacad.com", RolUsuario.PROFESOR, Genero.F, "Ingeniera en Sistemas");
                profeRoberto = createUsuario(usuarioRepository, passwordEncoder, "53333", "Roberto", "Diaz", "22222224", "roberto@sysacad.com", RolUsuario.PROFESOR, Genero.M, "Lic. en Física");
                profeAna = createUsuario(usuarioRepository, passwordEncoder, "54444", "Ana", "Martinez", "22222225", "ana@sysacad.com", RolUsuario.PROFESOR, Genero.F, "Traductora Pública");

                // --- ESTUDIANTES ---
                alumnoAgustin = createUsuario(usuarioRepository, passwordEncoder, "55555", "Agustin", "Santinelli", "33333333", "agus@sysacad.com", RolUsuario.ESTUDIANTE, Genero.M, null);
                alumnoMaria = createUsuario(usuarioRepository, passwordEncoder, "56666", "Maria", "Rodriguez", "33333334", "maria@sysacad.com", RolUsuario.ESTUDIANTE, Genero.F, null);
                alumnoJuan = createUsuario(usuarioRepository, passwordEncoder, "57777", "Juan", "Perez", "33333335", "juan@sysacad.com", RolUsuario.ESTUDIANTE, Genero.M, null);
                alumnoSofia = createUsuario(usuarioRepository, passwordEncoder, "58888", "Sofia", "Lopez", "33333336", "sofia@sysacad.com", RolUsuario.ESTUDIANTE, Genero.F, null);
                alumnoMiguel = createUsuario(usuarioRepository, passwordEncoder, "59999", "Miguel", "Torres", "33333337", "miguel@sysacad.com", RolUsuario.ESTUDIANTE, Genero.M, null);

                System.out.println(">> Usuarios creados con éxito.");
            } else {
                // Recuperar referencias si ya existían (para recargas)
                profeNicolas = usuarioRepository.findByLegajo("51111").orElse(null);
                profeLaura = usuarioRepository.findByLegajo("52222").orElse(null);
                profeRoberto = usuarioRepository.findByLegajo("53333").orElse(null);
                profeAna = usuarioRepository.findByLegajo("54444").orElse(null);

                alumnoAgustin = usuarioRepository.findByLegajo("55555").orElse(null);
                alumnoMaria = usuarioRepository.findByLegajo("56666").orElse(null);
                alumnoJuan = usuarioRepository.findByLegajo("57777").orElse(null);
                alumnoSofia = usuarioRepository.findByLegajo("58888").orElse(null);
                alumnoMiguel = usuarioRepository.findByLegajo("59999").orElse(null);
            }

            // 3. Infraestructura y Cursada
            if (comisionRepository.count() == 0 && profeNicolas != null) {
                System.out.println(">> DbSeeder: Desplegando infraestructura de cursada completa...");

                FacultadRegional frro = facultadRepository.findAll().stream()
                        .filter(f -> f.getCiudad().equalsIgnoreCase("Rosario"))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Error: Facultad Rosario no encontrada."));

                // --- SALONES ---
                Salon lab305 = createSalon(salonRepository, frro, "Lab. Computación 305", "3");
                Salon aula401 = createSalon(salonRepository, frro, "Aula 401", "4");
                Salon aula402 = createSalon(salonRepository, frro, "Aula 402", "4");
                Salon aula101 = createSalon(salonRepository, frro, "Aula 101", "1"); // PB
                Salon aula301 = createSalon(salonRepository, frro, "Aula 301", "3");
                Salon sum = createSalon(salonRepository, frro, "SUM", "0");
                Salon aula201 = createSalon(salonRepository, frro, "Aula 201", "2");

                // --- MATERIAS NIVEL 1 ---
                Materia algoritmos = getMateria(materiaRepository, "Algoritmos y Estructuras de Datos");
                Materia sistemas = getMateria(materiaRepository, "Sistemas y Procesos de Negocio");
                Materia ingles1 = getMateria(materiaRepository, "Inglés I");
                Materia analisis1 = getMateria(materiaRepository, "Análisis Matemático I");
                Materia fisica1 = getMateria(materiaRepository, "Física I");
                Materia algebra = getMateria(materiaRepository, "Álgebra y Geometría Analítica");
                Materia arquitectura = getMateria(materiaRepository, "Arquitectura de Computadoras");

                // --- MATERIAS NIVEL 2 ---
                Materia analisis2 = getMateria(materiaRepository, "Análisis Matemático II");
                Materia sintaxis = getMateria(materiaRepository, "Sintaxis y Semántica de los Lenguajes");
                Materia paradigmas = getMateria(materiaRepository, "Paradigmas de Programación");
                Materia sistemasOp = getMateria(materiaRepository, "Sistemas Operativos");

                // --- MATERIAS NIVEL 3 ---
                Materia disenio = getMateria(materiaRepository, "Diseño de Sistemas de Información");
                Materia basesDatos = getMateria(materiaRepository, "Bases de Datos");

                // --- ASIGNACIONES DOCENTES (Jefaturas) ---
                asignarCargo(asignacionMateriaRepository, profeNicolas, algoritmos, RolCargo.JEFE_CATEDRA);
                asignarCargo(asignacionMateriaRepository, profeNicolas, basesDatos, RolCargo.JEFE_CATEDRA);
                asignarCargo(asignacionMateriaRepository, profeNicolas, sintaxis, RolCargo.JEFE_CATEDRA); // Experto en computación

                asignarCargo(asignacionMateriaRepository, profeLaura, analisis1, RolCargo.JEFE_CATEDRA);
                asignarCargo(asignacionMateriaRepository, profeLaura, analisis2, RolCargo.JEFE_CATEDRA);

                asignarCargo(asignacionMateriaRepository, profeRoberto, fisica1, RolCargo.JEFE_CATEDRA);
                asignarCargo(asignacionMateriaRepository, profeRoberto, algebra, RolCargo.JEFE_CATEDRA); // Lic. en Física enseña Álgebra

                asignarCargo(asignacionMateriaRepository, profeAna, ingles1, RolCargo.JEFE_CATEDRA);


                // === COMISIONES Y HORARIOS ===

                // 1. COMISIÓN 1K1 (Mañana - 1er Año) - Aula Grande
                // Materias: Algoritmos, Sistemas, Inglés, Álgebra
                Comision c1k1 = createComision(comisionRepository, "1K1", 1, "MAÑANA", lab305,
                        Arrays.asList(algoritmos, sistemas, ingles1, algebra),
                        List.of(profeNicolas, profeAna, profeRoberto));

                crearHorario(horarioCursadoRepository, c1k1, algoritmos, DiaSemana.LUNES, 8, 12); // Nico
                crearHorario(horarioCursadoRepository, c1k1, sistemas, DiaSemana.MARTES, 10, 13); // Nico (Docente)
                crearHorario(horarioCursadoRepository, c1k1, algebra, DiaSemana.JUEVES, 8, 12);   // Roberto
                crearHorario(horarioCursadoRepository, c1k1, ingles1, DiaSemana.VIERNES, 8, 10);  // Ana

                // 2. COMISIÓN 1K2 (Noche - 1er Año) - Aula Común
                // Materias: Algoritmos, Análisis I, Física I, Arquitectura
                Comision c1k2 = createComision(comisionRepository, "1K2", 1, "NOCHE", aula401,
                        Arrays.asList(algoritmos, analisis1, fisica1, arquitectura),
                        List.of(profeNicolas, profeLaura, profeRoberto));

                crearHorario(horarioCursadoRepository, c1k2, arquitectura, DiaSemana.LUNES, 18, 22); // Nico (Docente)
                crearHorario(horarioCursadoRepository, c1k2, algoritmos, DiaSemana.MIERCOLES, 18, 22); // Nico
                crearHorario(horarioCursadoRepository, c1k2, analisis1, DiaSemana.JUEVES, 19, 23);   // Laura
                crearHorario(horarioCursadoRepository, c1k2, fisica1, DiaSemana.VIERNES, 18, 22);    // Roberto

                // 3. COMISIÓN 2K1 (Tarde - 2do Año)
                // Materias: Análisis II, Sintaxis, Paradigmas, Sist. Operativos
                Comision c2k1 = createComision(comisionRepository, "2K1", 2, "TARDE", aula201,
                        Arrays.asList(analisis2, sintaxis, paradigmas, sistemasOp),
                        List.of(profeLaura, profeNicolas));

                crearHorario(horarioCursadoRepository, c2k1, analisis2, DiaSemana.LUNES, 14, 18);    // Laura
                crearHorario(horarioCursadoRepository, c2k1, sintaxis, DiaSemana.MARTES, 14, 18);    // Nico
                crearHorario(horarioCursadoRepository, c2k1, paradigmas, DiaSemana.MIERCOLES, 14, 18);// Nico
                crearHorario(horarioCursadoRepository, c2k1, sistemasOp, DiaSemana.VIERNES, 14, 18);  // Nico

                // 4. COMISIÓN 3K1 (Noche - 3er Año)
                // Materias: Diseño, Bases de Datos
                Comision c3k1 = createComision(comisionRepository, "3K1", 3, "NOCHE", lab305,
                        Arrays.asList(disenio, basesDatos),
                        List.of(profeNicolas));

                crearHorario(horarioCursadoRepository, c3k1, basesDatos, DiaSemana.MARTES, 18, 22); // Nico (BD)
                crearHorario(horarioCursadoRepository, c3k1, disenio, DiaSemana.JUEVES, 18, 22);    // Nico (Diseño)


                // === INSCRIPCIONES ===

                // Agustin (Legajo 55555) -> Cursa 2do Año (2K1) pero debe recursar Algoritmos (1K1)
                if (alumnoAgustin != null) {
                    inscribirAlumno(inscripcionRepository, alumnoAgustin, c1k1);
                    inscribirAlumno(inscripcionRepository, alumnoAgustin, c2k1);

                    // Notas Históricas
                    cargarNota(calificacionRepository, inscripcionRepository, alumnoAgustin, c1k1, "1er Parcial - Algoritmos", "8.50");
                    cargarNota(calificacionRepository, inscripcionRepository, alumnoAgustin, c1k1, "TP Inglés", "9.00");
                }

                // Maria (Legajo 56666) -> Ingresante pura (1K1)
                if (alumnoMaria != null) {
                    inscribirAlumno(inscripcionRepository, alumnoMaria, c1k1);
                    cargarNota(calificacionRepository, inscripcionRepository, alumnoMaria, c1k1, "1er Parcial - Algoritmos", "10.00");
                }

                // Juan (Legajo 57777) -> Ingresante Turno Noche (1K2)
                if (alumnoJuan != null) {
                    inscribirAlumno(inscripcionRepository, alumnoJuan, c1k2);
                }

                // Sofia (Legajo 58888) -> Avanzada (3K1)
                if (alumnoSofia != null) {
                    inscribirAlumno(inscripcionRepository, alumnoSofia, c3k1);
                    cargarNota(calificacionRepository, inscripcionRepository, alumnoSofia, c3k1, "TP Normalización BD", "7.50");
                }

                // Miguel (Legajo 59999) -> Ingresante 1K2, sin notas aun
                if (alumnoMiguel != null) {
                    inscribirAlumno(inscripcionRepository, alumnoMiguel, c1k2);
                }

                System.out.println(">> Seeding Académico Finalizado: 4 Comisiones, Múltiples Horarios y Notas cargadas.");
            }
        };
    }

    // --- Métodos Helper para limpieza y DRY ---

    private Usuario createUsuario(UsuarioRepository repo, PasswordEncoder encoder, String legajo, String nombre, String apellido, String dni, String mail, RolUsuario rol, Genero genero, String titulo) {
        Usuario u = new Usuario();
        u.setLegajo(legajo);
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setDni(dni);
        u.setPassword(encoder.encode("123456"));
        u.setTipoDocumento(TipoDocumento.DNI);
        u.setMail(mail);
        u.setRol(rol);
        u.setGenero(genero);
        u.setEstado("ACTIVO");
        u.setTituloAcademico(titulo);
        u.setFechaNacimiento(LocalDate.of(1995, 1, 1)); // Default
        u.setFechaIngreso(LocalDate.now());
        return repo.save(u);
    }

    private Salon createSalon(SalonRepository repo, FacultadRegional facu, String nombre, String piso) {
        Salon s = new Salon();
        s.setFacultad(facu);
        s.setNombre(nombre);
        s.setPiso(piso);
        return repo.save(s);
    }

    private Comision createComision(ComisionRepository repo, String nombre, Integer anio, String turno, Salon salon, List<Materia> materias, List<Usuario> profes) {
        Comision c = new Comision();
        c.setNombre(nombre);
        c.setAnio(anio);
        c.setTurno(turno);
        c.setSalon(salon);
        c.setMaterias(materias);
        c.setProfesores(profes);
        return repo.save(c);
    }

    private Materia getMateria(MateriaRepository repo, String nombre) {
        return repo.findByNombre(nombre)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada: " + nombre));
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

    private void cargarNota(CalificacionRepository califRepo, InscripcionRepository inscRepo, Usuario alumno, Comision comision, String concepto, String valor) {
        // Usamos el método seguro para obtener la inscripción
        Inscripcion insc = inscRepo.findByIdIdUsuarioAndIdIdComision(alumno.getId(), comision.getId())
                .stream().findFirst()
                .orElseThrow(() -> new RuntimeException("No se encontró inscripción para cargar nota"));

        Calificacion nota = new Calificacion();
        CalificacionId id = new CalificacionId(
                insc.getId().getIdUsuario(),
                insc.getId().getIdComision(),
                insc.getId().getTipo(),
                insc.getId().getVecesTipo(),
                concepto
        );
        nota.setId(id);
        nota.setNota(new BigDecimal(valor));
        nota.setInscripcion(insc);
        califRepo.save(nota);
    }
}