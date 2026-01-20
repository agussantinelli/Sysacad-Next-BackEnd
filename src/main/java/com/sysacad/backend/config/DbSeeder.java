package com.sysacad.backend.config;

import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.Inscripcion.InscripcionId;
import com.sysacad.backend.modelo.Calificacion.CalificacionId;
import com.sysacad.backend.modelo.enums.*;
import com.sysacad.backend.repository.*;
import com.sysacad.backend.modelo.MesaExamen;
import com.sysacad.backend.modelo.DetalleMesaExamen;
import com.sysacad.backend.modelo.InscripcionExamen;
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
                        MatriculacionRepository matriculacionRepository, // Agregado para matricular alumnos
                        PasswordEncoder passwordEncoder,
                        FacultadRegionalRepository facultadRepository,
                        SalonRepository salonRepository,
                        MateriaRepository materiaRepository,
                        ComisionRepository comisionRepository,
                        AsignacionMateriaRepository asignacionMateriaRepository,
                        HorarioCursadoRepository horarioCursadoRepository,
                        InscripcionRepository inscripcionRepository,
                        CalificacionRepository calificacionRepository,
                        MesaExamenRepository mesaExamenRepository,
                        DetalleMesaExamenRepository detalleMesaExamenRepository,
                        InscripcionExamenRepository inscripcionExamenRepository) {

                return args -> {

                        // 1. Carga Estructural (Materias, Planes, Carreras)
                        utnSeeder.seed();

                        Usuario profeNicolas = null;
                        Usuario profeLaura = null;
                        Usuario profeRoberto = null;
                        Usuario profeAna = null;
                        Usuario profeSandra = null;
                        Usuario profeCristian = null;

                        Usuario alumnoAgustin = null;
                        Usuario alumnoMaria = null;
                        Usuario alumnoJuan = null;
                        Usuario alumnoSofia = null;
                        Usuario alumnoMiguel = null;
                        Usuario alumnoLucia = null;
                        Usuario alumnoCarlos = null;

                        // Cargar Usuarios
                        if (usuarioRepository.count() == 0) {
                                System.out.println(">> DbSeeder: Creando población de usuarios...");

                                // --- ADMIN ---
                                createUsuario(usuarioRepository, passwordEncoder, "1", "Homero", "Simpson", "11111111",
                                                "admin@sysacad.com", RolUsuario.ADMIN, Genero.M, "Rector",
                                                LocalDate.of(1980, 5, 12));

                                // --- PROFESORES ---
                                profeNicolas = createUsuario(usuarioRepository, passwordEncoder, "51111", "Nicolas",
                                                "Cabello",
                                                "22222222", "nic@sysacad.com", RolUsuario.PROFESOR, Genero.M,
                                                "Dr. en Ciencias de la Computación", LocalDate.of(1990, 6, 23));
                                profeLaura = createUsuario(usuarioRepository, passwordEncoder, "52222", "Laura",
                                                "Gomez", "22222223",
                                                "laura@sysacad.com", RolUsuario.PROFESOR, Genero.F,
                                                "Ingeniera en Sistemas",
                                                LocalDate.of(1985, 3, 15));
                                profeRoberto = createUsuario(usuarioRepository, passwordEncoder, "53333", "Roberto",
                                                "Diaz", "22222224",
                                                "roberto@sysacad.com", RolUsuario.PROFESOR, Genero.M, "Lic. en Física",
                                                LocalDate.of(1978, 9, 10));
                                profeAna = createUsuario(usuarioRepository, passwordEncoder, "54444", "Ana", "Martinez",
                                                "22222225",
                                                "ana@sysacad.com", RolUsuario.PROFESOR, Genero.F, "Traductora Pública",
                                                LocalDate.of(1982, 11, 30));
                                profeSandra = createUsuario(usuarioRepository, passwordEncoder, "55551", "Julian",
                                                "Bricco",
                                                "22222226", "juli@sysacad.com", RolUsuario.PROFESOR, Genero.F,
                                                "Matemática",
                                                LocalDate.of(1975, 4, 12));
                                profeCristian = createUsuario(usuarioRepository, passwordEncoder, "55552", "Marino",
                                                "Hinestroza",
                                                "22222227", "marino@sysacad.com", RolUsuario.PROFESOR, Genero.M,
                                                "Ingeniero Electrónico",
                                                LocalDate.of(1988, 8, 25));

                                // ESTUDIANTES
                                alumnoAgustin = createUsuario(usuarioRepository, passwordEncoder, "55555", "Agustin",
                                                "Santinelli",
                                                "33333333", "agus@sysacad.com", RolUsuario.ESTUDIANTE, Genero.M, null,
                                                LocalDate.of(2004, 11, 17));
                                alumnoMaria = createUsuario(usuarioRepository, passwordEncoder, "56666", "Maria",
                                                "Rodriguez",
                                                "33333334", "maria@sysacad.com", RolUsuario.ESTUDIANTE, Genero.F, null,
                                                LocalDate.of(2003, 5, 20));
                                alumnoJuan = createUsuario(usuarioRepository, passwordEncoder, "57777", "Juan", "Perez",
                                                "33333335",
                                                "juan@sysacad.com", RolUsuario.ESTUDIANTE, Genero.M, null,
                                                LocalDate.of(2004, 1, 10));
                                alumnoSofia = createUsuario(usuarioRepository, passwordEncoder, "58888", "Sofia",
                                                "Lopez", "33333336",
                                                "sofia@sysacad.com", RolUsuario.ESTUDIANTE, Genero.F, null,
                                                LocalDate.of(2001, 7, 8));
                                alumnoMiguel = createUsuario(usuarioRepository, passwordEncoder, "59999", "Miguel",
                                                "Torres",
                                                "33333337", "miguel@sysacad.com", RolUsuario.ESTUDIANTE, Genero.M, null,
                                                LocalDate.of(2004, 3, 30));
                                alumnoLucia = createUsuario(usuarioRepository, passwordEncoder, "60001", "Lucia",
                                                "Fernandez",
                                                "33333338", "lucia@sysacad.com", RolUsuario.ESTUDIANTE, Genero.F, null,
                                                LocalDate.of(2002, 12, 12));
                                alumnoCarlos = createUsuario(usuarioRepository, passwordEncoder, "60002", "Carlos",
                                                "Tevez", "33333339",
                                                "carlos@sysacad.com", RolUsuario.ESTUDIANTE, Genero.M, null,
                                                LocalDate.of(2003, 2, 5));

                                System.out.println(">> Usuarios creados con éxito.");
                        } else {
                                // Recuperar referencias si ya existían (para recargas)

                                profeNicolas = usuarioRepository.findByLegajo("51111").orElse(null);
                                profeLaura = usuarioRepository.findByLegajo("52222").orElse(null);
                                profeRoberto = usuarioRepository.findByLegajo("53333").orElse(null);
                                profeAna = usuarioRepository.findByLegajo("54444").orElse(null);
                                profeSandra = usuarioRepository.findByLegajo("55551").orElse(null);
                                profeCristian = usuarioRepository.findByLegajo("55552").orElse(null);
                                alumnoAgustin = usuarioRepository.findByLegajo("55555").orElse(null);
                                alumnoMaria = usuarioRepository.findByLegajo("56666").orElse(null);
                                alumnoJuan = usuarioRepository.findByLegajo("57777").orElse(null);
                                alumnoSofia = usuarioRepository.findByLegajo("58888").orElse(null);
                                alumnoMiguel = usuarioRepository.findByLegajo("59999").orElse(null);
                                alumnoLucia = usuarioRepository.findByLegajo("60001").orElse(null);
                                alumnoCarlos = usuarioRepository.findByLegajo("60002").orElse(null);
                        }

                        // -----------------------------------------------------------------------------------------
                        // NUEVO: MATRICULACIÓN DE ALUMNOS EN CARRERAS (Esencial para que vean sus
                        // materias)
                        // -----------------------------------------------------------------------------------------
                        if (matriculacionRepository.count() == 0) {
                                System.out.println(">> DbSeeder: Matriculando alumnos de prueba en carreras...");

                                // Necesitamos la facultad creada por UTNSeeder
                                FacultadRegional frro = facultadRepository.findAll().stream()
                                                .filter(f -> f.getCiudad().equalsIgnoreCase("Rosario"))
                                                .findFirst()
                                                .orElseThrow(() -> new RuntimeException(
                                                                "Error: Facultad Rosario no encontrada (UTNSeeder falló?)."));

                                // Matriculamos a la mayoría en Sistemas (ISI) - Plan 2023
                                matricularAlumno(matriculacionRepository, alumnoAgustin, frro, "ISI", "Plan 2023");
                                matricularAlumno(matriculacionRepository, alumnoSofia, frro, "ISI", "Plan 2023");
                                matricularAlumno(matriculacionRepository, alumnoCarlos, frro, "ISI", "Plan 2023");
                                matricularAlumno(matriculacionRepository, alumnoMaria, frro, "ISI", "Plan 2023");

                                // Matriculamos a algunos en otras carreras para variedad
                                matricularAlumno(matriculacionRepository, alumnoJuan, frro, "IC", "Plan 2023"); // Civil
                                matricularAlumno(matriculacionRepository, alumnoMiguel, frro, "IEE", "Plan 2023"); // Electrica

                                System.out.println(">> Alumnos matriculados exitosamente.");
                        }

                        if (comisionRepository.count() == 0 && profeNicolas != null) {
                                System.out.println(">> DbSeeder: Desplegando infraestructura de cursada completa...");

                                FacultadRegional frro = facultadRepository.findAll().stream()
                                                .filter(f -> f.getCiudad().equalsIgnoreCase("Rosario"))
                                                .findFirst()
                                                .orElseThrow(() -> new RuntimeException(
                                                                "Error: Facultad Rosario no encontrada."));

                                // SALONES
                                Salon lab305 = createSalon(salonRepository, frro, "Lab. Computación 305", "3");
                                Salon aula401 = createSalon(salonRepository, frro, "Aula 401", "4");
                                Salon aula402 = createSalon(salonRepository, frro, "Aula 402", "4");
                                Salon aula101 = createSalon(salonRepository, frro, "Aula 101", "1"); // PB
                                Salon aula301 = createSalon(salonRepository, frro, "Aula 301", "3");
                                Salon sum = createSalon(salonRepository, frro, "SUM", "1");
                                Salon aula201 = createSalon(salonRepository, frro, "Aula 201", "2");

                                // MATERIAS 1er AÑO
                                Materia algoritmos = getMateria(materiaRepository, "Algoritmos y Estructuras de Datos");
                                Materia sistemas = getMateria(materiaRepository, "Sistemas y Procesos de Negocio");
                                Materia ingles1 = getMateria(materiaRepository, "Inglés I");
                                Materia analisis1 = getMateria(materiaRepository, "Análisis Matemático I");
                                Materia fisica1 = getMateria(materiaRepository, "Física I");
                                Materia algebra = getMateria(materiaRepository, "Álgebra y Geometría Analítica");
                                Materia arquitectura = getMateria(materiaRepository, "Arquitectura de Computadoras");

                                // MATERIAS 2do AÑO
                                Materia analisis2 = getMateria(materiaRepository, "Análisis Matemático II");
                                Materia sintaxis = getMateria(materiaRepository,
                                                "Sintaxis y Semántica de los Lenguajes");
                                Materia paradigmas = getMateria(materiaRepository, "Paradigmas de Programación");
                                Materia sistemasOp = getMateria(materiaRepository, "Sistemas Operativos");

                                // MATERIAS 3er AÑO
                                Materia disenio = getMateria(materiaRepository, "Diseño de Sistemas de Información");
                                Materia basesDatos = getMateria(materiaRepository, "Bases de Datos");
                                Materia analisisNumerico = getMateria(materiaRepository, "Análisis Numérico"); // Agregada
                                                                                                               // para
                                                                                                               // Cristian

                                // ASIGNACIONES DOCENTES
                                asignarCargo(asignacionMateriaRepository, profeNicolas, algoritmos,
                                                RolCargo.JEFE_CATEDRA);
                                asignarCargo(asignacionMateriaRepository, profeNicolas, basesDatos,
                                                RolCargo.JEFE_CATEDRA);
                                asignarCargo(asignacionMateriaRepository, profeNicolas, sintaxis,
                                                RolCargo.JEFE_CATEDRA);
                                asignarCargo(asignacionMateriaRepository, profeSandra, analisis1,
                                                RolCargo.JEFE_CATEDRA);
                                asignarCargo(asignacionMateriaRepository, profeSandra, algebra, RolCargo.JEFE_CATEDRA);
                                asignarCargo(asignacionMateriaRepository, profeCristian, analisis2,
                                                RolCargo.JEFE_CATEDRA);
                                asignarCargo(asignacionMateriaRepository, profeCristian, analisisNumerico,
                                                RolCargo.JEFE_CATEDRA);
                                asignarCargo(asignacionMateriaRepository, profeRoberto, fisica1, RolCargo.JEFE_CATEDRA);
                                asignarCargo(asignacionMateriaRepository, profeAna, ingles1, RolCargo.JEFE_CATEDRA);

                                // COMISIONES Y HORARIOS

                                Comision c1k1 = createComision(comisionRepository, "1K1", 1, "MAÑANA", lab305,
                                                Arrays.asList(algoritmos, sistemas, ingles1, algebra),
                                                List.of(profeNicolas, profeAna, profeSandra));

                                crearHorario(horarioCursadoRepository, c1k1, algoritmos, DiaSemana.LUNES, 8, 12);
                                crearHorario(horarioCursadoRepository, c1k1, sistemas, DiaSemana.MARTES, 10, 13);
                                crearHorario(horarioCursadoRepository, c1k1, algebra, DiaSemana.JUEVES, 8, 12);
                                crearHorario(horarioCursadoRepository, c1k1, ingles1, DiaSemana.VIERNES, 8, 10);

                                Comision c1k2 = createComision(comisionRepository, "1K2", 1, "NOCHE", aula401,
                                                Arrays.asList(algoritmos, analisis1, fisica1, arquitectura),
                                                List.of(profeNicolas, profeSandra, profeRoberto));

                                crearHorario(horarioCursadoRepository, c1k2, arquitectura, DiaSemana.LUNES, 18, 22);
                                crearHorario(horarioCursadoRepository, c1k2, algoritmos, DiaSemana.MIERCOLES, 18, 22);
                                crearHorario(horarioCursadoRepository, c1k2, analisis1, DiaSemana.JUEVES, 19, 23);
                                crearHorario(horarioCursadoRepository, c1k2, fisica1, DiaSemana.VIERNES, 18, 22);

                                Comision c2k1 = createComision(comisionRepository, "2K1", 2, "TARDE", aula201,
                                                Arrays.asList(analisis2, sintaxis, paradigmas, sistemasOp),
                                                List.of(profeCristian, profeNicolas));

                                crearHorario(horarioCursadoRepository, c2k1, analisis2, DiaSemana.LUNES, 14, 18);
                                crearHorario(horarioCursadoRepository, c2k1, sintaxis, DiaSemana.MARTES, 14, 18);
                                crearHorario(horarioCursadoRepository, c2k1, paradigmas, DiaSemana.MIERCOLES, 14, 18);
                                crearHorario(horarioCursadoRepository, c2k1, sistemasOp, DiaSemana.VIERNES, 14, 18);

                                Comision c3k1 = createComision(comisionRepository, "3K1", 3, "NOCHE", lab305,
                                                Arrays.asList(disenio, basesDatos, analisisNumerico),
                                                List.of(profeNicolas, profeCristian));

                                crearHorario(horarioCursadoRepository, c3k1, basesDatos, DiaSemana.MARTES, 18, 22);
                                crearHorario(horarioCursadoRepository, c3k1, disenio, DiaSemana.JUEVES, 18, 22);
                                crearHorario(horarioCursadoRepository, c3k1, analisisNumerico, DiaSemana.VIERNES, 18,
                                                21); // Cristian

                                // INSCRIPCIONES Y NOTAS

                                if (alumnoAgustin != null) {
                                        inscribirAlumno(inscripcionRepository, alumnoAgustin, c1k1);
                                        inscribirAlumno(inscripcionRepository, alumnoAgustin, c2k1);

                                        cargarNota(calificacionRepository, inscripcionRepository, alumnoAgustin, c1k1,
                                                        "1er Parcial - " + algoritmos.getNombre(), "8.50");
                                        cargarNota(calificacionRepository, inscripcionRepository, alumnoAgustin, c1k1,
                                                        "TP " + ingles1.getNombre(), "9.00");
                                }

                                if (alumnoSofia != null) {
                                        inscribirAlumno(inscripcionRepository, alumnoSofia, c1k1);
                                        cargarNota(calificacionRepository, inscripcionRepository, alumnoSofia, c1k1,
                                                        "Final " + algoritmos.getNombre(),
                                                        "9.00");
                                        cargarNota(calificacionRepository, inscripcionRepository, alumnoSofia, c1k1,
                                                        "Final " + sistemas.getNombre(),
                                                        "8.00");
                                        cargarNota(calificacionRepository, inscripcionRepository, alumnoSofia, c1k1,
                                                        "Final " + ingles1.getNombre(),
                                                        "10.00");
                                        cargarNota(calificacionRepository, inscripcionRepository, alumnoSofia, c1k1,
                                                        "Final " + algebra.getNombre(),
                                                        "7.50");

                                        inscribirAlumno(inscripcionRepository, alumnoSofia, c1k2);
                                        cargarNota(calificacionRepository, inscripcionRepository, alumnoSofia, c1k2,
                                                        "Final " + analisis1.getNombre(), "8.00");
                                        cargarNota(calificacionRepository, inscripcionRepository, alumnoSofia, c1k2,
                                                        "Final " + fisica1.getNombre(),
                                                        "9.50");

                                        inscribirAlumno(inscripcionRepository, alumnoSofia, c2k1);
                                        cargarNota(calificacionRepository, inscripcionRepository, alumnoSofia, c2k1,
                                                        "Final " + analisis2.getNombre(), "7.00");
                                        cargarNota(calificacionRepository, inscripcionRepository, alumnoSofia, c2k1,
                                                        "Final " + sintaxis.getNombre(),
                                                        "10.00");
                                        cargarNota(calificacionRepository, inscripcionRepository, alumnoSofia, c2k1,
                                                        "Final " + paradigmas.getNombre(),
                                                        "9.00");

                                        inscribirAlumno(inscripcionRepository, alumnoSofia, c3k1);
                                        cargarNota(calificacionRepository, inscripcionRepository, alumnoSofia, c3k1,
                                                        "TP Normalización " + basesDatos.getNombre(),
                                                        "7.50");
                                }

                                if (alumnoCarlos != null) {
                                        inscribirAlumno(inscripcionRepository, alumnoCarlos, c1k1);
                                        cargarNota(calificacionRepository, inscripcionRepository, alumnoCarlos, c1k1,
                                                        "Final " + algoritmos.getNombre(),
                                                        "6.00");
                                        cargarNota(calificacionRepository, inscripcionRepository, alumnoCarlos, c1k1,
                                                        "Final " + sistemas.getNombre(),
                                                        "7.00");

                                        inscribirAlumno(inscripcionRepository, alumnoCarlos, c2k1);
                                        cargarNota(calificacionRepository, inscripcionRepository, alumnoCarlos, c2k1,
                                                        "1er Parcial " + sintaxis.getNombre(), "4.00");
                                }

                                if (alumnoLucia != null) {
                                        inscribirAlumno(inscripcionRepository, alumnoLucia, c2k1);
                                        cargarNota(calificacionRepository, inscripcionRepository, alumnoLucia, c2k1,
                                                        "1er Parcial " + analisis2.getNombre(), "2.00");
                                }

                                if (alumnoMaria != null) {
                                        inscribirAlumno(inscripcionRepository, alumnoMaria, c1k1);
                                        cargarNota(calificacionRepository, inscripcionRepository, alumnoMaria, c1k1,
                                                        "1er Parcial - " + algoritmos.getNombre(), "10.00");
                                }

                                if (alumnoJuan != null) {
                                        inscribirAlumno(inscripcionRepository, alumnoJuan, c1k2);
                                }

                                if (alumnoMiguel != null) {
                                        inscribirAlumno(inscripcionRepository, alumnoMiguel, c1k2);
                                }

                                System.out
                                                .println(">> Seeding Académico Finalizado: 4 Comisiones, Múltiples Horarios y Notas cargadas.");
                        }

                        if (mesaExamenRepository.count() == 0) {
                                System.out.println(">> DbSeeder: Creando Mesas de Examen...");

                                // 1. Crear Mesa
                                MesaExamen mesaFeb = new MesaExamen();
                                mesaFeb.setNombre("Turno Febrero 2026");
                                mesaFeb.setFechaInicio(LocalDate.of(2026, 2, 1));
                                mesaFeb.setFechaFin(LocalDate.of(2026, 2, 28));
                                mesaExamenRepository.save(mesaFeb);

                                // 2. Crear Detalle (Materia en Mesa)
                                Materia algoritmos = materiaRepository.findByNombre("Algoritmos y Estructuras de Datos")
                                                .orElseThrow();

                                DetalleMesaExamen detalleAlgo = new DetalleMesaExamen();
                                detalleAlgo.setMesaExamen(mesaFeb);
                                detalleAlgo.setMateria(algoritmos);
                                detalleAlgo.setDiaExamen(LocalDate.of(2026, 2, 10));
                                detalleAlgo.setHoraExamen(LocalTime.of(9, 0));
                                detalleMesaExamenRepository.save(detalleAlgo);

                                // 3. Inscribir Alumno a Examen
                                if (alumnoAgustin != null) {
                                        InscripcionExamen inscExamen = new InscripcionExamen();
                                        inscExamen.setUsuario(alumnoAgustin);
                                        inscExamen.setDetalleMesaExamen(detalleAlgo);
                                        inscExamen.setFechaInscripcion(LocalDateTime.now());
                                        inscExamen.setEstado("PENDIENTE");
                                        inscripcionExamenRepository.save(inscExamen);
                                        System.out.println(
                                                        ">> Alumno inscripto a examen: " + alumnoAgustin.getNombre());
                                }

                                System.out.println(">> Mesa creada: " + mesaFeb.getNombre());
                        }
                };
        }

        // --- HELPERS ---

        private void matricularAlumno(MatriculacionRepository repo, Usuario alumno, FacultadRegional facu,
                        String carrera, String plan) {
                if (alumno == null)
                        return;
                Matriculacion eu = new Matriculacion();
                Matriculacion.MatriculacionId id = new Matriculacion.MatriculacionId(alumno.getId(), facu.getId(),
                                carrera, plan);
                eu.setId(id);
                eu.setFechaInscripcion(LocalDate.now());
                eu.setEstado("ACTIVO");
                repo.save(eu);
        }

        private Usuario createUsuario(UsuarioRepository repo, PasswordEncoder encoder, String legajo, String nombre,
                        String apellido, String dni, String mail, RolUsuario rol, Genero genero, String titulo,
                        LocalDate fechaNacimiento) {
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
                u.setFechaNacimiento(fechaNacimiento != null ? fechaNacimiento : LocalDate.of(1995, 1, 1));
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

        private Comision createComision(ComisionRepository repo, String nombre, Integer anio, String turno, Salon salon,
                        List<Materia> materias, List<Usuario> profes) {
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

        private void crearHorario(HorarioCursadoRepository repo, Comision com, Materia mat, DiaSemana dia, int hDesde,
                        int hHasta) {
                HorarioCursado horario = new HorarioCursado();
                HorarioCursado.HorarioCursadoId id = new HorarioCursado.HorarioCursadoId(
                                com.getId(), mat.getId(), dia, LocalTime.of(hDesde, 0));
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

        private void cargarNota(CalificacionRepository califRepo, InscripcionRepository inscRepo, Usuario alumno,
                        Comision comision, String concepto, String valor) {
                Inscripcion insc = inscRepo.findByIdIdUsuarioAndIdIdComision(alumno.getId(), comision.getId())
                                .stream().findFirst()
                                .orElseThrow(() -> new RuntimeException("No se encontró inscripción para cargar nota"));

                Calificacion nota = new Calificacion();
                CalificacionId id = new CalificacionId(
                                insc.getId().getIdUsuario(),
                                insc.getId().getIdComision(),
                                insc.getId().getTipo(),
                                insc.getId().getVecesTipo(),
                                concepto);
                nota.setId(id);
                nota.setNota(new BigDecimal(valor));
                nota.setInscripcion(insc);
                califRepo.save(nota);
        }
}