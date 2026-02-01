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
            Comision c1k1 = createComision("1K1", 2025, "MAÑANA", lab305,
                    Arrays.asList(analisis1, algebra, fisica1, ingles1, logica, algoritmos, arquitectura, sistemas),
                    List.of(profeNicolas, profeAna, profeSandra, profeRoberto, profeGustavo, profeCristian, profeValeria));

            // 1K2 (Todas las de Primero - Noche)
            Comision c1k2 = createComision("1K2", 2025, "NOCHE", aula401,
                    Arrays.asList(analisis1, algebra, fisica1, ingles1, logica, algoritmos, arquitectura, sistemas),
                    List.of(profeGustavo, profeClaudia, profeJorge, profeValeria));

            // 2K1 (Todas las de Segundo - Tarde)
            Comision c2k1 = createComision("2K1", 2025, "TARDE", aula201,
                    Arrays.asList(analisis2, fisica2, ingSociedad, ingles2, sintaxis, paradigmas, sistemasOp, analisisSist),
                    List.of(profeCristian, profeRoberto, profeLaura, profeAna, profeNicolas, profeGustavo, profeClaudia, profeJorge));

            // 3K1 (Todas las de Tercero - Noche)
            Comision c3k1 = createComision("3K1", 2025, "NOCHE", lab305,
                    Arrays.asList(probEst, economia, basesDatos, desSoft, comDatos, analisisNumerico, disenio),
                    List.of(profeSandra, profeValeria, profeLaura, profeNicolas, profeCristian, profeRoberto));

            // 4K1 (Todas las de Cuarto - Noche)
            Comision c4k1 = createComision("4K1", 2025, "NOCHE", aulaMagna,
                    Arrays.asList(legislacion, ingCalidad, redes, invOp, simulacion, tecAuto, adminSist),
                    List.of(profeLaura, profeCristian, profeRoberto, profeSandra, profeGustavo));

            // 5K1 (Todas las de Quinto - Noche)
            Comision c5k1 = createComision("5K1", 2025, "NOCHE", aulaMagna,
                    Arrays.asList(ia, dataScience, sistGestion, gestionGer, seguridad, proyectoFinal),
                    List.of(profeNicolas, profeLaura, profeJorge, profeCristian));

            // Electivas
            Comision cElectivas = createComision("Electivas 2025 - Noche", 2025, "NOCHE", aulaMagna,
                    Arrays.asList(entornos, emprendedores, metAgiles, mineria),
                    List.of(profeNicolas, profeClaudia));

            // NUEVAS COMISIONES MASIVAS
            Comision c1k3 = createComision("1K3", 2025, "TARDE", aula101,
                    Arrays.asList(analisis1, algebra, fisica1, ingles1, logica, algoritmos, arquitectura, sistemas),
                    List.of(profeLaura, profeNicolas, profeSandra, profeRoberto, profeAna, profeCristian));
            
            Comision c1k4 = createComision("1K4", 2025, "MAÑANA", aula301,
                    Arrays.asList(analisis1, algebra, fisica1, ingles1, logica, algoritmos, arquitectura, sistemas),
                    List.of(profeCristian, profeGustavo, profeSandra, profeRoberto, profeAna));

            Comision c2k2 = createComision("2K2", 2025, "NOCHE", aula402,
                    Arrays.asList(analisis2, fisica2, ingSociedad, ingles2, sintaxis, paradigmas, sistemasOp, analisisSist),
                    List.of(profeRoberto, profeAna, profeJorge, profeCristian, profeNicolas, profeLaura));

            Comision c2k3 = createComision("2K3", 2025, "MAÑANA", aulaMagna,
                    Arrays.asList(analisis2, fisica2, ingSociedad, ingles2, sintaxis, paradigmas, sistemasOp, analisisSist),
                    List.of(profeSandra, profeClaudia, profeRoberto, profeAna, profeCristian, profeNicolas, profeLaura));

            Comision c3k2 = createComision("3K2", 2025, "NOCHE", sum,
                    Arrays.asList(probEst, economia, basesDatos, desSoft, comDatos, analisisNumerico, disenio),
                    List.of(profeLaura, profeSandra, profeValeria, profeCristian, profeRoberto, profeNicolas));

            Comision c4k2 = createComision("4K2", 2025, "NOCHE", aula201,
                    Arrays.asList(legislacion, ingCalidad, redes, invOp, simulacion, tecAuto, adminSist),
                    List.of(profeRoberto, profeLaura, profeGustavo, profeCristian, profeSandra));

            Comision c5k2 = createComision("5K2", 2025, "NOCHE", aula402,
                    Arrays.asList(ia, dataScience, sistGestion, gestionGer, seguridad, proyectoFinal),
                    List.of(profeCristian, profeJorge, profeNicolas, profeLaura));

            Comision cElec2 = createComision("Electivas 2025 - Mañana", 2025, "MAÑANA", aula101,
                    Arrays.asList(entornos, emprendedores, metAgiles, mineria),
                    List.of(profeLaura, profeClaudia, profeNicolas));


            // -------------------------------------------------------------------------
            // HORARIOS
            // -------------------------------------------------------------------------

            // 1K1
            crearHorario(c1k1, analisis1, DiaSemana.LUNES, 8, 12);
            crearHorario(c1k1, algebra, DiaSemana.MARTES, 8, 12);
            crearHorario(c1k1, fisica1, DiaSemana.MIERCOLES, 8, 12);
            crearHorario(c1k1, logica, DiaSemana.JUEVES, 8, 12);
            crearHorario(c1k1, algoritmos, DiaSemana.VIERNES, 8, 12);
            crearHorario(c1k1, arquitectura, DiaSemana.SABADO, 8, 12);
            crearHorario(c1k1, sistemas, DiaSemana.LUNES, 12, 14);
            crearHorario(c1k1, ingles1, DiaSemana.MIERCOLES, 12, 14);

            // 1K2
            crearHorario(c1k2, analisis1, DiaSemana.LUNES, 18, 22);
            crearHorario(c1k2, algebra, DiaSemana.MARTES, 18, 22);
            crearHorario(c1k2, fisica1, DiaSemana.MIERCOLES, 18, 22);
            crearHorario(c1k2, logica, DiaSemana.JUEVES, 18, 22);
            crearHorario(c1k2, algoritmos, DiaSemana.VIERNES, 18, 22);
            crearHorario(c1k2, arquitectura, DiaSemana.SABADO, 9, 13);
            crearHorario(c1k2, sistemas, DiaSemana.LUNES, 22, 23);
            crearHorario(c1k2, ingles1, DiaSemana.MIERCOLES, 22, 23);

            // 1K3
            crearHorario(c1k3, analisis1, DiaSemana.LUNES, 13, 17);
            crearHorario(c1k3, algebra, DiaSemana.MARTES, 13, 17);
            crearHorario(c1k3, fisica1, DiaSemana.MIERCOLES, 13, 17);
            crearHorario(c1k3, logica, DiaSemana.JUEVES, 13, 17);
            crearHorario(c1k3, algoritmos, DiaSemana.VIERNES, 13, 17);
            crearHorario(c1k3, arquitectura, DiaSemana.SABADO, 9, 13);
            crearHorario(c1k3, sistemas, DiaSemana.MARTES, 17, 19);
            crearHorario(c1k3, ingles1, DiaSemana.JUEVES, 17, 19);

            // 1K4
            crearHorario(c1k4, analisis1, DiaSemana.LUNES, 8, 12);
            crearHorario(c1k4, algebra, DiaSemana.MARTES, 8, 12);
            crearHorario(c1k4, fisica1, DiaSemana.MIERCOLES, 8, 12);
            crearHorario(c1k4, logica, DiaSemana.JUEVES, 8, 12);
            crearHorario(c1k4, algoritmos, DiaSemana.VIERNES, 8, 12);
            crearHorario(c1k4, arquitectura, DiaSemana.SABADO, 8, 12);
            crearHorario(c1k4, sistemas, DiaSemana.VIERNES, 12, 14);
            crearHorario(c1k4, ingles1, DiaSemana.MARTES, 12, 14);

            // 2K1
            crearHorario(c2k1, analisis2, DiaSemana.LUNES, 14, 18);
            crearHorario(c2k1, fisica2, DiaSemana.MARTES, 14, 18);
            crearHorario(c2k1, sintaxis, DiaSemana.MIERCOLES, 14, 18);
            crearHorario(c2k1, paradigmas, DiaSemana.JUEVES, 14, 18);
            crearHorario(c2k1, sistemasOp, DiaSemana.VIERNES, 14, 18);
            crearHorario(c2k1, analisisSist, DiaSemana.SABADO, 9, 13);
            crearHorario(c2k1, ingSociedad, DiaSemana.LUNES, 18, 20);
            crearHorario(c2k1, ingles2, DiaSemana.MIERCOLES, 18, 20);

            // 2K2
            crearHorario(c2k2, analisis2, DiaSemana.LUNES, 18, 22);
            crearHorario(c2k2, fisica2, DiaSemana.MARTES, 18, 22);
            crearHorario(c2k2, sintaxis, DiaSemana.MIERCOLES, 18, 22);
            crearHorario(c2k2, paradigmas, DiaSemana.JUEVES, 18, 22);
            crearHorario(c2k2, sistemasOp, DiaSemana.VIERNES, 18, 22);
            crearHorario(c2k2, analisisSist, DiaSemana.SABADO, 9, 13);
            crearHorario(c2k2, ingSociedad, DiaSemana.MARTES, 22, 23);
            crearHorario(c2k2, ingles2, DiaSemana.JUEVES, 22, 23);

            // 2K3
            crearHorario(c2k3, analisis2, DiaSemana.LUNES, 8, 12);
            crearHorario(c2k3, fisica2, DiaSemana.MARTES, 8, 12);
            crearHorario(c2k3, sintaxis, DiaSemana.MIERCOLES, 8, 12);
            crearHorario(c2k3, paradigmas, DiaSemana.JUEVES, 8, 12);
            crearHorario(c2k3, sistemasOp, DiaSemana.VIERNES, 8, 12);
            crearHorario(c2k3, analisisSist, DiaSemana.SABADO, 8, 12);
            crearHorario(c2k3, ingSociedad, DiaSemana.LUNES, 12, 14);
            crearHorario(c2k3, ingles2, DiaSemana.MIERCOLES, 12, 14);

            // 3K1
            crearHorario(c3k1, disenio, DiaSemana.LUNES, 18, 22);
            crearHorario(c3k1, basesDatos, DiaSemana.MARTES, 18, 22);
            crearHorario(c3k1, comDatos, DiaSemana.MIERCOLES, 18, 22);
            crearHorario(c3k1, desSoft, DiaSemana.JUEVES, 18, 22);
            crearHorario(c3k1, analisisNumerico, DiaSemana.VIERNES, 18, 22);
            crearHorario(c3k1, probEst, DiaSemana.SABADO, 9, 13);
            crearHorario(c3k1, economia, DiaSemana.LUNES, 22, 23);

            // 3K2
            crearHorario(c3k2, disenio, DiaSemana.MARTES, 18, 22);
            crearHorario(c3k2, basesDatos, DiaSemana.LUNES, 18, 22);
            crearHorario(c3k2, comDatos, DiaSemana.JUEVES, 18, 22);
            crearHorario(c3k2, desSoft, DiaSemana.MIERCOLES, 18, 22);
            crearHorario(c3k2, analisisNumerico, DiaSemana.SABADO, 9, 13);
            crearHorario(c3k2, probEst, DiaSemana.VIERNES, 18, 22);
            crearHorario(c3k2, economia, DiaSemana.MARTES, 22, 23);

            // 4K1
            crearHorario(c4k1, tecAuto, DiaSemana.LUNES, 18, 22);
            crearHorario(c4k1, redes, DiaSemana.MARTES, 18, 22);
            crearHorario(c4k1, adminSist, DiaSemana.MIERCOLES, 18, 22);
            crearHorario(c4k1, invOp, DiaSemana.JUEVES, 18, 22);
            crearHorario(c4k1, simulacion, DiaSemana.VIERNES, 18, 22);
            crearHorario(c4k1, ingCalidad, DiaSemana.SABADO, 9, 13);
            crearHorario(c4k1, legislacion, DiaSemana.MIERCOLES, 22, 23);

            // 4K2
            crearHorario(c4k2, tecAuto, DiaSemana.MARTES, 18, 22);
            crearHorario(c4k2, redes, DiaSemana.LUNES, 18, 22);
            crearHorario(c4k2, adminSist, DiaSemana.JUEVES, 18, 22);
            crearHorario(c4k2, invOp, DiaSemana.MIERCOLES, 18, 22);
            crearHorario(c4k2, simulacion, DiaSemana.SABADO, 9, 13);
            crearHorario(c4k2, ingCalidad, DiaSemana.VIERNES, 18, 22);
            crearHorario(c4k2, legislacion, DiaSemana.JUEVES, 22, 23);

            // 5K1
            crearHorario(c5k1, ia, DiaSemana.LUNES, 18, 22);
            crearHorario(c5k1, dataScience, DiaSemana.MARTES, 18, 22);
            crearHorario(c5k1, sistGestion, DiaSemana.MIERCOLES, 18, 22);
            crearHorario(c5k1, gestionGer, DiaSemana.JUEVES, 18, 22);
            crearHorario(c5k1, seguridad, DiaSemana.VIERNES, 18, 22);
            crearHorario(c5k1, proyectoFinal, DiaSemana.SABADO, 9, 13);

            // 5K2
            crearHorario(c5k2, ia, DiaSemana.MARTES, 18, 22);
            crearHorario(c5k2, dataScience, DiaSemana.LUNES, 18, 22);
            crearHorario(c5k2, sistGestion, DiaSemana.JUEVES, 18, 22);
            crearHorario(c5k2, gestionGer, DiaSemana.MIERCOLES, 18, 22);
            crearHorario(c5k2, seguridad, DiaSemana.SABADO, 9, 13);
            crearHorario(c5k2, proyectoFinal, DiaSemana.VIERNES, 18, 22);

            // Electivas
            crearHorario(cElectivas, entornos, DiaSemana.LUNES, 18, 22);
            crearHorario(cElectivas, emprendedores, DiaSemana.MIERCOLES, 18, 22);
            crearHorario(cElectivas, metAgiles, DiaSemana.JUEVES, 18, 22);
            crearHorario(cElectivas, mineria, DiaSemana.VIERNES, 18, 22);

            crearHorario(cElec2, entornos, DiaSemana.LUNES, 8, 12);
            crearHorario(cElec2, emprendedores, DiaSemana.MIERCOLES, 8, 12);
            crearHorario(cElec2, metAgiles, DiaSemana.JUEVES, 8, 12);
            crearHorario(cElec2, mineria, DiaSemana.VIERNES, 8, 12);

            // ASIGNACIONES MANUALES DE PROFESORES A MATERIAS
            
            // NICOLAS
            asignarCargo(profeNicolas, sistemas, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeNicolas, logica, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeNicolas, sistemasOp, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeNicolas, ia, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeNicolas, dataScience, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeNicolas, basesDatos, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeNicolas, algoritmos, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeNicolas, proyectoFinal, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeNicolas, ingSociedad, RolCargo.DOCENTE);
            asignarCargo(profeNicolas, entornos, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeNicolas, metAgiles, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeNicolas, mineria, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeNicolas, emprendedores, RolCargo.DOCENTE);

            // SANDRA
            asignarCargo(profeSandra, analisis1, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeSandra, algebra, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeSandra, probEst, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeSandra, invOp, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeSandra, analisisNumerico, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeSandra, analisis2, RolCargo.DOCENTE);

            // ROBERTO
            asignarCargo(profeRoberto, fisica1, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeRoberto, fisica2, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeRoberto, comDatos, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeRoberto, redes, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeRoberto, tecAuto, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeRoberto, sintaxis, RolCargo.DOCENTE);
            asignarCargo(profeRoberto, arquitectura, RolCargo.DOCENTE);

            // ANA
            asignarCargo(profeAna, ingles1, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeAna, ingles2, RolCargo.JEFE_CATEDRA);

            // CRISTIAN
            asignarCargo(profeCristian, arquitectura, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeCristian, paradigmas, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeCristian, desSoft, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeCristian, ingCalidad, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeCristian, simulacion, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeCristian, seguridad, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeCristian, analisis2, RolCargo.JEFE_CATEDRA);

            // LAURA
            asignarCargo(profeLaura, ingSociedad, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeLaura, analisisSist, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeLaura, economia, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeLaura, disenio, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeLaura, legislacion, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeLaura, adminSist, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeLaura, sistGestion, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeLaura, gestionGer, RolCargo.JEFE_CATEDRA);
            asignarCargo(profeLaura, entornos, RolCargo.DOCENTE);
            asignarCargo(profeLaura, metAgiles, RolCargo.DOCENTE);
            asignarCargo(profeLaura, mineria, RolCargo.DOCENTE);
            asignarCargo(profeLaura, emprendedores, RolCargo.DOCENTE);

            // GUSTAVO
            asignarCargo(profeGustavo, algoritmos, RolCargo.DOCENTE);
            asignarCargo(profeGustavo, sistemas, RolCargo.DOCENTE);
            asignarCargo(profeGustavo, paradigmas, RolCargo.DOCENTE);
            asignarCargo(profeGustavo, simulacion, RolCargo.DOCENTE);
            asignarCargo(profeGustavo, logica, RolCargo.DOCENTE);

            // CLAUDIA
            asignarCargo(profeClaudia, analisis1, RolCargo.DOCENTE);
            asignarCargo(profeClaudia, algebra, RolCargo.DOCENTE);
            asignarCargo(profeClaudia, arquitectura, RolCargo.DOCENTE);
            asignarCargo(profeClaudia, sistemasOp, RolCargo.DOCENTE);
            asignarCargo(profeClaudia, emprendedores, RolCargo.JEFE_CATEDRA);

            // JORGE
            asignarCargo(profeJorge, fisica1, RolCargo.DOCENTE);
            asignarCargo(profeJorge, analisisSist, RolCargo.DOCENTE);
            asignarCargo(profeJorge, gestionGer, RolCargo.DOCENTE);

            // VALERIA
            asignarCargo(profeValeria, ingles1, RolCargo.DOCENTE);
            asignarCargo(profeValeria, logica, RolCargo.DOCENTE);
            asignarCargo(profeValeria, probEst, RolCargo.DOCENTE);
            asignarCargo(profeValeria, economia, RolCargo.DOCENTE);

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

    private Comision createComision(String nombre, Integer anio, String turno, Salon salon,
                                    List<Materia> materias, List<Usuario> profes) {
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

    private void crearHorario(Comision com, Materia mat, DiaSemana dia, int hDesde,
                              int hHasta) {
        HorarioCursado horario = new HorarioCursado();
        HorarioCursado.HorarioCursadoId id = new HorarioCursado.HorarioCursadoId(
                com.getId(), mat.getId(), dia, LocalTime.of(hDesde, 0));
        horario.setId(id);
        horario.setHoraHasta(LocalTime.of(hHasta, 0));
        horario.setComision(com);
        horario.setMateria(mat);
        horarioCursadoRepository.save(horario);
    }

    private void asignarCargo(Usuario profe, Materia mat, RolCargo cargo) {
        AsignacionMateria asignacion = new AsignacionMateria();
        asignacion.setId(new AsignacionMateria.AsignacionMateriaId(profe.getId(), mat.getId()));
        asignacion.setCargo(cargo);
        asignacion.setProfesor(profe);
        asignacion.setMateria(mat);
        asignacionMateriaRepository.save(asignacion);
    }
}
