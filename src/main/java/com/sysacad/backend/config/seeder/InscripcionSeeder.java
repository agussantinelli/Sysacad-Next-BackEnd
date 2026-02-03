package com.sysacad.backend.config.seeder;

import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.enums.EstadoCursada;
import com.sysacad.backend.modelo.enums.EstadoExamen;
import com.sysacad.backend.repository.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class InscripcionSeeder {

    private final InscripcionCursadoRepository inscripcionCursadoRepository;
    private final CalificacionCursadaRepository calificacionCursadaRepository;
    private final MesaExamenRepository mesaExamenRepository;
    private final DetalleMesaExamenRepository detalleMesaExamenRepository;
    private final InscripcionExamenRepository inscripcionExamenRepository;
    private final MateriaRepository materiaRepository;
    private final ComisionRepository comisionRepository;
    private final UsuarioRepository usuarioRepository;

    public InscripcionSeeder(InscripcionCursadoRepository inscripcionCursadoRepository,
                             CalificacionCursadaRepository calificacionCursadaRepository,
                             MesaExamenRepository mesaExamenRepository,
                             DetalleMesaExamenRepository detalleMesaExamenRepository,
                             InscripcionExamenRepository inscripcionExamenRepository,
                             MateriaRepository materiaRepository,
                             ComisionRepository comisionRepository,
                             UsuarioRepository usuarioRepository) {
        this.inscripcionCursadoRepository = inscripcionCursadoRepository;
        this.calificacionCursadaRepository = calificacionCursadaRepository;
        this.mesaExamenRepository = mesaExamenRepository;
        this.detalleMesaExamenRepository = detalleMesaExamenRepository;
        this.inscripcionExamenRepository = inscripcionExamenRepository;
        this.materiaRepository = materiaRepository;
        this.comisionRepository = comisionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public void seed() {
        // Verificar si ya hay inscripciones cargadas
        if (inscripcionCursadoRepository.count() > 0 || inscripcionExamenRepository.count() > 0) {
            System.out.println(">> InscripcionSeeder: OMITIDO - Las inscripciones ya están cargadas.");
            return;
        }

        System.out.println(">> InscripcionSeeder: Iniciando carga de inscripciones y exámenes...");
        
        seedCursado();
        seedExamenes();
    }

    private void seedCursado() {
        if (comisionRepository.count() == 0) return; // Should not happen if run after ComisionSeeder

        System.out.println(">> InscripcionSeeder: Inscribiendo a Cursado...");

        // Retrieve needed entities
        Usuario alumnoAgustin = usuarioRepository.findByLegajo("55555").orElse(null);
        Usuario alumnoSofia = usuarioRepository.findByLegajo("58888").orElse(null);
        Usuario alumnoCarlos = usuarioRepository.findByLegajo("60002").orElse(null);
        Usuario alumnoLucia = usuarioRepository.findByLegajo("60001").orElse(null);
        Usuario alumnoMaria = usuarioRepository.findByLegajo("56666").orElse(null);
        Usuario alumnoJuan = usuarioRepository.findByLegajo("57777").orElse(null);
        Usuario alumnoMiguel = usuarioRepository.findByLegajo("59999").orElse(null);
        Usuario alumnoMartin = usuarioRepository.findByLegajo("60003").orElse(null);
        Usuario alumnoFlavia = usuarioRepository.findByLegajo("60004").orElse(null);
        Usuario alumnoPedro = usuarioRepository.findByLegajo("60010").orElse(null);
        Usuario alumnoLionel = usuarioRepository.findByLegajo("60011").orElse(null);
        Usuario alumnoAlex = usuarioRepository.findByLegajo("60012").orElse(null);
        Usuario alumnoDiego = usuarioRepository.findByLegajo("60013").orElse(null);
        Usuario alumnoEnzo = usuarioRepository.findByLegajo("60014").orElse(null);

        Comision c1k1 = comisionRepository.findByNombreAndAnio("1K1", 2025).stream().findFirst().orElse(null);
        Comision c1k2 = comisionRepository.findByNombreAndAnio("1K2", 2025).stream().findFirst().orElse(null);
        Comision c2k1 = comisionRepository.findByNombreAndAnio("2K1", 2025).stream().findFirst().orElse(null);
        Comision cElectivas = comisionRepository.findByNombreAndAnio("Electivas 2025 - Noche", 2025).stream().findFirst().orElse(null);

        Materia algoritmos = getMateria("Algoritmos y Estructuras de Datos");
        Materia sistemas = getMateria("Sistemas y Procesos de Negocio");
        Materia ingles1 = getMateria("Inglés I");
        Materia algebra = getMateria("Álgebra y Geometría Analítica");
        Materia analisis1 = getMateria("Análisis Matemático I");
        Materia fisica1 = getMateria("Física I");
        Materia arquitectura = getMateria("Arquitectura de Computadoras");
        Materia analisis2 = getMateria("Análisis Matemático II");
        Materia sintaxis = getMateria("Sintaxis y Semántica de los Lenguajes");
        Materia paradigmas = getMateria("Paradigmas de Programación");
        Materia sistemasOp = getMateria("Sistemas Operativos");
        Materia entornos = getMateria("Entornos Gráficos");
        Materia mineria = getMateria("Minería de Datos");
        Materia emprendedores = getMateria("Formación de Emprendedores");

        if (c1k1 != null && alumnoAgustin != null) {
            // --- AÑO 1: TODO APROBADO (PROMOCIONADO) ---
            // Esto habilita cursar/rendir 2do año
            
            crearHistoriaAcademica(alumnoAgustin, c1k1, algoritmos, EstadoCursada.PROMOCIONADO, new BigDecimal("9"));
            crearHistoriaAcademica(alumnoAgustin, c1k1, sistemas, EstadoCursada.PROMOCIONADO, new BigDecimal("8"));
            crearHistoriaAcademica(alumnoAgustin, c1k1, analisis1, EstadoCursada.PROMOCIONADO, new BigDecimal("7"));
            crearHistoriaAcademica(alumnoAgustin, c1k1, algebra, EstadoCursada.PROMOCIONADO, new BigDecimal("8"));
            crearHistoriaAcademica(alumnoAgustin, c1k1, fisica1, EstadoCursada.PROMOCIONADO, new BigDecimal("9"));
            crearHistoriaAcademica(alumnoAgustin, c1k1, ingles1, EstadoCursada.PROMOCIONADO, new BigDecimal("10"));
            crearHistoriaAcademica(alumnoAgustin, c1k1, arquitectura, EstadoCursada.PROMOCIONADO, new BigDecimal("7"));
            
            // Lógica también aprobada
             Materia logica = getMateria("Lógica y Estructuras Discretas");
             crearHistoriaAcademica(alumnoAgustin, c1k1, logica, EstadoCursada.PROMOCIONADO, new BigDecimal("8"));
        }
        
        if (c2k1 != null && alumnoAgustin != null) {
            // --- AÑO 2: ALGUNAS APROBADAS, OTRAS REGULARES ---
            
            // Aprobadas (Promocionadas)
            crearHistoriaAcademica(alumnoAgustin, c2k1, analisis2, EstadoCursada.PROMOCIONADO, new BigDecimal("8"));
            Materia fisica2 = getMateria("Física II");
            crearHistoriaAcademica(alumnoAgustin, c2k1, fisica2, EstadoCursada.PROMOCIONADO, new BigDecimal("7"));
            
            // Regulares (Listas para rendir examen final)
            // Sintaxis requiere: Algoritmos y Lógica (Ya aprobadas) -> OK para rendir
            crearHistoriaAcademica(alumnoAgustin, c2k1, sintaxis, EstadoCursada.REGULAR, new BigDecimal("6"));
            
            // Paradigmas requiere: Algoritmos y Lógica (Ya aprobadas) -> OK para rendir
            crearHistoriaAcademica(alumnoAgustin, c2k1, paradigmas, EstadoCursada.REGULAR, new BigDecimal("6"));
            
            // Sistemas Operativos requiere: Arquitectura (Ya aprobada) -> OK para rendir
            crearHistoriaAcademica(alumnoAgustin, c2k1, sistemasOp, EstadoCursada.REGULAR, new BigDecimal("6"));

            Materia analisisSist = getMateria("Análisis de Sistemas de Información");
            inscribirCursado(alumnoAgustin, c2k1, analisisSist);
            
            Materia ingles2 = getMateria("Inglés II");
            inscribirCursado(alumnoAgustin, c2k1, ingles2);
        }



        if (cElectivas != null && alumnoAgustin != null) {
            var inscEntornos = inscribirCursado(alumnoAgustin, cElectivas, entornos);
            inscEntornos.setEstado(EstadoCursada.REGULAR);
            inscEntornos.setNotaFinal(new BigDecimal("5.00"));
            inscripcionCursadoRepository.save(inscEntornos);

            var inscMineria = inscribirCursado(alumnoAgustin, cElectivas, mineria);
            inscMineria.setEstado(EstadoCursada.REGULAR);
            inscMineria.setNotaFinal(new BigDecimal("5.50"));
            inscripcionCursadoRepository.save(inscMineria);

            var inscEmprendedores = inscribirCursado(alumnoAgustin, cElectivas, emprendedores);
            inscEmprendedores.setEstado(EstadoCursada.REGULAR);
            inscEmprendedores.setNotaFinal(new BigDecimal("5.00"));
            inscripcionCursadoRepository.save(inscEmprendedores);
        }

        if (c1k1 != null && alumnoSofia != null) {
            var inscAlgo = inscribirCursado(alumnoSofia, c1k1, algoritmos);
            inscAlgo.setNotaFinal(new BigDecimal("9.00"));
            inscAlgo.setEstado(EstadoCursada.PROMOCIONADO);
            inscAlgo.setFechaPromocion(LocalDate.now());
            inscAlgo.setTomo(getRandomTomo());
            inscAlgo.setFolio(getRandomFolio());
            inscripcionCursadoRepository.save(inscAlgo);

            var inscSis = inscribirCursado(alumnoSofia, c1k1, sistemas);
            inscSis.setNotaFinal(new BigDecimal("8.00"));
            inscSis.setEstado(EstadoCursada.PROMOCIONADO);
            inscSis.setTomo(getRandomTomo());
            inscSis.setFolio(getRandomFolio());
            inscripcionCursadoRepository.save(inscSis);

            var inscIng = inscribirCursado(alumnoSofia, c1k1, ingles1);
            inscIng.setNotaFinal(new BigDecimal("10.00"));
            inscIng.setEstado(EstadoCursada.PROMOCIONADO);
            inscIng.setTomo(getRandomTomo());
            inscIng.setFolio(getRandomFolio());
            inscripcionCursadoRepository.save(inscIng);

            var inscAlg = inscribirCursado(alumnoSofia, c1k1, algebra);
            inscAlg.setNotaFinal(new BigDecimal("7.50"));
            inscAlg.setEstado(EstadoCursada.PROMOCIONADO);
            inscAlg.setTomo(getRandomTomo());
            inscAlg.setFolio(getRandomFolio());
            inscripcionCursadoRepository.save(inscAlg);
        }

        if (c1k2 != null && alumnoSofia != null) {
            var inscAn1 = inscribirCursado(alumnoSofia, c1k2, analisis1);
            inscAn1.setNotaFinal(new BigDecimal("8.00"));
            inscAn1.setEstado(EstadoCursada.REGULAR);
            inscripcionCursadoRepository.save(inscAn1);

            var inscFis1 = inscribirCursado(alumnoSofia, c1k2, fisica1);
            inscFis1.setNotaFinal(new BigDecimal("9.50"));
            inscFis1.setEstado(EstadoCursada.PROMOCIONADO);
            inscFis1.setTomo(getRandomTomo());
            inscFis1.setFolio(getRandomFolio());
            inscripcionCursadoRepository.save(inscFis1);
        }

        if (c2k1 != null && alumnoSofia != null) {
            var inscAn2 = inscribirCursado(alumnoSofia, c2k1, analisis2);
            inscAn2.setNotaFinal(new BigDecimal("7.00"));
            inscripcionCursadoRepository.save(inscAn2);

            var inscSin = inscribirCursado(alumnoSofia, c2k1, sintaxis);
            inscSin.setNotaFinal(new BigDecimal("10.00"));
            inscripcionCursadoRepository.save(inscSin);
        }

        if (c1k1 != null && alumnoCarlos != null) {
            var inscAlgo = inscribirCursado(alumnoCarlos, c1k1, algoritmos);
            inscAlgo.setNotaFinal(new BigDecimal("6.00"));
            inscripcionCursadoRepository.save(inscAlgo);

            var inscSis = inscribirCursado(alumnoCarlos, c1k1, sistemas);
            inscSis.setNotaFinal(new BigDecimal("7.00"));
            inscripcionCursadoRepository.save(inscSis);
        }

        if (c2k1 != null && alumnoCarlos != null) {
            var inscSin = inscribirCursado(alumnoCarlos, c2k1, sintaxis);
            cargarNotaCursada(inscSin, "1er Parcial", "4.00");
        }

        if (c2k1 != null && alumnoLucia != null) {
            var inscAn2 = inscribirCursado(alumnoLucia, c2k1, analisis2);
            cargarNotaCursada(inscAn2, "1er Parcial", "2.00");
        }

        if (c1k1 != null && alumnoMaria != null) {
            var inscAlgo = inscribirCursado(alumnoMaria, c1k1, algoritmos);
            cargarNotaCursada(inscAlgo, "1er Parcial", "10.00");
        }

        if (c1k2 != null && alumnoJuan != null) {
            inscribirCursado(alumnoJuan, c1k2, analisis1);
        }

        if (c1k2 != null && alumnoMiguel != null) {
            inscribirCursado(alumnoMiguel, c1k2, fisica1);
        }

        if (c1k1 != null && alumnoMartin != null) {
            var insc = inscribirCursado(alumnoMartin, c1k1, algoritmos);
            cargarNotaCursada(insc, "1er Parcial", "2.00");
        }

        if (c1k1 != null && alumnoFlavia != null) {
            inscribirCursado(alumnoFlavia, c1k1, sistemas);
        }

        if (c1k2 != null && alumnoPedro != null) {
            var i1 = inscribirCursado(alumnoPedro, c1k2, algoritmos);
            i1.setEstado(EstadoCursada.REGULAR); i1.setNotaFinal(new BigDecimal("5")); inscripcionCursadoRepository.save(i1);
            var i2 = inscribirCursado(alumnoPedro, c1k2, analisis1);
            i2.setEstado(EstadoCursada.REGULAR); i2.setNotaFinal(new BigDecimal("5")); inscripcionCursadoRepository.save(i2);
            var i3 = inscribirCursado(alumnoPedro, c1k2, fisica1);
            i3.setEstado(EstadoCursada.REGULAR); i3.setNotaFinal(new BigDecimal("5")); inscripcionCursadoRepository.save(i3);
            var i4 = inscribirCursado(alumnoPedro, c1k2, arquitectura);
            i4.setEstado(EstadoCursada.REGULAR); i4.setNotaFinal(new BigDecimal("4")); inscripcionCursadoRepository.save(i4);
        }

        if (c1k1 != null && alumnoLionel != null) {
            var p1 = inscribirCursado(alumnoLionel, c1k1, algoritmos);
            p1.setEstado(EstadoCursada.PROMOCIONADO); p1.setNotaFinal(new BigDecimal("10")); p1.setFechaPromocion(LocalDate.of(2024, 12, 1)); inscripcionCursadoRepository.save(p1);
            var p2 = inscribirCursado(alumnoLionel, c1k1, sistemas);
            p2.setEstado(EstadoCursada.PROMOCIONADO); p2.setNotaFinal(new BigDecimal("9")); p2.setFechaPromocion(LocalDate.of(2024, 12, 1)); inscripcionCursadoRepository.save(p2);
            var p3 = inscribirCursado(alumnoLionel, c1k1, ingles1);
            p3.setEstado(EstadoCursada.PROMOCIONADO); p3.setNotaFinal(new BigDecimal("10")); p3.setFechaPromocion(LocalDate.of(2024, 12, 1)); inscripcionCursadoRepository.save(p3);
            var p4 = inscribirCursado(alumnoLionel, c1k1, algebra);
            p4.setEstado(EstadoCursada.PROMOCIONADO); p4.setNotaFinal(new BigDecimal("10")); p4.setFechaPromocion(LocalDate.of(2024, 12, 1)); inscripcionCursadoRepository.save(p4);
        }

        if (c2k1 != null && alumnoLionel != null) {
            var c1 = inscribirCursado(alumnoLionel, c2k1, analisis2);
            cargarNotaCursada(c1, "1er Parcial", "10");
            var c2 = inscribirCursado(alumnoLionel, c2k1, sintaxis);
            cargarNotaCursada(c2, "TP", "10");
        }

        if (c1k2 != null && alumnoAlex != null) {
            var m1 = inscribirCursado(alumnoAlex, c1k2, algoritmos);
            cargarNotaCursada(m1, "1er Parcial", "2");
        }

        if (c1k1 != null && alumnoAlex != null) {
            var m2 = inscribirCursado(alumnoAlex, c1k1, sistemas);
            m2.setEstado(EstadoCursada.REGULAR); m2.setNotaFinal(new BigDecimal("7")); inscripcionCursadoRepository.save(m2);
        }

        if (c1k1 != null && alumnoDiego != null) {
            var d1 = inscribirCursado(alumnoDiego, c1k1, algoritmos);
            d1.setEstado(EstadoCursada.LIBRE); d1.setNotaFinal(new BigDecimal("2")); inscripcionCursadoRepository.save(d1);
            var d2 = inscribirCursado(alumnoDiego, c1k1, algebra);
            cargarNotaCursada(d2, "1er Parcial", "2");
        }

        if (c1k1 != null && alumnoEnzo != null) {
            inscribirCursado(alumnoEnzo, c1k1, algoritmos);
            inscribirCursado(alumnoEnzo, c1k1, sistemas);
            inscribirCursado(alumnoEnzo, c1k1, ingles1);
        }

        System.out.println(">> InscripcionSeeder: Seeding de Cursado Finalizado.");
    }

    private void seedExamenes() {

            System.out.println(">> InscripcionSeeder: Desplegando Mesas de Examen...");

            // Crear Mesas
            MesaExamen mesaFeb = createMesa("Turno Febrero 2026", LocalDate.of(2026, 2, 1), LocalDate.of(2026, 2, 28));
            MesaExamen mesaJul = createMesa("Turno Julio 2026", LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 31));
            MesaExamen mesaDic = createMesa("Turno Diciembre 2026", LocalDate.of(2026, 12, 1), LocalDate.of(2026, 12, 22));
            MesaExamen mesaMay = createMesa("Turno Mayo 2026", LocalDate.of(2026, 5, 20), LocalDate.of(2026, 5, 30));
            MesaExamen mesaSep = createMesa("Turno Septiembre 2026", LocalDate.of(2026, 9, 15), LocalDate.of(2026, 9, 25));

            // Profes y Materias
            Usuario profeNicolas = usuarioRepository.findByLegajo("51111").orElse(null);
            Usuario profeLaura = usuarioRepository.findByLegajo("52222").orElse(null);
            Usuario profeRoberto = usuarioRepository.findByLegajo("53333").orElse(null);
            Usuario profeAna = usuarioRepository.findByLegajo("54444").orElse(null);
            Usuario profeSandra = usuarioRepository.findByLegajo("55551").orElse(null);
            Usuario profeCristian = usuarioRepository.findByLegajo("55552").orElse(null);
            Usuario profeClaudia = usuarioRepository.findByLegajo("55554").orElse(null);

            Materia algoritmos = getMateria("Algoritmos y Estructuras de Datos");
            Materia sistemas = getMateria("Sistemas y Procesos de Negocio");
            Materia analisis1 = getMateria("Análisis Matemático I");
            Materia fisica1 = getMateria("Física I");
            Materia ingles1 = getMateria("Inglés I");
            Materia logica = getMateria("Lógica y Estructuras Discretas");
            Materia arquitectura = getMateria("Arquitectura de Computadoras");
            Materia algebra = getMateria("Álgebra y Geometría Analítica");

            Materia analisis2 = getMateria("Análisis Matemático II");
            Materia fisica2 = getMateria("Física II");
            Materia ingSociedad = getMateria("Ingeniería y Sociedad");
            Materia ingles2 = getMateria("Inglés II");
            Materia sintaxis = getMateria("Sintaxis y Semántica de los Lenguajes");
            Materia paradigmas = getMateria("Paradigmas de Programación");
            Materia sistemasOp = getMateria("Sistemas Operativos");
            Materia analisisSist = getMateria("Análisis de Sistemas de Información");

            Materia probEst = getMateria("Probabilidad y Estadística");
            Materia economia = getMateria("Economía");
            Materia basesDatos = getMateria("Bases de Datos");
            Materia desSoft = getMateria("Desarrollo de Software");
            Materia comDatos = getMateria("Comunicación de Datos");
            Materia analisisNumerico = getMateria("Análisis Numérico");
            Materia disenio = getMateria("Diseño de Sistemas de Información");

            Materia legislacion = getMateria("Legislación");
            Materia ingCalidad = getMateria("Ingeniería y Calidad de Software");
            Materia redes = getMateria("Redes de Datos");
            Materia invOp = getMateria("Investigación Operativa");
            Materia simulacion = getMateria("Simulación");
            Materia tecAuto = getMateria("Tecnologías para la Automatización");
            Materia adminSist = getMateria("Administración de Sistemas de Información");

            Materia ia = getMateria("Inteligencia Artificial");
            Materia dataScience = getMateria("Ciencia de Datos");
            Materia sistGestion = getMateria("Sistemas de Gestión");
            Materia gestionGer = getMateria("Gestión Gerencial");
            Materia seguridad = getMateria("Seguridad en los Sistemas de Información");
            Materia proyectoFinal = getMateria("Proyecto Final");
            
            Materia entornos = getMateria("Entornos Gráficos");
            Materia mineria = getMateria("Minería de Datos");
            Materia emprendedores = getMateria("Formación de Emprendedores");

            // FEBRERO
            DetalleMesaExamen febAlgo = createDetalleMesa(mesaFeb, 1, algoritmos, profeNicolas, LocalDate.of(2026, 2, 10), LocalTime.of(9, 0), profeLaura, profeCristian);
            DetalleMesaExamen febSistemas = createDetalleMesa(mesaFeb, 2, sistemas, profeNicolas, LocalDate.of(2026, 2, 12), LocalTime.of(14, 0), profeSandra);
            DetalleMesaExamen febAnalisis = createDetalleMesa(mesaFeb, 3, analisis1, profeSandra, LocalDate.of(2026, 2, 15), LocalTime.of(9, 0), profeAna);

            // Complete Generation (Feb)
            createDetalleMesa(mesaFeb, 4, fisica1, profeRoberto, LocalDate.of(2026, 2, 14), LocalTime.of(16, 0));
            createDetalleMesa(mesaFeb, 5, ingles1, profeAna, LocalDate.of(2026, 2, 16), LocalTime.of(14, 0));
            createDetalleMesa(mesaFeb, 6, logica, profeNicolas, LocalDate.of(2026, 2, 18), LocalTime.of(18, 0));
            createDetalleMesa(mesaFeb, 8, arquitectura, profeCristian, LocalDate.of(2026, 2, 22), LocalTime.of(9, 0));
            createDetalleMesa(mesaFeb, 9, analisis2, profeCristian, LocalDate.of(2026, 2, 11), LocalTime.of(14, 0));
            createDetalleMesa(mesaFeb, 10, fisica2, profeRoberto, LocalDate.of(2026, 2, 13), LocalTime.of(16, 0));
            createDetalleMesa(mesaFeb, 11, ingSociedad, profeLaura, LocalDate.of(2026, 2, 15), LocalTime.of(18, 0));
            createDetalleMesa(mesaFeb, 12, ingles2, profeAna, LocalDate.of(2026, 2, 17), LocalTime.of(14, 0));
            createDetalleMesa(mesaFeb, 13, sintaxis, profeNicolas, LocalDate.of(2026, 2, 19), LocalTime.of(19, 0));
            createDetalleMesa(mesaFeb, 14, paradigmas, profeCristian, LocalDate.of(2026, 2, 21), LocalTime.of(9, 0));
            createDetalleMesa(mesaFeb, 15, sistemasOp, profeNicolas, LocalDate.of(2026, 2, 23), LocalTime.of(18, 0));
            createDetalleMesa(mesaFeb, 16, analisisSist, profeLaura, LocalDate.of(2026, 2, 25), LocalTime.of(18, 0));
            
            // ... Nivel 3
            createDetalleMesa(mesaFeb, 17, probEst, profeSandra, LocalDate.of(2026, 2, 10), LocalTime.of(14, 0));
            createDetalleMesa(mesaFeb, 18, economia, profeLaura, LocalDate.of(2026, 2, 12), LocalTime.of(19, 0));
            createDetalleMesa(mesaFeb, 19, basesDatos, profeNicolas, LocalDate.of(2026, 2, 14), LocalTime.of(18, 0));
            createDetalleMesa(mesaFeb, 20, desSoft, profeCristian, LocalDate.of(2026, 2, 16), LocalTime.of(18, 0));
            createDetalleMesa(mesaFeb, 21, comDatos, profeRoberto, LocalDate.of(2026, 2, 18), LocalTime.of(16, 0));
            createDetalleMesa(mesaFeb, 22, analisisNumerico, profeCristian, LocalDate.of(2026, 2, 20), LocalTime.of(14, 0));
            createDetalleMesa(mesaFeb, 23, disenio, profeLaura, LocalDate.of(2026, 2, 22), LocalTime.of(18, 0));

             // Nivel 4
            createDetalleMesa(mesaFeb, 24, legislacion, profeLaura, LocalDate.of(2026, 2, 11), LocalTime.of(18, 0));
            createDetalleMesa(mesaFeb, 25, ingCalidad, profeCristian, LocalDate.of(2026, 2, 13), LocalTime.of(18, 0));
            createDetalleMesa(mesaFeb, 26, redes, profeRoberto, LocalDate.of(2026, 2, 15), LocalTime.of(16, 0));
            createDetalleMesa(mesaFeb, 27, invOp, profeSandra, LocalDate.of(2026, 2, 17), LocalTime.of(14, 0));
            createDetalleMesa(mesaFeb, 28, simulacion, profeCristian, LocalDate.of(2026, 2, 19), LocalTime.of(18, 0));
            createDetalleMesa(mesaFeb, 29, tecAuto, profeRoberto, LocalDate.of(2026, 2, 21), LocalTime.of(16, 0));
            createDetalleMesa(mesaFeb, 30, adminSist, profeLaura, LocalDate.of(2026, 2, 23), LocalTime.of(18, 0));

            // Nivel 5
            createDetalleMesa(mesaFeb, 31, ia, profeNicolas, LocalDate.of(2026, 2, 10), LocalTime.of(18, 0));
            createDetalleMesa(mesaFeb, 32, dataScience, profeNicolas, LocalDate.of(2026, 2, 12), LocalTime.of(18, 0));
            createDetalleMesa(mesaFeb, 33, sistGestion, profeLaura, LocalDate.of(2026, 2, 14), LocalTime.of(19, 0));
            createDetalleMesa(mesaFeb, 34, gestionGer, profeLaura, LocalDate.of(2026, 2, 16), LocalTime.of(18, 0));
            createDetalleMesa(mesaFeb, 35, seguridad, profeCristian, LocalDate.of(2026, 2, 18), LocalTime.of(18, 0));
            createDetalleMesa(mesaFeb, 36, proyectoFinal, profeNicolas, LocalDate.of(2026, 2, 25), LocalTime.of(18, 0));

            createDetalleMesa(mesaFeb, 37, entornos, profeClaudia, LocalDate.of(2026, 2, 26), LocalTime.of(18, 0));
            createDetalleMesa(mesaFeb, 38, mineria, profeNicolas, LocalDate.of(2026, 2, 27), LocalTime.of(18, 0));
            createDetalleMesa(mesaFeb, 39, emprendedores, profeClaudia, LocalDate.of(2026, 2, 28), LocalTime.of(18, 0));
            createDetalleMesa(mesaFeb, 40, ingles2, profeAna, LocalDate.of(2026, 2, 24), LocalTime.of(14, 0));

            // JULIO
            DetalleMesaExamen julSintaxis = createDetalleMesa(mesaJul, 1, sintaxis, profeNicolas, LocalDate.of(2026, 7, 10), LocalTime.of(9, 0));
            DetalleMesaExamen julFisica = createDetalleMesa(mesaJul, 2, fisica1, profeRoberto, LocalDate.of(2026, 7, 15), LocalTime.of(16, 0));
            createDetalleMesa(mesaJul, 3, analisis1, profeSandra, LocalDate.of(2026, 7, 6), LocalTime.of(9, 0));
            createDetalleMesa(mesaJul, 4, algebra, profeSandra, LocalDate.of(2026, 7, 7), LocalTime.of(9, 0));
            createDetalleMesa(mesaJul, 5, ingles1, profeAna, LocalDate.of(2026, 7, 8), LocalTime.of(14, 0));
            createDetalleMesa(mesaJul, 6, logica, profeNicolas, LocalDate.of(2026, 7, 10), LocalTime.of(18, 0));
            createDetalleMesa(mesaJul, 7, arquitectura, profeCristian, LocalDate.of(2026, 7, 13), LocalTime.of(9, 0));
            createDetalleMesa(mesaJul, 8, sistemas, profeNicolas, LocalDate.of(2026, 7, 14), LocalTime.of(14, 0));
            createDetalleMesa(mesaJul, 9, analisis2, profeCristian, LocalDate.of(2026, 7, 15), LocalTime.of(14, 0));
            createDetalleMesa(mesaJul, 10, fisica2, profeRoberto, LocalDate.of(2026, 7, 16), LocalTime.of(16, 0));
            createDetalleMesa(mesaJul, 11, ingSociedad, profeLaura, LocalDate.of(2026, 7, 17), LocalTime.of(18, 0));
            createDetalleMesa(mesaJul, 12, ingles2, profeAna, LocalDate.of(2026, 7, 20), LocalTime.of(14, 0));
            createDetalleMesa(mesaJul, 13, paradigmas, profeCristian, LocalDate.of(2026, 7, 21), LocalTime.of(9, 0));
            createDetalleMesa(mesaJul, 14, sistemasOp, profeNicolas, LocalDate.of(2026, 7, 22), LocalTime.of(18, 0));
            createDetalleMesa(mesaJul, 15, analisisSist, profeLaura, LocalDate.of(2026, 7, 23), LocalTime.of(18, 0));
            createDetalleMesa(mesaJul, 16, basesDatos, profeNicolas, LocalDate.of(2026, 7, 24), LocalTime.of(18, 0));
            createDetalleMesa(mesaJul, 17, emprendedores, profeClaudia, LocalDate.of(2026, 7, 27), LocalTime.of(18, 0));

            // DICIEMBRE
            createDetalleMesa(mesaDic, 1, algoritmos, profeNicolas, LocalDate.of(2026, 12, 10), LocalTime.of(9, 0));
             createDetalleMesa(mesaDic, 2, sistemas, profeNicolas, LocalDate.of(2026, 12, 12), LocalTime.of(9, 0));
            createDetalleMesa(mesaDic, 3, analisis1, profeSandra, LocalDate.of(2026, 12, 14), LocalTime.of(9, 0));
            createDetalleMesa(mesaDic, 4, fisica1, profeRoberto, LocalDate.of(2026, 12, 16), LocalTime.of(16, 0));
            createDetalleMesa(mesaDic, 5, sintaxis, profeNicolas, LocalDate.of(2026, 12, 11), LocalTime.of(18, 0));
            createDetalleMesa(mesaDic, 6, paradigmas, profeCristian, LocalDate.of(2026, 12, 13), LocalTime.of(9, 0));
            createDetalleMesa(mesaDic, 7, basesDatos, profeNicolas, LocalDate.of(2026, 12, 15), LocalTime.of(18, 0));
            createDetalleMesa(mesaDic, 8, disenio, profeLaura, LocalDate.of(2026, 12, 17), LocalTime.of(18, 0));
            createDetalleMesa(mesaDic, 9, proyectoFinal, profeNicolas, LocalDate.of(2026, 12, 20), LocalTime.of(18, 0));
            createDetalleMesa(mesaDic, 10, ingles1, profeAna, LocalDate.of(2026, 12, 11), LocalTime.of(14, 0));
            createDetalleMesa(mesaDic, 11, logica, profeNicolas, LocalDate.of(2026, 12, 13), LocalTime.of(18, 0));
            createDetalleMesa(mesaDic, 12, arquitectura, profeCristian, LocalDate.of(2026, 12, 16), LocalTime.of(9, 0));
            createDetalleMesa(mesaDic, 13, analisis2, profeCristian, LocalDate.of(2026, 12, 18), LocalTime.of(14, 0));
            createDetalleMesa(mesaDic, 14, fisica2, profeRoberto, LocalDate.of(2026, 12, 19), LocalTime.of(16, 0));
            createDetalleMesa(mesaDic, 15, ingSociedad, profeLaura, LocalDate.of(2026, 12, 21), LocalTime.of(18, 0));
            createDetalleMesa(mesaDic, 16, ingles2, profeAna, LocalDate.of(2026, 12, 22), LocalTime.of(14, 0));
            createDetalleMesa(mesaDic, 17, sistemasOp, profeNicolas, LocalDate.of(2026, 12, 14), LocalTime.of(18, 0));
            createDetalleMesa(mesaDic, 18, analisisSist, profeLaura, LocalDate.of(2026, 12, 15), LocalTime.of(18, 0));
            createDetalleMesa(mesaDic, 19, emprendedores, profeClaudia, LocalDate.of(2026, 12, 17), LocalTime.of(18, 0));

            // MAYO
            createDetalleMesa(mesaMay, 1, algoritmos, profeNicolas, LocalDate.of(2026, 5, 22), LocalTime.of(14, 0));
            createDetalleMesa(mesaMay, 2, analisis1, profeSandra, LocalDate.of(2026, 5, 24), LocalTime.of(14, 0));
            createDetalleMesa(mesaMay, 3, sintaxis, profeNicolas, LocalDate.of(2026, 5, 26), LocalTime.of(14, 0));
            createDetalleMesa(mesaMay, 4, fisica1, profeRoberto, LocalDate.of(2026, 5, 27), LocalTime.of(16, 0));
            createDetalleMesa(mesaMay, 5, sistemas, profeNicolas, LocalDate.of(2026, 5, 28), LocalTime.of(14, 0));
            createDetalleMesa(mesaMay, 6, emprendedores, profeClaudia, LocalDate.of(2026, 5, 29), LocalTime.of(18, 0));

            // SEPTIEMBRE
            createDetalleMesa(mesaSep, 1, algoritmos, profeNicolas, LocalDate.of(2026, 9, 18), LocalTime.of(14, 0));
            createDetalleMesa(mesaSep, 2, analisis1, profeSandra, LocalDate.of(2026, 9, 20), LocalTime.of(14, 0));
            createDetalleMesa(mesaSep, 3, disenio, profeLaura, LocalDate.of(2026, 9, 22), LocalTime.of(14, 0));
            createDetalleMesa(mesaSep, 4, fisica1, profeRoberto, LocalDate.of(2026, 9, 23), LocalTime.of(16, 0));
            createDetalleMesa(mesaSep, 5, sistemas, profeNicolas, LocalDate.of(2026, 9, 24), LocalTime.of(14, 0));
            createDetalleMesa(mesaSep, 6, emprendedores, profeClaudia, LocalDate.of(2026, 9, 25), LocalTime.of(18, 0));

            // DICIEMBRE 2025 (Especial para Nicolas)
            MesaExamen mesaDic2025 = createMesa("Turno Diciembre 2025", LocalDate.of(2025, 12, 1), LocalDate.of(2025, 12, 22));
            DetalleMesaExamen dic25Algo = createDetalleMesa(mesaDic2025, 1, algoritmos, profeNicolas, LocalDate.of(2025, 12, 10), LocalTime.of(9, 0), profeLaura);
            DetalleMesaExamen dic25Sistemas = createDetalleMesa(mesaDic2025, 2, sistemas, profeNicolas, LocalDate.of(2025, 12, 12), LocalTime.of(14, 0), profeSandra);
            
            // Inscribir alumnos a Diciembre 2025 para que Nicolas tenga que corregir
            // Usamos a Agustin y Sofia
            Usuario alumnoAgustin = usuarioRepository.findByLegajo("55555").orElse(null);
            Usuario alumnoSofia = usuarioRepository.findByLegajo("58888").orElse(null);
            Usuario alumnoCarlos = usuarioRepository.findByLegajo("60002").orElse(null);

            if (alumnoAgustin != null) {
                // Agustin rinde Algoritmos en Dic 2025 -> PENDIENTE (Para corregir)
                inscribirExamen(alumnoAgustin, dic25Algo);
            }
            
            if (alumnoSofia != null) {
                // Sofia rinde Sistemas en Dic 2025 -> PENDIENTE (Para corregir)
                inscribirExamen(alumnoSofia, dic25Sistemas);
            }

            // Inscribir Alumnos a Examenes (General)

            if (alumnoAgustin != null) {
                inscribirExamen(alumnoAgustin, julSintaxis);
            }

            if (alumnoSofia != null) {
                inscribirExamen(alumnoSofia, febSistemas);
                var insc = inscribirExamen(alumnoSofia, febAnalisis);
                insc.setNota(new BigDecimal("9.00"));
                insc.setEstado(EstadoExamen.APROBADO);
                insc.setTomo(getRandomTomo());
                insc.setFolio(getRandomFolio());
                inscripcionExamenRepository.save(insc);
            }

            if (alumnoCarlos != null) {
                inscribirExamen(alumnoCarlos, julFisica);
            }

            System.out.println(">> InscripcionSeeder: Mesas de Examen y Asociaciones creadas.");

    }


    private InscripcionCursado inscribirCursado(Usuario alumno, Comision comision, Materia materia) {
        // Idempotency check
        var existing = inscripcionCursadoRepository.findByUsuarioIdAndMateriaId(alumno.getId(), materia.getId());
        if (existing.isPresent()) {
            return existing.get();
        }

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
        return inscripcionCursadoRepository.save(insc);
    }

    private void cargarNotaCursada(InscripcionCursado insc, String descripcion, String valor) {
        boolean exists = calificacionCursadaRepository.findAll().stream()
             .anyMatch(c -> c.getInscripcionCursado().getId().equals(insc.getId()) && c.getDescripcion().equals(descripcion));
        
        if (exists) return;

        CalificacionCursada calif = new CalificacionCursada();
        calif.setInscripcionCursado(insc);
        calif.setDescripcion(descripcion);
        calif.setNota(new BigDecimal(valor));
        calif.setFecha(LocalDate.now());
        calificacionCursadaRepository.save(calif);
    }

    private Materia getMateria(String nombre) {
        return materiaRepository.findByNombre(nombre)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada: " + nombre));
    }

    private MesaExamen createMesa(String nombre, LocalDate inicio, LocalDate fin) {
        return mesaExamenRepository.findAll().stream()
                .filter(m -> m.getNombre().equals(nombre))
                .findFirst()
                .orElseGet(() -> {
                    MesaExamen mesa = new MesaExamen();
                    mesa.setNombre(nombre);
                    mesa.setFechaInicio(inicio);
                    mesa.setFechaFin(fin);
                    return mesaExamenRepository.save(mesa);
                });
    }

    private DetalleMesaExamen createDetalleMesa(MesaExamen mesa, Integer nro,
                                              Materia materia,
                                              Usuario presidente, LocalDate dia, LocalTime hora, Usuario... auxiliares) {
        DetalleMesaExamen.DetalleId id = new DetalleMesaExamen.DetalleId(mesa.getId(), nro);
        if (detalleMesaExamenRepository.existsById(id)) {
            return detalleMesaExamenRepository.findById(id).get();
        }
        
        DetalleMesaExamen detalle = new DetalleMesaExamen();
        detalle.setId(id);
        detalle.setMesaExamen(mesa);
        detalle.setMateria(materia);
        detalle.setPresidente(presidente);
        detalle.setDiaExamen(dia);
        detalle.setHoraExamen(hora);
        
        if (auxiliares != null && auxiliares.length > 0) {
            detalle.setAuxiliares(java.util.List.of(auxiliares));
        }
        
        return detalleMesaExamenRepository.save(detalle);
    }

    private InscripcionExamen inscribirExamen(Usuario alumno, DetalleMesaExamen detalle) {
        boolean exists = inscripcionExamenRepository.findByUsuarioId(alumno.getId()).stream()
                .anyMatch(i -> i.getDetalleMesaExamen() != null && 
                               i.getDetalleMesaExamen().getId().equals(detalle.getId()));
        
        if (exists) {
            return inscripcionExamenRepository.findByUsuarioId(alumno.getId()).stream()
                .filter(i -> i.getDetalleMesaExamen() != null && i.getDetalleMesaExamen().getId().equals(detalle.getId()))
                .findFirst().get();
        }

        InscripcionExamen insc = new InscripcionExamen();
        insc.setUsuario(alumno);
        insc.setDetalleMesaExamen(detalle);
        insc.setFechaInscripcion(LocalDateTime.now());
        insc.setEstado(EstadoExamen.PENDIENTE);
        return inscripcionExamenRepository.save(insc);
    }

    private String getRandomTomo() {
        return String.valueOf(100 + (int) (Math.random() * 900));
    }

    private String getRandomFolio() {
        return String.valueOf(10 + (int) (Math.random() * 900));
    }

    private void crearHistoriaAcademica(Usuario alumno, Comision comision, Materia materia, EstadoCursada estado, BigDecimal nota) {
        var insc = inscribirCursado(alumno, comision, materia);
        insc.setEstado(estado);
        insc.setNotaFinal(nota);
        
        if (estado == EstadoCursada.PROMOCIONADO) {
            insc.setFechaPromocion(LocalDate.of(2025, 12, 15));
            insc.setTomo(getRandomTomo());
            insc.setFolio(getRandomFolio());
        }
        
        inscripcionCursadoRepository.save(insc);
    }
}
