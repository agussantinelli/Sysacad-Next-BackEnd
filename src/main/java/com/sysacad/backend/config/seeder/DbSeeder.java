package com.sysacad.backend.config.seeder;

import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.modelo.Matriculacion;
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
                        CarreraRepository carreraRepository, // NEW injected
                        PasswordEncoder passwordEncoder,
                        FacultadRegionalRepository facultadRepository,
                        SalonRepository salonRepository,
                        MateriaRepository materiaRepository,
                        ComisionRepository comisionRepository,
                        AsignacionMateriaRepository asignacionMateriaRepository,
                        HorarioCursadoRepository horarioCursadoRepository,
                        MesaExamenRepository mesaExamenRepository,
                        DetalleMesaExamenRepository detalleMesaExamenRepository,
                        InscripcionExamenRepository inscripcionExamenRepository,
                        InscripcionCursadoRepository inscripcionCursadoRepository, // NEW
                        CalificacionCursadaRepository calificacionCursadaRepository // NEW
        ) {

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
                        Usuario alumnoMartin = null;
                        Usuario alumnoFlavia = null;

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
                                profeSandra = createUsuario(usuarioRepository, passwordEncoder, "55551", "Sandra",
                                                "Civiero",
                                                "22222226", "sandra@sysacad.com", RolUsuario.PROFESOR, Genero.F,
                                                "Matemática",
                                                LocalDate.of(1975, 4, 12));
                                profeCristian = createUsuario(usuarioRepository, passwordEncoder, "55552", "Cristian",
                                                "Milone",
                                                "22222227", "cristian@sysacad.com", RolUsuario.PROFESOR, Genero.M,
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
                                alumnoMartin = createUsuario(usuarioRepository, passwordEncoder, "60003", "Martin",
                                                "Palermo", "33333340",
                                                "martin@sysacad.com", RolUsuario.ESTUDIANTE, Genero.M, null,
                                                LocalDate.of(2003, 11, 7));
                                alumnoFlavia = createUsuario(usuarioRepository, passwordEncoder, "60004", "Flavia",
                                                "Avara", "33333341",
                                                "flavia@sysacad.com", RolUsuario.ESTUDIANTE, Genero.F, null,
                                                LocalDate.of(2004, 11, 24));

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
                                alumnoMartin = usuarioRepository.findByLegajo("60003").orElse(null);
                                alumnoFlavia = usuarioRepository.findByLegajo("60004").orElse(null);
                        }
                        
                        if (matriculacionRepository.count() == 0) {
                                System.out.println(">> DbSeeder: Matriculando alumnos de prueba en carreras...");

                                // Necesitamos la facultad creada por UTNSeeder
                                FacultadRegional frro = facultadRepository.findAll().stream()
                                                .filter(f -> f.getCiudad().equalsIgnoreCase("Rosario"))
                                                .findFirst()
                                                .orElseThrow(() -> new RuntimeException(
                                                                "Error: Facultad Rosario no encontrada (UTNSeeder falló?)."));

                                // Recuperar Carreras por Alias (asumiendo unicidad por alias en seeder)
                                List<Carrera> todasCarreras = carreraRepository.findAll();
                                Carrera carreraISI = todasCarreras.stream().filter(c -> "ISI".equals(c.getAlias())).findFirst().orElse(null);
                                Carrera carreraCivil = todasCarreras.stream().filter(c -> "IC".equals(c.getAlias())).findFirst().orElse(null);
                                Carrera carreraElectrica = todasCarreras.stream().filter(c -> "IEE".equals(c.getAlias())).findFirst().orElse(null);

                                if (carreraISI != null) {
                                    // Matriculamos a la mayoría en Sistemas (ISI) - Plan 2023
                                    matricularAlumno(matriculacionRepository, alumnoAgustin, frro, carreraISI.getId(), 2023);
                                    matricularAlumno(matriculacionRepository, alumnoSofia, frro, carreraISI.getId(), 2023);
                                    matricularAlumno(matriculacionRepository, alumnoCarlos, frro, carreraISI.getId(), 2023);
                                    matricularAlumno(matriculacionRepository, alumnoMaria, frro, carreraISI.getId(), 2023);
                                    matricularAlumno(matriculacionRepository, alumnoMartin, frro, carreraISI.getId(), 2023);
                                    matricularAlumno(matriculacionRepository, alumnoFlavia, frro, carreraISI.getId(), 2023);
                                }
                                
                                if (carreraCivil != null) {
                                    matricularAlumno(matriculacionRepository, alumnoJuan, frro, carreraCivil.getId(), 2023); // Civil
                                }
                                
                                if (carreraElectrica != null) {
                                    matricularAlumno(matriculacionRepository, alumnoMiguel, frro, carreraElectrica.getId(), 2023); // Electrica
                                }

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

                                // INSCRIPCIONES Y NOTAS (NUEVO SISTEMA)

                                if (alumnoAgustin != null) {
                                        // 1K1 -> Algoritmos, Sistemas, Ingles, Algebra
                                        var insc = inscribirCursado(inscripcionCursadoRepository, alumnoAgustin, c1k1,
                                                        algoritmos);
                                        insc.setNotaFinal(new BigDecimal("9.00"));
                                        insc.setEstado(EstadoCursada.PROMOCIONADO);
                                        insc.setFechaPromocion(LocalDate.now());
                                        insc.setTomo("LIB-001");
                                        insc.setFolio("123");
                                        inscripcionCursadoRepository.save(insc);

                                        var inscSist = inscribirCursado(inscripcionCursadoRepository, alumnoAgustin,
                                                        c1k1, sistemas);
                                        inscSist.setNotaFinal(new BigDecimal("8.00"));
                                        inscSist.setEstado(EstadoCursada.PROMOCIONADO);
                                        inscSist.setFechaPromocion(LocalDate.now());
                                        inscSist.setTomo("LIB-001");
                                        inscSist.setFolio("124");
                                        inscripcionCursadoRepository.save(inscSist);

                                        var inscEng = inscribirCursado(inscripcionCursadoRepository, alumnoAgustin,
                                                        c1k1, ingles1);
                                        inscEng.setNotaFinal(new BigDecimal("10.00"));
                                        inscEng.setEstado(EstadoCursada.PROMOCIONADO);
                                        inscEng.setFechaPromocion(LocalDate.now());
                                        inscEng.setTomo("LIB-001");
                                        inscEng.setFolio("125");
                                        inscripcionCursadoRepository.save(inscEng);

                                        // 2K1 -> Analisis II, Sintaxis, Paradigmas, Sistemas Op
                                        inscribirCursado(inscripcionCursadoRepository, alumnoAgustin, c2k1, analisis2);
                                        // inscribirCursado(inscripcionCursadoRepository, alumnoAgustin, c2k1, ingles1);
                                        // // NO ES DE 2K1

                                        // Algebra (1K1)
                                        var inscAlg = inscribirCursado(inscripcionCursadoRepository, alumnoAgustin,
                                                        c1k1, algebra);
                                        inscAlg.setNotaFinal(new BigDecimal("6.00"));
                                        inscAlg.setEstado(EstadoCursada.REGULAR);
                                        inscAlg.setFechaRegularidad(LocalDate.now()); // Asumimos regularidad obtenida hoy o antes
                                        inscripcionCursadoRepository.save(inscAlg);

                                        // Analisis 1 (1K2 - aunque sea de otro turno, lo inscribimos para que tenga la materia)
                                        var inscAn1 = inscribirCursado(inscripcionCursadoRepository, alumnoAgustin,
                                                        c1k2, analisis1);
                                        inscAn1.setNotaFinal(new BigDecimal("6.00"));
                                        inscAn1.setEstado(EstadoCursada.REGULAR);
                                        inscAn1.setFechaRegularidad(LocalDate.now());
                                        inscripcionCursadoRepository.save(inscAn1);
                                }

                                if (alumnoSofia != null) {
                                        // 1K1
                                        var inscAlgo = inscribirCursado(inscripcionCursadoRepository, alumnoSofia, c1k1,
                                                        algoritmos);
                                        inscAlgo.setNotaFinal(new BigDecimal("9.00"));
                                        inscAlgo.setEstado(EstadoCursada.PROMOCIONADO);
                                        inscAlgo.setFechaPromocion(LocalDate.now());
                                        inscAlgo.setTomo("LIB-002");
                                        inscAlgo.setFolio("201");
                                        inscripcionCursadoRepository.save(inscAlgo);

                                        var inscSis = inscribirCursado(inscripcionCursadoRepository, alumnoSofia, c1k1,
                                                        sistemas);
                                        inscSis.setNotaFinal(new BigDecimal("8.00"));
                                        inscSis.setEstado(EstadoCursada.PROMOCIONADO);
                                        inscSis.setTomo("LIB-002");
                                        inscSis.setFolio("202");
                                        inscripcionCursadoRepository.save(inscSis);

                                        var inscIng = inscribirCursado(inscripcionCursadoRepository, alumnoSofia, c1k1,
                                                        ingles1);
                                        inscIng.setNotaFinal(new BigDecimal("10.00"));
                                        inscIng.setEstado(EstadoCursada.PROMOCIONADO);
                                        inscIng.setTomo("LIB-002");
                                        inscIng.setFolio("203");
                                        inscripcionCursadoRepository.save(inscIng);

                                        var inscAlg = inscribirCursado(inscripcionCursadoRepository, alumnoSofia, c1k1,
                                                        algebra);
                                        inscAlg.setNotaFinal(new BigDecimal("7.50"));
                                        inscAlg.setEstado(EstadoCursada.PROMOCIONADO);
                                        inscAlg.setTomo("LIB-002");
                                        inscAlg.setFolio("204");
                                        inscripcionCursadoRepository.save(inscAlg);

                                        // 1K2 -> Analisis 1, Fisica 1
                                        var inscAn1 = inscribirCursado(inscripcionCursadoRepository, alumnoSofia, c1k2,
                                                        analisis1);
                                        inscAn1.setNotaFinal(new BigDecimal("8.00"));
                                        inscAn1.setEstado(EstadoCursada.REGULAR);
                                        inscripcionCursadoRepository.save(inscAn1);

                                        var inscFis1 = inscribirCursado(inscripcionCursadoRepository, alumnoSofia, c1k2,
                                                        fisica1);
                                        inscFis1.setNotaFinal(new BigDecimal("9.50"));
                                        inscFis1.setEstado(EstadoCursada.PROMOCIONADO);
                                        inscFis1.setTomo("LIB-002");
                                        inscFis1.setFolio("205");
                                        inscripcionCursadoRepository.save(inscFis1);

                                        // 2K1
                                        var inscAn2 = inscribirCursado(inscripcionCursadoRepository, alumnoSofia, c2k1,
                                                        analisis2);
                                        inscAn2.setNotaFinal(new BigDecimal("7.00"));
                                        inscripcionCursadoRepository.save(inscAn2);

                                        var inscSin = inscribirCursado(inscripcionCursadoRepository, alumnoSofia, c2k1,
                                                        sintaxis);
                                        inscSin.setNotaFinal(new BigDecimal("10.00"));
                                        inscripcionCursadoRepository.save(inscSin);
                                }

                                if (alumnoCarlos != null) {
                                        var inscAlgo = inscribirCursado(inscripcionCursadoRepository, alumnoCarlos,
                                                        c1k1, algoritmos);
                                        inscAlgo.setNotaFinal(new BigDecimal("6.00"));
                                        inscripcionCursadoRepository.save(inscAlgo);

                                        var inscSis = inscribirCursado(inscripcionCursadoRepository, alumnoCarlos, c1k1,
                                                        sistemas);
                                        inscSis.setNotaFinal(new BigDecimal("7.00"));
                                        inscripcionCursadoRepository.save(inscSis);

                                        var inscSin = inscribirCursado(inscripcionCursadoRepository, alumnoCarlos, c2k1,
                                                        sintaxis);
                                        cargarNotaCursada(calificacionCursadaRepository, inscSin, "1er Parcial",
                                                        "4.00");
                                }

                                if (alumnoLucia != null) {
                                        var inscAn2 = inscribirCursado(inscripcionCursadoRepository, alumnoLucia, c2k1,
                                                        analisis2);
                                        cargarNotaCursada(calificacionCursadaRepository, inscAn2, "1er Parcial",
                                                        "2.00");
                                }

                                if (alumnoMaria != null) {
                                        var inscAlgo = inscribirCursado(inscripcionCursadoRepository, alumnoMaria, c1k1,
                                                        algoritmos);
                                        cargarNotaCursada(calificacionCursadaRepository, inscAlgo, "1er Parcial",
                                                        "10.00");
                                }

                                // Juan y Miguel solo inscriptos
                                if (alumnoJuan != null) {
                                        inscribirCursado(inscripcionCursadoRepository, alumnoJuan, c1k2, analisis1);
                                }

                                if (alumnoMiguel != null) {
                                        inscribirCursado(inscripcionCursadoRepository, alumnoMiguel, c1k2, fisica1);
                                }

                                if (alumnoMartin != null) {
                                        var insc = inscribirCursado(inscripcionCursadoRepository, alumnoMartin, c1k1,
                                                        algoritmos);
                                        cargarNotaCursada(calificacionCursadaRepository, insc, "1er Parcial", "2.00");
                                }

                                if (alumnoFlavia != null) {
                                        inscribirCursado(inscripcionCursadoRepository, alumnoFlavia, c1k1, sistemas);
                                }

                                System.out
                                                .println(">> Seeding Académico Finalizado: 4 Comisiones, Múltiples Horarios y Notas cargadas (Nueva Estructura).");
                        }

                        if (mesaExamenRepository.count() == 0) {
                                System.out.println(">> DbSeeder: Desplegando Mesas de Examen...");

                                // 1. Crear Turnos (Mesas)
                                MesaExamen mesaFeb = createMesa(mesaExamenRepository, "Turno Febrero 2026",
                                                LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28));
                                MesaExamen mesaJul = createMesa(mesaExamenRepository, "Turno Julio 2026",
                                                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 31));
                                MesaExamen mesaDic = createMesa(mesaExamenRepository, "Turno Diciembre 2026",
                                                LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 22));

                                // 2. Agregar Materias a las Mesas (Detalles)
                                Materia algoritmos = getMateria(materiaRepository, "Algoritmos y Estructuras de Datos");
                                Materia sistemas = getMateria(materiaRepository, "Sistemas y Procesos de Negocio");
                                Materia analisis1 = getMateria(materiaRepository, "Análisis Matemático I");
                                Materia fisica1 = getMateria(materiaRepository, "Física I");
                                Materia sintaxis = getMateria(materiaRepository,
                                                "Sintaxis y Semántica de los Lenguajes");

                                // Febrero
                                DetalleMesaExamen febAlgo = createDetalleMesa(detalleMesaExamenRepository, mesaFeb, 1,
                                                algoritmos, profeNicolas, LocalDate.of(2026, 2, 10),
                                                LocalTime.of(9, 0));
                                DetalleMesaExamen febSistemas = createDetalleMesa(detalleMesaExamenRepository, mesaFeb,
                                                2,
                                                sistemas, profeNicolas, LocalDate.of(2026, 2, 12), LocalTime.of(14, 0));
                                DetalleMesaExamen febAnalisis = createDetalleMesa(detalleMesaExamenRepository, mesaFeb,
                                                3,
                                                analisis1, profeSandra, LocalDate.of(2026, 2, 15), LocalTime.of(9, 0));

                                // Julio
                                DetalleMesaExamen julSintaxis = createDetalleMesa(detalleMesaExamenRepository, mesaJul,
                                                1,
                                                sintaxis, profeNicolas, LocalDate.of(2026, 7, 10), LocalTime.of(9, 0));
                                DetalleMesaExamen julFisica = createDetalleMesa(detalleMesaExamenRepository, mesaJul, 2,
                                                fisica1, profeRoberto, LocalDate.of(2026, 7, 15), LocalTime.of(16, 0));

                                // 3. Inscribir Alumnos a Examenes
                                if (alumnoAgustin != null) {
                                        inscribirExamen(inscripcionExamenRepository, alumnoAgustin, febAlgo);
                                        inscribirExamen(inscripcionExamenRepository, alumnoAgustin, julSintaxis);
                                }

                                if (alumnoSofia != null) {
                                        inscribirExamen(inscripcionExamenRepository, alumnoSofia, febSistemas);
                                        var insc = inscribirExamen(inscripcionExamenRepository, alumnoSofia,
                                                        febAnalisis);
                                        // Simulamos corrección
                                        insc.setNota(new BigDecimal("9.00"));
                                        insc.setEstado(EstadoExamen.APROBADO);
                                        insc.setTomo("ACTA-feb");
                                        insc.setFolio("042");
                                        inscripcionExamenRepository.save(insc);
                                }

                                if (alumnoCarlos != null) {
                                        inscribirExamen(inscripcionExamenRepository, alumnoCarlos, julFisica);
                                }

                                System.out.println(">> Mesas de Examen creadas exitosamente.");
                        }
                };
        }

        private void matricularAlumno(MatriculacionRepository repo, Usuario alumno, FacultadRegional facu,
                        java.util.UUID idCarrera, Integer nroPlan) {
                if (alumno == null)
                        return;
                Matriculacion eu = new Matriculacion();
                Matriculacion.MatriculacionId id = new Matriculacion.MatriculacionId(alumno.getId(), facu.getId(),
                                idCarrera, nroPlan);
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

        // --- NUEVOS HELPERS PARA INSCRIPCION CURSADO ---

        private InscripcionCursado inscribirCursado(InscripcionCursadoRepository repo, Usuario alumno,
                        Comision comision, Materia materia) {
                // Verificar que la comisión tenga esa materia (simple check)
                boolean materiaEnComision = comision.getMaterias().stream()
                                .anyMatch(m -> m.getId().equals(materia.getId()));
                if (!materiaEnComision) {
                        System.out.println("WARNING: Intentando inscribir en materia " + materia.getNombre()
                                        + " no presente en comisión " + comision.getNombre());
                }

                InscripcionCursado insc = new InscripcionCursado();
                insc.setUsuario(alumno);
                insc.setComision(comision);
                insc.setMateria(materia);
                insc.setFechaInscripcion(LocalDateTime.now());
                insc.setEstado(EstadoCursada.CURSANDO);
                return repo.save(insc);
        }

        private void cargarNotaCursada(CalificacionCursadaRepository repo, InscripcionCursado insc, String descripcion,
                        String valor) {
                CalificacionCursada calif = new CalificacionCursada();
                calif.setInscripcionCursado(insc);
                calif.setDescripcion(descripcion);
                calif.setNota(new BigDecimal(valor));
                calif.setFecha(LocalDate.now());
                repo.save(calif);
        }

        private MesaExamen createMesa(MesaExamenRepository repo, String nombre, LocalDate inicio, LocalDate fin) {
                MesaExamen mesa = new MesaExamen();
                mesa.setNombre(nombre);
                mesa.setFechaInicio(inicio);
                mesa.setFechaFin(fin);
                return repo.save(mesa);
        }

        private DetalleMesaExamen createDetalleMesa(DetalleMesaExamenRepository repo, MesaExamen mesa, Integer nro,
                        Materia materia,
                        Usuario presidente, LocalDate dia, LocalTime hora) {
                DetalleMesaExamen detalle = new DetalleMesaExamen();
                detalle.setId(new DetalleMesaExamen.DetalleId(mesa.getId(), nro));
                detalle.setMesaExamen(mesa);
                detalle.setMateria(materia);
                detalle.setPresidente(presidente);
                detalle.setDiaExamen(dia);
                detalle.setHoraExamen(hora);
                return repo.save(detalle);
        }

        private InscripcionExamen inscribirExamen(InscripcionExamenRepository repo, Usuario alumno,
                        DetalleMesaExamen detalle) {
                InscripcionExamen insc = new InscripcionExamen();
                insc.setUsuario(alumno);
                insc.setDetalleMesaExamen(detalle);
                insc.setFechaInscripcion(LocalDateTime.now());
                insc.setEstado(EstadoExamen.PENDIENTE);
                return repo.save(insc);
        }
}