package com.sysacad.backend.config.seeder;

import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.enums.DiaSemana;
import com.sysacad.backend.modelo.enums.RolCargo;
import com.sysacad.backend.repository.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Component
public class ComisionSeeder {

    private final ComisionRepository comisionRepository;
    private final SalonRepository salonRepository;
    private final FacultadRegionalRepository facultadRepository;
    private final MateriaRepository materiaRepository;
    private final HorarioCursadoRepository horarioCursadoRepository;
    private final AsignacionMateriaRepository asignacionMateriaRepository;
    private final UsuarioRepository usuarioRepository;

    public ComisionSeeder(ComisionRepository comisionRepository, SalonRepository salonRepository,
                          FacultadRegionalRepository facultadRepository, MateriaRepository materiaRepository,
                          HorarioCursadoRepository horarioCursadoRepository, AsignacionMateriaRepository asignacionMateriaRepository,
                          UsuarioRepository usuarioRepository) {
        this.comisionRepository = comisionRepository;
        this.salonRepository = salonRepository;
        this.facultadRepository = facultadRepository;
        this.materiaRepository = materiaRepository;
        this.horarioCursadoRepository = horarioCursadoRepository;
        this.asignacionMateriaRepository = asignacionMateriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public void seed() {
        if (comisionRepository.count() == 0) {
            System.out.println(">> ComisionSeeder: Desplegando infraestructura de cursada completa...");

            FacultadRegional frro = facultadRepository.findAll().stream()
                    .filter(f -> f.getCiudad().equalsIgnoreCase("Rosario"))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Error: Facultad Rosario no encontrada."));

            // Recuperar Profesores
            Usuario profeNicolas = usuarioRepository.findByLegajo("51111").orElse(null);
            Usuario profeLaura = usuarioRepository.findByLegajo("52222").orElse(null);
            Usuario profeRoberto = usuarioRepository.findByLegajo("53333").orElse(null);
            Usuario profeAna = usuarioRepository.findByLegajo("54444").orElse(null);
            Usuario profeSandra = usuarioRepository.findByLegajo("55551").orElse(null);
            Usuario profeCristian = usuarioRepository.findByLegajo("55552").orElse(null);
            Usuario profeGustavo = usuarioRepository.findByLegajo("55553").orElse(null);
            Usuario profeClaudia = usuarioRepository.findByLegajo("55554").orElse(null);
            Usuario profeJorge = usuarioRepository.findByLegajo("55556").orElse(null);
            Usuario profeValeria = usuarioRepository.findByLegajo("55557").orElse(null);

            if (profeNicolas == null) {
                System.out.println(">> ComisionSeeder: Faltan profesores esenciales, abortando seed parcial.");
                return;
            }

            // SALONES
            Salon lab305 = createSalon(frro, "Lab. Computación 305", "3");
            Salon aula401 = createSalon(frro, "Aula 401", "4");
            Salon aula402 = createSalon(frro, "Aula 402", "4");
            Salon aula101 = createSalon(frro, "Aula 101", "1");
            Salon aula301 = createSalon(frro, "Aula 301", "3");
            Salon sum = createSalon(frro, "SUM", "1");
            Salon aula201 = createSalon(frro, "Aula 201", "2");
            Salon aulaMagna = createSalon(frro, "Aula Magna", "PB");

            // Nivel 1
            Materia analisis1 = getMateria("Análisis Matemático I");
            Materia algebra = getMateria("Álgebra y Geometría Analítica");
            Materia fisica1 = getMateria("Física I");
            Materia ingles1 = getMateria("Inglés I");
            Materia logica = getMateria("Lógica y Estructuras Discretas");
            Materia algoritmos = getMateria("Algoritmos y Estructuras de Datos");
            Materia arquitectura = getMateria("Arquitectura de Computadoras");
            Materia sistemas = getMateria("Sistemas y Procesos de Negocio");

            // Nivel 2
            Materia analisis2 = getMateria("Análisis Matemático II");
            Materia fisica2 = getMateria("Física II");
            Materia ingSociedad = getMateria("Ingeniería y Sociedad");
            Materia ingles2 = getMateria("Inglés II");
            Materia sintaxis = getMateria("Sintaxis y Semántica de los Lenguajes");
            Materia paradigmas = getMateria("Paradigmas de Programación");
            Materia sistemasOp = getMateria("Sistemas Operativos");
            Materia analisisSist = getMateria("Análisis de Sistemas de Información");

            // Nivel 3
            Materia probEst = getMateria("Probabilidad y Estadística");
            Materia economia = getMateria("Economía");
            Materia basesDatos = getMateria("Bases de Datos");
            Materia desSoft = getMateria("Desarrollo de Software");
            Materia comDatos = getMateria("Comunicación de Datos");
            Materia analisisNumerico = getMateria("Análisis Numérico");
            Materia disenio = getMateria("Diseño de Sistemas de Información");

            // Nivel 4
            Materia legislacion = getMateria("Legislación");
            Materia ingCalidad = getMateria("Ingeniería y Calidad de Software");
            Materia redes = getMateria("Redes de Datos");
            Materia invOp = getMateria("Investigación Operativa");
            Materia simulacion = getMateria("Simulación");
            Materia tecAuto = getMateria("Tecnologías para la Automatización");
            Materia adminSist = getMateria("Administración de Sistemas de Información");

            // Nivel 5
            Materia ia = getMateria("Inteligencia Artificial");
            Materia dataScience = getMateria("Ciencia de Datos");
            Materia sistGestion = getMateria("Sistemas de Gestión");
            Materia gestionGer = getMateria("Gestión Gerencial");
            Materia seguridad = getMateria("Seguridad en los Sistemas de Información");
            Materia proyectoFinal = getMateria("Proyecto Final");

            // Electivas
            Materia entornos = getMateria("Entornos Gráficos");
            Materia emprendedores = getMateria("Formación de Emprendedores");
            Materia metAgiles = getMateria("Metodologías Ágiles");
            Materia mineria = getMateria("Minería de Datos");


            // 1K1 (Todas las de Primero - Mañana)
            Comision c1k1 = createComisionIfNotExists("1K1", 2025, "MAÑANA", lab305,
                    Arrays.asList(analisis1, algebra, fisica1, ingles1, logica, algoritmos, arquitectura, sistemas),
                    List.of(profeNicolas, profeAna, profeSandra, profeRoberto, profeGustavo, profeCristian, profeValeria));

            // 1K2 (Todas las de Primero - Noche)
            Comision c1k2 = createComisionIfNotExists("1K2", 2025, "NOCHE", aula401,
                    Arrays.asList(analisis1, algebra, fisica1, ingles1, logica, algoritmos, arquitectura, sistemas),
                    List.of(profeGustavo, profeClaudia, profeJorge, profeValeria));

            // 2K1 (Todas las de Segundo - Tarde)
            Comision c2k1 = createComisionIfNotExists("2K1", 2025, "TARDE", aula201,
                    Arrays.asList(analisis2, fisica2, ingSociedad, ingles2, sintaxis, paradigmas, sistemasOp, analisisSist),
                    List.of(profeCristian, profeRoberto, profeLaura, profeAna, profeNicolas, profeGustavo, profeClaudia, profeJorge));

            // 3K1 (Todas las de Tercero - Noche)
            Comision c3k1 = createComisionIfNotExists("3K1", 2025, "NOCHE", lab305,
                    Arrays.asList(probEst, economia, basesDatos, desSoft, comDatos, analisisNumerico, disenio),
                    List.of(profeSandra, profeValeria, profeLaura, profeNicolas, profeCristian, profeRoberto));

            // 4K1 (Todas las de Cuarto - Noche)
            Comision c4k1 = createComisionIfNotExists("4K1", 2025, "NOCHE", aulaMagna,
                    Arrays.asList(legislacion, ingCalidad, redes, invOp, simulacion, tecAuto, adminSist),
                    List.of(profeLaura, profeCristian, profeRoberto, profeSandra, profeGustavo));

            // 5K1 (Todas las de Quinto - Noche)
            Comision c5k1 = createComisionIfNotExists("5K1", 2025, "NOCHE", aulaMagna,
                    Arrays.asList(ia, dataScience, sistGestion, gestionGer, seguridad, proyectoFinal),
                    List.of(profeNicolas, profeLaura, profeJorge, profeCristian));

            // Electivas
            Comision cElectivas = createComisionIfNotExists("Electivas 2025 - Noche", 2025, "NOCHE", aulaMagna,
                    Arrays.asList(entornos, emprendedores, metAgiles, mineria),
                    List.of(profeNicolas, profeClaudia));

            // NUEVAS COMISIONES MASIVAS
            Comision c1k3 = createComisionIfNotExists("1K3", 2025, "TARDE", aula101,
                    Arrays.asList(analisis1, algebra, fisica1, ingles1, logica, algoritmos, arquitectura, sistemas),
                    List.of(profeLaura, profeNicolas, profeSandra, profeRoberto, profeAna, profeCristian));
            
            Comision c1k4 = createComisionIfNotExists("1K4", 2025, "MAÑANA", aula301,
                    Arrays.asList(analisis1, algebra, fisica1, ingles1, logica, algoritmos, arquitectura, sistemas),
                    List.of(profeCristian, profeGustavo, profeSandra, profeRoberto, profeAna));

            Comision c2k2 = createComisionIfNotExists("2K2", 2025, "NOCHE", aula402,
                    Arrays.asList(analisis2, fisica2, ingSociedad, ingles2, sintaxis, paradigmas, sistemasOp, analisisSist),
                    List.of(profeRoberto, profeAna, profeJorge, profeCristian, profeNicolas, profeLaura));

            Comision c2k3 = createComisionIfNotExists("2K3", 2025, "MAÑANA", aulaMagna,
                    Arrays.asList(analisis2, fisica2, ingSociedad, ingles2, sintaxis, paradigmas, sistemasOp, analisisSist),
                    List.of(profeSandra, profeClaudia, profeRoberto, profeAna, profeCristian, profeNicolas, profeLaura));

            Comision c3k2 = createComisionIfNotExists("3K2", 2025, "NOCHE", sum,
                    Arrays.asList(probEst, economia, basesDatos, desSoft, comDatos, analisisNumerico, disenio),
                    List.of(profeLaura, profeSandra, profeValeria, profeCristian, profeRoberto, profeNicolas));

            Comision c4k2 = createComisionIfNotExists("4K2", 2025, "NOCHE", aula201,
                    Arrays.asList(legislacion, ingCalidad, redes, invOp, simulacion, tecAuto, adminSist),
                    List.of(profeRoberto, profeLaura, profeGustavo, profeCristian, profeSandra));

            Comision c5k2 = createComisionIfNotExists("5K2", 2025, "NOCHE", aula402,
                    Arrays.asList(ia, dataScience, sistGestion, gestionGer, seguridad, proyectoFinal),
                    List.of(profeCristian, profeJorge, profeNicolas, profeLaura));

            Comision cElec2 = createComisionIfNotExists("Electivas 2025 - Mañana", 2025, "MAÑANA", aula101,
                    Arrays.asList(entornos, emprendedores, metAgiles, mineria),
                    List.of(profeLaura, profeClaudia, profeNicolas));


            // -------------------------------------------------------------------------
            // HORARIOS
            // -------------------------------------------------------------------------

            // 1K1
            crearHorarioIfNotExists(c1k1, analisis1, DiaSemana.LUNES, 8, 12);
            crearHorarioIfNotExists(c1k1, algebra, DiaSemana.MARTES, 8, 12);
            crearHorarioIfNotExists(c1k1, fisica1, DiaSemana.MIERCOLES, 8, 12);
            crearHorarioIfNotExists(c1k1, logica, DiaSemana.JUEVES, 8, 12);
            crearHorarioIfNotExists(c1k1, algoritmos, DiaSemana.VIERNES, 8, 12);
            crearHorarioIfNotExists(c1k1, arquitectura, DiaSemana.SABADO, 8, 12);
            crearHorarioIfNotExists(c1k1, sistemas, DiaSemana.LUNES, 12, 14);
            crearHorarioIfNotExists(c1k1, ingles1, DiaSemana.MIERCOLES, 12, 14);

            // 1K2
            crearHorarioIfNotExists(c1k2, analisis1, DiaSemana.LUNES, 18, 22);
            crearHorarioIfNotExists(c1k2, algebra, DiaSemana.MARTES, 18, 22);
            crearHorarioIfNotExists(c1k2, fisica1, DiaSemana.MIERCOLES, 18, 22);
            crearHorarioIfNotExists(c1k2, logica, DiaSemana.JUEVES, 18, 22);
            crearHorarioIfNotExists(c1k2, algoritmos, DiaSemana.VIERNES, 18, 22);
            crearHorarioIfNotExists(c1k2, arquitectura, DiaSemana.SABADO, 9, 13);
            crearHorarioIfNotExists(c1k2, sistemas, DiaSemana.LUNES, 22, 23);
            crearHorarioIfNotExists(c1k2, ingles1, DiaSemana.MIERCOLES, 22, 23);

            // 1K3
            crearHorarioIfNotExists(c1k3, analisis1, DiaSemana.LUNES, 13, 17);
            crearHorarioIfNotExists(c1k3, algebra, DiaSemana.MARTES, 13, 17);
            crearHorarioIfNotExists(c1k3, fisica1, DiaSemana.MIERCOLES, 13, 17);
            crearHorarioIfNotExists(c1k3, logica, DiaSemana.JUEVES, 13, 17);
            crearHorarioIfNotExists(c1k3, algoritmos, DiaSemana.VIERNES, 13, 17);
            crearHorarioIfNotExists(c1k3, arquitectura, DiaSemana.SABADO, 9, 13);
            crearHorarioIfNotExists(c1k3, sistemas, DiaSemana.MARTES, 17, 19);
            crearHorarioIfNotExists(c1k3, ingles1, DiaSemana.JUEVES, 17, 19);

            // 1K4
            crearHorarioIfNotExists(c1k4, analisis1, DiaSemana.LUNES, 8, 12);
            crearHorarioIfNotExists(c1k4, algebra, DiaSemana.MARTES, 8, 12);
            crearHorarioIfNotExists(c1k4, fisica1, DiaSemana.MIERCOLES, 8, 12);
            crearHorarioIfNotExists(c1k4, logica, DiaSemana.JUEVES, 8, 12);
            crearHorarioIfNotExists(c1k4, algoritmos, DiaSemana.VIERNES, 8, 12);
            crearHorarioIfNotExists(c1k4, arquitectura, DiaSemana.SABADO, 8, 12);
            crearHorarioIfNotExists(c1k4, sistemas, DiaSemana.VIERNES, 12, 14);
            crearHorarioIfNotExists(c1k4, ingles1, DiaSemana.MARTES, 12, 14);

            // 2K1
            crearHorarioIfNotExists(c2k1, analisis2, DiaSemana.LUNES, 14, 18);
            crearHorarioIfNotExists(c2k1, fisica2, DiaSemana.MARTES, 14, 18);
            crearHorarioIfNotExists(c2k1, sintaxis, DiaSemana.MIERCOLES, 14, 18);
            crearHorarioIfNotExists(c2k1, paradigmas, DiaSemana.JUEVES, 14, 18);
            crearHorarioIfNotExists(c2k1, sistemasOp, DiaSemana.VIERNES, 14, 18);
            crearHorarioIfNotExists(c2k1, analisisSist, DiaSemana.SABADO, 9, 13);
            crearHorarioIfNotExists(c2k1, ingSociedad, DiaSemana.LUNES, 18, 20);
            crearHorarioIfNotExists(c2k1, ingles2, DiaSemana.MIERCOLES, 18, 20);

            // 2K2
            crearHorarioIfNotExists(c2k2, analisis2, DiaSemana.LUNES, 18, 22);
            crearHorarioIfNotExists(c2k2, fisica2, DiaSemana.MARTES, 18, 22);
            crearHorarioIfNotExists(c2k2, sintaxis, DiaSemana.MIERCOLES, 18, 22);
            crearHorarioIfNotExists(c2k2, paradigmas, DiaSemana.JUEVES, 18, 22);
            crearHorarioIfNotExists(c2k2, sistemasOp, DiaSemana.VIERNES, 18, 22);
            crearHorarioIfNotExists(c2k2, analisisSist, DiaSemana.SABADO, 9, 13);
            crearHorarioIfNotExists(c2k2, ingSociedad, DiaSemana.MARTES, 22, 23);
            crearHorarioIfNotExists(c2k2, ingles2, DiaSemana.JUEVES, 22, 23);

            // 2K3
            crearHorarioIfNotExists(c2k3, analisis2, DiaSemana.LUNES, 8, 12);
            crearHorarioIfNotExists(c2k3, fisica2, DiaSemana.MARTES, 8, 12);
            crearHorarioIfNotExists(c2k3, sintaxis, DiaSemana.MIERCOLES, 8, 12);
            crearHorarioIfNotExists(c2k3, paradigmas, DiaSemana.JUEVES, 8, 12);
            crearHorarioIfNotExists(c2k3, sistemasOp, DiaSemana.VIERNES, 8, 12);
            crearHorarioIfNotExists(c2k3, analisisSist, DiaSemana.SABADO, 8, 12);
            crearHorarioIfNotExists(c2k3, ingSociedad, DiaSemana.LUNES, 12, 14);
            crearHorarioIfNotExists(c2k3, ingles2, DiaSemana.MIERCOLES, 12, 14);

            // 3K1
            crearHorarioIfNotExists(c3k1, disenio, DiaSemana.LUNES, 18, 22);
            crearHorarioIfNotExists(c3k1, basesDatos, DiaSemana.MARTES, 18, 22);
            crearHorarioIfNotExists(c3k1, comDatos, DiaSemana.MIERCOLES, 18, 22);
            crearHorarioIfNotExists(c3k1, desSoft, DiaSemana.JUEVES, 18, 22);
            crearHorarioIfNotExists(c3k1, analisisNumerico, DiaSemana.VIERNES, 18, 22);
            crearHorarioIfNotExists(c3k1, probEst, DiaSemana.SABADO, 9, 13);
            crearHorarioIfNotExists(c3k1, economia, DiaSemana.LUNES, 22, 23);

            // 3K2
            crearHorarioIfNotExists(c3k2, disenio, DiaSemana.MARTES, 18, 22);
            crearHorarioIfNotExists(c3k2, basesDatos, DiaSemana.LUNES, 18, 22);
            crearHorarioIfNotExists(c3k2, comDatos, DiaSemana.JUEVES, 18, 22);
            crearHorarioIfNotExists(c3k2, desSoft, DiaSemana.MIERCOLES, 18, 22);
            crearHorarioIfNotExists(c3k2, analisisNumerico, DiaSemana.SABADO, 9, 13);
            crearHorarioIfNotExists(c3k2, probEst, DiaSemana.VIERNES, 18, 22);
            crearHorarioIfNotExists(c3k2, economia, DiaSemana.MARTES, 22, 23);

            // 4K1
            crearHorarioIfNotExists(c4k1, tecAuto, DiaSemana.LUNES, 18, 22);
            crearHorarioIfNotExists(c4k1, redes, DiaSemana.MARTES, 18, 22);
            crearHorarioIfNotExists(c4k1, adminSist, DiaSemana.MIERCOLES, 18, 22);
            crearHorarioIfNotExists(c4k1, invOp, DiaSemana.JUEVES, 18, 22);
            crearHorarioIfNotExists(c4k1, simulacion, DiaSemana.VIERNES, 18, 22);
            crearHorarioIfNotExists(c4k1, ingCalidad, DiaSemana.SABADO, 9, 13);
            crearHorarioIfNotExists(c4k1, legislacion, DiaSemana.MIERCOLES, 22, 23);

            // 4K2
            crearHorarioIfNotExists(c4k2, tecAuto, DiaSemana.MARTES, 18, 22);
            crearHorarioIfNotExists(c4k2, redes, DiaSemana.LUNES, 18, 22);
            crearHorarioIfNotExists(c4k2, adminSist, DiaSemana.JUEVES, 18, 22);
            crearHorarioIfNotExists(c4k2, invOp, DiaSemana.MIERCOLES, 18, 22);
            crearHorarioIfNotExists(c4k2, simulacion, DiaSemana.SABADO, 9, 13);
            crearHorarioIfNotExists(c4k2, ingCalidad, DiaSemana.VIERNES, 18, 22);
            crearHorarioIfNotExists(c4k2, legislacion, DiaSemana.JUEVES, 22, 23);

            // 5K1
            crearHorarioIfNotExists(c5k1, ia, DiaSemana.LUNES, 18, 22);
            crearHorarioIfNotExists(c5k1, dataScience, DiaSemana.MARTES, 18, 22);
            crearHorarioIfNotExists(c5k1, sistGestion, DiaSemana.MIERCOLES, 18, 22);
            crearHorarioIfNotExists(c5k1, gestionGer, DiaSemana.JUEVES, 18, 22);
            crearHorarioIfNotExists(c5k1, seguridad, DiaSemana.VIERNES, 18, 22);
            crearHorarioIfNotExists(c5k1, proyectoFinal, DiaSemana.SABADO, 9, 13);

            // 5K2
            crearHorarioIfNotExists(c5k2, ia, DiaSemana.MARTES, 18, 22);
            crearHorarioIfNotExists(c5k2, dataScience, DiaSemana.LUNES, 18, 22);
            crearHorarioIfNotExists(c5k2, sistGestion, DiaSemana.JUEVES, 18, 22);
            crearHorarioIfNotExists(c5k2, gestionGer, DiaSemana.MIERCOLES, 18, 22);
            crearHorarioIfNotExists(c5k2, seguridad, DiaSemana.SABADO, 9, 13);
            crearHorarioIfNotExists(c5k2, proyectoFinal, DiaSemana.VIERNES, 18, 22);

            // Electivas
            crearHorarioIfNotExists(cElectivas, entornos, DiaSemana.LUNES, 18, 22);
            crearHorarioIfNotExists(cElectivas, emprendedores, DiaSemana.MIERCOLES, 18, 22);
            crearHorarioIfNotExists(cElectivas, metAgiles, DiaSemana.JUEVES, 18, 22);
            crearHorarioIfNotExists(cElectivas, mineria, DiaSemana.VIERNES, 18, 22);

            crearHorarioIfNotExists(cElec2, entornos, DiaSemana.LUNES, 8, 12);
            crearHorarioIfNotExists(cElec2, emprendedores, DiaSemana.MIERCOLES, 8, 12);
            crearHorarioIfNotExists(cElec2, metAgiles, DiaSemana.JUEVES, 8, 12);
            crearHorarioIfNotExists(cElec2, mineria, DiaSemana.VIERNES, 8, 12);

            // ASIGNACIONES MANUALES DE PROFESORES A MATERIAS
            
            // NICOLAS
            asignarCargoIfNotExists(profeNicolas, sistemas, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeNicolas, logica, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeNicolas, sistemasOp, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeNicolas, ia, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeNicolas, dataScience, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeNicolas, basesDatos, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeNicolas, algoritmos, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeNicolas, proyectoFinal, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeNicolas, ingSociedad, RolCargo.DOCENTE);
            asignarCargoIfNotExists(profeNicolas, entornos, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeNicolas, metAgiles, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeNicolas, mineria, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeNicolas, emprendedores, RolCargo.DOCENTE);

            // SANDRA
            asignarCargoIfNotExists(profeSandra, analisis1, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeSandra, algebra, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeSandra, probEst, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeSandra, invOp, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeSandra, analisisNumerico, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeSandra, analisis2, RolCargo.DOCENTE);

            // ROBERTO
            asignarCargoIfNotExists(profeRoberto, fisica1, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeRoberto, fisica2, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeRoberto, comDatos, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeRoberto, redes, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeRoberto, tecAuto, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeRoberto, sintaxis, RolCargo.DOCENTE);
            asignarCargoIfNotExists(profeRoberto, arquitectura, RolCargo.DOCENTE);

            // ANA
            asignarCargoIfNotExists(profeAna, ingles1, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeAna, ingles2, RolCargo.JEFE_CATEDRA);

            // CRISTIAN
            asignarCargoIfNotExists(profeCristian, arquitectura, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeCristian, paradigmas, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeCristian, desSoft, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeCristian, ingCalidad, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeCristian, simulacion, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeCristian, seguridad, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeCristian, analisis2, RolCargo.JEFE_CATEDRA);

            // LAURA
            asignarCargoIfNotExists(profeLaura, ingSociedad, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeLaura, analisisSist, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeLaura, economia, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeLaura, disenio, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeLaura, legislacion, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeLaura, adminSist, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeLaura, sistGestion, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeLaura, gestionGer, RolCargo.JEFE_CATEDRA);
            asignarCargoIfNotExists(profeLaura, entornos, RolCargo.DOCENTE);
            asignarCargoIfNotExists(profeLaura, metAgiles, RolCargo.DOCENTE);
            asignarCargoIfNotExists(profeLaura, mineria, RolCargo.DOCENTE);
            asignarCargoIfNotExists(profeLaura, emprendedores, RolCargo.DOCENTE);

            // GUSTAVO
            asignarCargoIfNotExists(profeGustavo, algoritmos, RolCargo.DOCENTE);
            asignarCargoIfNotExists(profeGustavo, sistemas, RolCargo.DOCENTE);
            asignarCargoIfNotExists(profeGustavo, paradigmas, RolCargo.DOCENTE);
            asignarCargoIfNotExists(profeGustavo, simulacion, RolCargo.DOCENTE);
            asignarCargoIfNotExists(profeGustavo, logica, RolCargo.DOCENTE);

            // CLAUDIA
            asignarCargoIfNotExists(profeClaudia, analisis1, RolCargo.DOCENTE);
            asignarCargoIfNotExists(profeClaudia, algebra, RolCargo.DOCENTE);
            asignarCargoIfNotExists(profeClaudia, arquitectura, RolCargo.DOCENTE);
            asignarCargoIfNotExists(profeClaudia, sistemasOp, RolCargo.DOCENTE);
            asignarCargoIfNotExists(profeClaudia, emprendedores, RolCargo.JEFE_CATEDRA);

            // JORGE
            asignarCargoIfNotExists(profeJorge, fisica1, RolCargo.DOCENTE);
            asignarCargoIfNotExists(profeJorge, analisisSist, RolCargo.DOCENTE);
            asignarCargoIfNotExists(profeJorge, gestionGer, RolCargo.DOCENTE);

            // VALERIA
            asignarCargoIfNotExists(profeValeria, ingles1, RolCargo.DOCENTE);
            asignarCargoIfNotExists(profeValeria, logica, RolCargo.DOCENTE);
            asignarCargoIfNotExists(profeValeria, probEst, RolCargo.DOCENTE);
            asignarCargoIfNotExists(profeValeria, economia, RolCargo.DOCENTE);

            System.out.println(">> Seeding Académico Finalizado: Comisiones, Horarios y Asignaciones.");
    }
    }

    private Salon createSalon(FacultadRegional facu, String nombre, String piso) {
        Salon s = new Salon();
        s.setFacultad(facu);
        s.setNombre(nombre);
        s.setPiso(piso);
        return salonRepository.save(s);
    }

    private Comision createComisionIfNotExists(String nombre, Integer anio, String turno, Salon salon,
                                    List<Materia> materias, List<Usuario> profes) {
        
        Comision existing = comisionRepository.findAll().stream()
                .filter(c -> c.getNombre().equals(nombre) && c.getAnio().equals(anio))
                .findFirst()
                .orElse(null);

        if (existing != null) {
            return existing;
        }

        Comision c = new Comision();
        c.setNombre(nombre);
        c.setAnio(anio);
        c.setTurno(turno);
        c.setSalon(salon);
        c.setMaterias(materias);
        c.setProfesores(profes);
        return comisionRepository.save(c);
    }

    private Materia getMateria(String nombre) {
        return materiaRepository.findByNombre(nombre)
                .orElseThrow(() -> new RuntimeException("Materia no encontrada: " + nombre));
    }

    private void crearHorarioIfNotExists(Comision com, Materia mat, DiaSemana dia, int hDesde,
                               int hHasta) {
        HorarioCursado.HorarioCursadoId id = new HorarioCursado.HorarioCursadoId(
                com.getId(), mat.getId(), dia, LocalTime.of(hDesde, 0));
        
        if (horarioCursadoRepository.existsById(id)) {
            return;
        }

        HorarioCursado horario = new HorarioCursado();
        horario.setId(id);
        horario.setHoraHasta(LocalTime.of(hHasta, 0));
        horario.setComision(com);
        horario.setMateria(mat);
        horarioCursadoRepository.save(horario);
    }

    private void asignarCargoIfNotExists(Usuario profe, Materia mat, RolCargo cargo) {
        AsignacionMateria.AsignacionMateriaId id = new AsignacionMateria.AsignacionMateriaId(profe.getId(), mat.getId());

        if (asignacionMateriaRepository.existsById(id)) {
            return;
        }

        AsignacionMateria asignacion = new AsignacionMateria();
        asignacion.setId(id);
        asignacion.setCargo(cargo);
        asignacion.setProfesor(profe);
        asignacion.setMateria(mat);
        asignacionMateriaRepository.save(asignacion);
    }
}
