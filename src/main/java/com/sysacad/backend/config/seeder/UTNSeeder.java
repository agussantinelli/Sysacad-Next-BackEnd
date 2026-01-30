package com.sysacad.backend.config.seeder;

import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.enums.*;
import com.sysacad.backend.repository.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Component
public class UTNSeeder {

    private final FacultadRegionalRepository facultadRepository;
    private final CarreraRepository carreraRepository;
    private final MateriaRepository materiaRepository;
    private final PlanDeEstudioRepository planRepository;
    private final PlanMateriaRepository planMateriaRepository;

    private final Map<String, Materia> materiasPersistidas = new HashMap<>();
    private final Map<String, Materia> mapaCodigoMateria = new HashMap<>();

    public UTNSeeder(FacultadRegionalRepository facultadRepository,
            CarreraRepository carreraRepository,
            MateriaRepository materiaRepository,
            PlanDeEstudioRepository planRepository,
            PlanMateriaRepository planMateriaRepository) {
        this.facultadRepository = facultadRepository;
        this.carreraRepository = carreraRepository;
        this.materiaRepository = materiaRepository;
        this.planRepository = planRepository;
        this.planMateriaRepository = planMateriaRepository;
    }

    @Transactional
    public void seed() {
        if (facultadRepository.count() > 0) {
            System.out.println(">> UTNSeeder: La base de datos ya tiene datos. Saltando carga.");
            return;
        }

        System.out
                .println(">> UTNSeeder: Iniciando carga masiva de planes y correlativas (Dataset Definitivo Final)...");

        // Facultad
        FacultadRegional frro = new FacultadRegional();
        frro.setCiudad("Rosario");
        frro.setProvincia("Santa Fe");
        frro = facultadRepository.save(frro);

        // Cargar Datos
        cargarCarrera(frro, 1, "ISI", "Ingeniería en Sistemas de Información", 20, getDatasetISI());
        cargarCarrera(frro, 2, "IC", "Ingeniería Civil", 11, getDatasetCivil());
        cargarCarrera(frro, 3, "IQ", "Ingeniería Química", 12, getDatasetQuimica());
        cargarCarrera(frro, 4, "IM", "Ingeniería Mecánica", 10, getDatasetMecanica());
        cargarCarrera(frro, 5, "IEE", "Ingeniería en Energía Eléctrica", 10, getDatasetElectrica());

        // Conectar Correlativas
        conectarCorrelativas(1, getDatasetISI());
        conectarCorrelativas(2, getDatasetCivil());
        conectarCorrelativas(3, getDatasetQuimica());
        conectarCorrelativas(4, getDatasetMecanica());
        conectarCorrelativas(5, getDatasetElectrica());

        System.out.println(">> UTNSeeder: Carga completa finalizada con éxito.");
    }

    private void cargarCarrera(FacultadRegional facu, Integer nroCarrera, String aliasCarrera, String nombreCarrera, Integer horasElectivas,
            List<MateriaDef> materias) {
        System.out.println("   -> Procesando Carrera: " + nombreCarrera);

        // Crear Carrera
        Carrera carrera = new Carrera();
        carrera.setNombre(nombreCarrera);
        carrera.setAlias(aliasCarrera);
        carrera.setHorasElectivasRequeridas(horasElectivas);
        
        Set<FacultadRegional> facultades = new HashSet<>();
        facultades.add(facu);
        carrera.setFacultades(facultades);
        
        carrera = carreraRepository.save(carrera);

        // Crear Plan
        PlanDeEstudio plan = new PlanDeEstudio();
        plan.setId(new PlanDeEstudio.PlanId(carrera.getId(), 2023));
        plan.setNombre("Plan 2023");
        plan.setFechaInicio(LocalDate.of(2023, 3, 1));
        plan.setEsVigente(true);
        plan = planRepository.save(plan);

        for (MateriaDef def : materias) {
            Materia materia = materiasPersistidas.get(def.nombre);

            if (materia == null) {
                Optional<Materia> existing = materiaRepository.findByNombre(def.nombre);
                if (existing.isPresent()) {
                    materia = existing.get();
                } else {
                    materia = new Materia();
                    materia.setNombre(def.nombre);
                    materia.setHorasCursado(def.horas);
                    materia.setDuracion(def.duracion);
                    materia.setCuatrimestreDictado(def.cuatrimestreDictado);
                    materia.setTipoMateria(def.tipo);
                    materia.setOptativa(def.esElectiva);
                    materia.setRendirLibre(false);
                    materia = materiaRepository.save(materia);
                    materiasPersistidas.put(def.nombre, materia);
                }
            }

            mapaCodigoMateria.put(nroCarrera + "-" + def.codigo, materia);

            PlanMateria pm = new PlanMateria();
            pm.setId(new PlanMateria.PlanMateriaId(carrera.getId(), 2023, materia.getId()));
            pm.setMateria(materia);
            pm.setPlan(plan);
            pm.setCodigoMateria(def.codigo);
            pm.setNivel(def.nivel);

            planMateriaRepository.save(pm);
        }
    }

    private void conectarCorrelativas(Integer nroCarrera, List<MateriaDef> materias) {
        for (MateriaDef def : materias) {
            Materia materiaActual = mapaCodigoMateria.get(nroCarrera + "-" + def.codigo);
            if (materiaActual == null)
                continue;

            Set<Correlatividad> correlatividades = new HashSet<>();

            // Regulares
            if (def.correlativasRegulares != null) {
                for (String codCorr : def.correlativasRegulares) {
                    Materia matCorr = mapaCodigoMateria.get(nroCarrera + "-" + codCorr);
                    if (matCorr != null) {
                         correlatividades.add(new Correlatividad(materiaActual, matCorr, TipoCorrelatividad.REGULAR));
                    } else {
                         // Manejo de caso especial "3er Año Completo" u otros strings no mapeables si existieran
                         if(!codCorr.contains("Año")) {
                             System.out.println("      [WARN] (" + nroCarrera + ") No se encontró correlativa REG '" + codCorr + "' para " + def.nombre);
                         }
                    }
                }
            }

            // Promocionadas
            if (def.correlativasPromocionadas != null) {
                for (String codCorr : def.correlativasPromocionadas) {
                    Materia matCorr = mapaCodigoMateria.get(nroCarrera + "-" + codCorr);
                    if (matCorr != null) {
                        correlatividades.add(new Correlatividad(materiaActual, matCorr, TipoCorrelatividad.PROMOCIONADA));
                    } else {
                        System.out.println("      [WARN] (" + nroCarrera + ") No se encontró correlativa PROMO '" + codCorr + "' para " + def.nombre);
                    }
                }
            }
            
            if(!correlatividades.isEmpty()) {
                materiaActual.setCorrelativas(correlatividades);
                materiaRepository.save(materiaActual);
            }
        }
    }

    @Data
    @AllArgsConstructor
    private static class MateriaDef {
        String codigo;
        String nombre;
        Short horas;
        DuracionMateria duracion;
        CuatrimestreDictado cuatrimestreDictado;
        Short nivel;
        TipoMateria tipo;
        boolean esElectiva;
        List<String> correlativasRegulares;
        List<String> correlativasPromocionadas;

        public MateriaDef(String codigo, String nombre, int horas, String dictadoInfo, int nivel, boolean esElectiva,
                List<String> correlativasRegulares, List<String> correlativasPromocionadas) {

        public MateriaDef(String codigo, String nombre, int horas, String dictadoInfo, int nivel, boolean esElectiva,
                List<String> correlativas) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.horas = (short) horas;

            // Determinar Duración y Cuatrimestre
            String infoUpper = dictadoInfo != null ? dictadoInfo.toUpperCase() : "ANUAL";

            if (infoUpper.contains("ANUAL")) {
                this.duracion = DuracionMateria.ANUAL;
                this.cuatrimestreDictado = CuatrimestreDictado.ANUAL;
            } else {
                this.duracion = DuracionMateria.CUATRIMESTRAL;
                if (infoUpper.contains("1") && infoUpper.contains("2")) {
                    this.cuatrimestreDictado = CuatrimestreDictado.AMBOS;
                } else if (infoUpper.contains("1")) {
                    this.cuatrimestreDictado = CuatrimestreDictado.PRIMERO;
                } else if (infoUpper.contains("2")) {
                    this.cuatrimestreDictado = CuatrimestreDictado.SEGUNDO;
                } else {
                    // Por defecto si es cuatrimestral y no se especifica, asumimos AMBOS para mayor disponibilidad
                    this.cuatrimestreDictado = CuatrimestreDictado.AMBOS;
                }
            }

            this.nivel = (short) nivel;

            if (esElectiva) {
                this.tipo = TipoMateria.ESPECIFICA;
            } else {
                this.tipo = nivel <= 2 ? TipoMateria.BASICA : TipoMateria.ESPECIFICA;
            }

            this.esElectiva = esElectiva;
            this.correlativasRegulares = correlativasRegulares;
            this.correlativasPromocionadas = correlativasPromocionadas;
        }
    }

    // INGENIERÍA EN SISTEMAS DE INFORMACIÓN (ISI)
    private List<MateriaDef> getDatasetISI() {
        return List.of(
                // Nivel 1
                new MateriaDef("1", "Análisis Matemático I", 5, "ANUAL", 1, false, null, null),
                new MateriaDef("2", "Álgebra y Geometría Analítica", 5, "ANUAL", 1, false, null, null),
                new MateriaDef("3", "Física I", 5, "ANUAL", 1, false, null, null),
                new MateriaDef("4", "Inglés I", 2, "ANUAL", 1, false, null, null),
                new MateriaDef("5", "Lógica y Estructuras Discretas", 3, "ANUAL", 1, false, null, null),
                new MateriaDef("6", "Algoritmos y Estructuras de Datos", 5, "ANUAL", 1, false, null, null),
                new MateriaDef("7", "Arquitectura de Computadoras", 4, "ANUAL", 1, false, null, null),
                new MateriaDef("8", "Sistemas y Procesos de Negocio", 3, "ANUAL", 1, false, null, null),

                // Nivel 2
                new MateriaDef("9", "Análisis Matemático II", 5, "ANUAL", 2, false, List.of("1", "2"), null),
                new MateriaDef("10", "Física II", 5, "ANUAL", 2, false, List.of("1", "3"), null),
                new MateriaDef("11", "Ingeniería y Sociedad", 2, "ANUAL", 2, false, null, null),
                new MateriaDef("12", "Inglés II", 2, "ANUAL", 2, false, List.of("4"), null),
                new MateriaDef("13", "Sintaxis y Semántica de los Lenguajes", 4, "ANUAL", 2, false, List.of("5", "6"), null),
                new MateriaDef("14", "Paradigmas de Programación", 4, "ANUAL", 2, false, List.of("5", "6"), null),
                new MateriaDef("15", "Sistemas Operativos", 4, "ANUAL", 2, false, List.of("7"), null),
                new MateriaDef("16", "Análisis de Sistemas de Información", 6, "ANUAL", 2, false, List.of("6", "8"), null),

                // Nivel 3
                new MateriaDef("17", "Probabilidad y Estadística", 3, "ANUAL", 3, false, List.of("1", "2"), null),
                new MateriaDef("18", "Economía", 3, "ANUAL", 3, false, List.of("1", "2"), null),
                new MateriaDef("19", "Bases de Datos", 4, "ANUAL", 3, false, List.of("13", "16"), List.of("5", "6")),
                new MateriaDef("20", "Desarrollo de Software", 4, "ANUAL", 3, false, List.of("14", "16"), List.of("5", "6")),
                new MateriaDef("21", "Comunicación de Datos", 4, "ANUAL", 3, false, List.of("3", "7"), null),
                new MateriaDef("22", "Análisis Numérico", 3, "ANUAL", 3, false, List.of("1", "2"), null),
                new MateriaDef("23", "Diseño de Sistemas de Información", 6, "ANUAL", 3, false, List.of("14", "16"), List.of("9")),

                // Nivel 4
                new MateriaDef("24", "Legislación", 2, "ANUAL", 4, false, List.of("11"), null),
                new MateriaDef("25", "Ingeniería y Calidad de Software", 3, "ANUAL", 4, false, List.of("19", "20", "23"), List.of("13", "14")),
                new MateriaDef("26", "Redes de Datos", 4, "ANUAL", 4, false, List.of("15", "21"), null),
                new MateriaDef("27", "Investigación Operativa", 4, "ANUAL", 4, false, List.of("17", "22"), null),
                new MateriaDef("28", "Simulación", 3, "ANUAL", 4, false, List.of("17"), List.of("9")),
                new MateriaDef("29", "Tecnologías para la Automatización", 3, "ANUAL", 4, false, List.of("10", "22"), List.of("9")),
                new MateriaDef("30", "Administración de Sistemas de Información", 6, "ANUAL", 4, false, List.of("18", "23"), List.of("16")),

                // Nivel 5
                new MateriaDef("31", "Inteligencia Artificial", 3, "ANUAL", 5, false, List.of("28"), List.of("17", "22")),
                new MateriaDef("32", "Ciencia de Datos", 3, "ANUAL", 5, false, List.of("28"), List.of("17", "19")),
                new MateriaDef("33", "Sistemas de Gestión", 4, "ANUAL", 5, false, List.of("18", "27"), List.of("23")),
                new MateriaDef("34", "Gestión Gerencial", 3, "ANUAL", 5, false, List.of("24", "30"), List.of("18")),
                new MateriaDef("35", "Seguridad en los Sistemas de Información", 3, "ANUAL", 5, false, List.of("26", "30"), List.of("20", "21")),
                new MateriaDef("36", "Proyecto Final", 6, "ANUAL", 5, false, List.of("25", "26", "30"), List.of("12", "20", "23")),

                // ELECTIVAS ISI
                new MateriaDef("37", "Entornos Gráficos", 4, "1 y 2 C", 2, true, List.of("5"), List.of("6", "8")),
                new MateriaDef("38", "Análisis y Diseño de Datos e Información", 3, "1 y 2 C", 2, true, List.of("8", "13"), List.of("6")),
                new MateriaDef("39", "Sistemas de Información Geográfica", 3, "2 C", 2, true, List.of("6", "1"), List.of("2")),
                new MateriaDef("40", "Formación de Emprendedores", 4, "1 y 2 C", 2, true, null, List.of("2")),
                new MateriaDef("41", "Algoritmos Genéticos", 4, "ANUAL", 3, true, List.of("13", "14"), List.of("5", "6", "7", "8")),
                new MateriaDef("42", "Información Jurídica", 3, "1 y 2 C", 3, true, List.of("15"), List.of("6", "7", "8")),
                new MateriaDef("43", "Lenguaje de Programación JAVA", 4, "ANUAL", 3, true, null, List.of("14")),
                new MateriaDef("44", "Tecnologías de Desarrollo de Software IDE", 4, "ANUAL", 3, true, null, List.of("5", "13", "14")),
                new MateriaDef("45", "Gestión Ingenieril", 4, "1 C", 3, true, null, List.of("8")),
                new MateriaDef("46", "Introducción a la Práctica Profesional", 4, "ANUAL", 3, true, null, List.of("16")),
                new MateriaDef("47", "Química Aplicada a la Informática", 4, "1 C", 3, true, List.of("4", "5", "6", "7", "8"), List.of("1", "2", "3")),
                new MateriaDef("48", "Infraestructura Tecnológica", 4, "CUATRIMESTRAL", 4, true, List.of("16"), List.of("15")),
                new MateriaDef("49", "Soporte a las Bases de Datos con PV", 4, "ANUAL", 4, true, List.of("19"), List.of("19")),
                new MateriaDef("50", "Metodología de la Investigación", 4, "CUATRIMESTRAL", 4, true, null, List.of("17")),
                new MateriaDef("51", "Metodologías Ágiles", 3, "CUATRIMESTRAL", 4, true, List.of("25"), List.of("14", "16")),
                new MateriaDef("52", "Fabricación Aditiva", 3, "ANUAL", 5, true, List.of("28", "29", "30"), List.of("28", "29", "30")),
                new MateriaDef("53", "Dirección de Recursos Humanos", 3, "CUATRIMESTRAL", 5, true, null, List.of("30")),
                new MateriaDef("54", "Informática en la Administración Pública", 4, "CUATRIMESTRAL", 5, true, null, List.of("16")),
                new MateriaDef("55", "Sistemas de Información Integrados", 4, "CUATRIMESTRAL", 5, true, List.of("25", "27", "30"), List.of("25", "27", "30")),
                new MateriaDef("56", "Minería de Datos", 6, "CUATRIMESTRAL", 5, true, List.of("28"), List.of("17", "19")));
    }

    // INGENIERÍA CIVIL (IC)
    private List<MateriaDef> getDatasetCivil() {
        return List.of(
                // NIVEL 1
                new MateriaDef("1", "Análisis Matemático I", 5, "ANUAL", 1, false, null, null),
                new MateriaDef("2", "Álgebra y Geometría Analítica", 5, "ANUAL", 1, false, null, null),
                new MateriaDef("3", "Ingeniería y Sociedad", 2, "ANUAL", 1, false, null, null),
                new MateriaDef("4", "Ingeniería Civil I", 3, "ANUAL", 1, false, null, null),
                new MateriaDef("5", "Sistemas de Representación", 3, "ANUAL", 1, false, null, null),
                new MateriaDef("6", "Química General", 5, "ANUAL", 1, false, null, null),
                new MateriaDef("7", "Física I", 5, "ANUAL", 1, false, null, null),
                new MateriaDef("8", "Fundamentos de Informática", 2, "ANUAL", 1, false, null, null),

                // NIVEL 2
                new MateriaDef("9", "Análisis Matemático II", 5, "ANUAL", 2, false, List.of("1", "2"), null),
                new MateriaDef("10", "Estabilidad", 5, "ANUAL", 2, false, List.of("1", "2", "5", "7", "8"), null),
                new MateriaDef("11", "Ingeniería Civil II", 3, "ANUAL", 2, false, List.of("3", "4", "5", "8"), null),
                new MateriaDef("12", "Tecnología de los Materiales", 4, "ANUAL", 2, false, List.of("1", "5", "6", "7"), null),
                new MateriaDef("13", "Física II", 5, "ANUAL", 2, false, List.of("1", "7"), null),
                new MateriaDef("14", "Probabilidad y Estadística", 3, "ANUAL", 2, false, List.of("1", "2"), null),
                new MateriaDef("15", "Inglés I", 2, "ANUAL", 2, false, List.of("3"), null),
                new MateriaDef("16", "Resistencia de Materiales", 4, "ANUAL", 2, false, List.of("10"), List.of("1", "2", "7", "8")),
                new MateriaDef("17", "Tecnología del Hormigón", 2, "ANUAL", 2, false, List.of("12", "14", "15"), List.of("1", "2", "6", "7")),

                // NIVEL 3
                new MateriaDef("18", "Tecnología de la Construcción", 6, "ANUAL", 3, false, List.of("10", "11", "12", "15"), List.of("1", "2", "4", "5", "6", "7", "8")),
                new MateriaDef("19", "Geotopografía", 4, "ANUAL", 3, false, List.of("9", "11", "13", "14"), List.of("1", "2", "4", "5", "7")),
                new MateriaDef("20", "Hidráulica General y Aplicada", 5, "ANUAL", 3, false, List.of("9", "10", "11", "13", "14"), List.of("1", "2", "5", "7", "8")),
                new MateriaDef("21", "Cálculo Avanzado", 2, "ANUAL", 3, false, List.of("9", "10", "12", "14"), List.of("1", "2", "5", "7", "8")),
                new MateriaDef("22", "Instalaciones Eléctricas y Acústicas", 2, "ANUAL", 3, false, List.of("11", "12", "13"), List.of("1", "2", "4", "5", "6", "7")),
                new MateriaDef("23", "Instalaciones Termomecánicas", 2, "ANUAL", 3, false, List.of("11", "12", "13"), List.of("1", "2", "4", "5", "6", "7")),
                new MateriaDef("24", "Economía", 3, "ANUAL", 3, false, List.of("11", "14", "15"), List.of("1", "2", "3", "4", "8")),
                new MateriaDef("25", "Inglés II", 2, "ANUAL", 3, false, List.of("15"), List.of("3", "4")),

                // NIVEL 4
                new MateriaDef("26", "Geotecnia", 5, "ANUAL", 4, false, List.of("16", "17", "18", "19", "20"), List.of("9", "10", "11", "12", "13", "14")),
                new MateriaDef("27", "Instalaciones Sanitarias y de Gas", 3, "ANUAL", 4, false, List.of("18", "19", "20", "24"), List.of("5", "6", "7", "8", "12")),
                new MateriaDef("28", "Diseño Arq., Planeamiento y Urbanismo", 5, "ANUAL", 4, false, List.of("18", "19", "22", "23", "24", "25"), List.of("10", "11", "12", "15")),
                new MateriaDef("29", "Análisis Estructural I", 5, "ANUAL", 4, false, List.of("16", "17"), List.of("9", "10", "11", "14")),
                new MateriaDef("30", "Estructuras de Hormigón", 5, "ANUAL", 4, false, List.of("16", "17", "18", "19", "25"), List.of("9", "10", "11", "12", "13", "14")),
                new MateriaDef("31", "Hidrología y Obras Hidráulicas", 4, "ANUAL", 4, false, List.of("16", "18", "19", "20", "24", "25"), List.of("9", "10", "11", "12", "13", "14")),
                new MateriaDef("32", "Ingeniería Legal", 3, "ANUAL", 4, false, List.of("9", "11", "14", "15"), List.of("1", "2", "3", "4", "8")),
                new MateriaDef("33", "Construcciones Metálicas y de Maderas", 4, "ANUAL", 4, false, List.of("21", "29"), List.of("16", "17", "18", "19")),

                // NIVEL 5
                new MateriaDef("34", "Cimentaciones", 3, "ANUAL", 5, false, List.of("21", "26", "29", "30", "31"), List.of("16", "17", "18", "19", "20")),
                new MateriaDef("35", "Ingeniería Sanitaria", 3, "ANUAL", 5, false, List.of("26", "27", "31"), List.of("17", "18", "19", "20", "25")),
                new MateriaDef("36", "Organización y Conducción de Obras", 5, "ANUAL", 5, false, List.of("26", "27", "28", "30", "31"), List.of("17", "18", "19", "20", "22", "23", "24", "25")),
                new MateriaDef("37", "Vías de Comunicación I", 4, "ANUAL", 5, false, List.of("26", "28", "31", "32"), List.of("20", "24", "25")),
                new MateriaDef("38", "Análisis Estructural II", 5, "ANUAL", 5, false, List.of("21", "26", "29", "30", "31"), List.of("9", "10", "11", "12", "14", "15")),
                new MateriaDef("39", "Vías de Comunicación II", 8, "1 C", 5, false, List.of("26", "30", "31", "32", "37"), List.of("16", "17", "18", "19", "20", "24")),
                new MateriaDef("40", "Gestión Ambiental y Desarrollo Sust.", 6, "ANUAL", 5, false, List.of("26", "28", "31", "32"), List.of("16", "17", "18", "19", "25")),
                
                // NIVEL 6 (Proyecto final)
                new MateriaDef("51", "Proyecto Final", 8, "ANUAL", 5, false, List.of("26", "27", "28", "29", "30", "31", "32"), List.of("15", "16", "17", "18", "19", "20", "22", "23", "24", "25")),

                // ELECTIVAS IC
                new MateriaDef("52", "Formación de Emprendedores", 4, "1 y 2 C", 2, true, null, List.of("3")), // E1
                new MateriaDef("53", "Geología Aplicada", 2, "ANUAL", 2, true, List.of("4", "6", "7"), List.of("4", "6", "7")), // E2
                new MateriaDef("54", "Elasticidad y Plasticidad", 3, "ANUAL", 4, true, List.of("9", "16"), List.of("9", "16")), // E3 (Also had cursar_apr 1,2,10 but mapped simplified)
                new MateriaDef("55", "Uso del Recurso Hídrico", 3, "ANUAL", 5, true, List.of("31"), List.of("31")), // E4
                new MateriaDef("56", "Prefabricación", 2, "ANUAL", 5, true, List.of("17", "30"), List.of("17", "30")), // E5
                new MateriaDef("57", "Herramientas para el Desarrollo Profesional", 3, "2 C", 5, true, null, null), // E6
                new MateriaDef("58", "Proyecto y Gestión Urbana", 6, "1 C", 6, true, List.of("28"), List.of("28")), // E7
                new MateriaDef("59", "Vialidad Especial", 5, "1 C", 6, true, List.of("37"), List.of("37")), // E8
                new MateriaDef("60", "Obras Fluviales y Marítimas", 6, "1 C", 6, true, List.of("31"), List.of("26", "31")), // E9
                new MateriaDef("61", "Tránsito y Transporte", 5, "1 C", 6, true, List.of("37"), List.of("37")), // E10
                new MateriaDef("62", "Análisis Estructural III", 6, "1 C", 6, true, List.of("38"), List.of("38")), // E11
                new MateriaDef("63", "Gestión y Administración Ambiental", 4, "1 C", 6, true, List.of("35"), List.of("35")), // E12
                new MateriaDef("64", "Gen., Int. y Análisis de Info en Lab", 4, "2 C", 6, true, null, null)); // E13
    }

    // INGENIERÍA QUÍMICA (IQ)
    private List<MateriaDef> getDatasetQuimica() {
        return List.of(
                // Nivel 1
                new MateriaDef("1", "Introducción a la Ingeniería Química", 3, "ANUAL", 1, false, null, null),
                new MateriaDef("2", "Ingeniería y Sociedad", 2, "ANUAL", 1, false, null, null),
                new MateriaDef("3", "Álgebra y Geometría Analítica", 5, "ANUAL", 1, false, null, null),
                new MateriaDef("4", "Análisis Matemático I", 5, "ANUAL", 1, false, null, null),
                new MateriaDef("5", "Física I", 5, "ANUAL", 1, false, null, null),
                new MateriaDef("6", "Química", 5, "ANUAL", 1, false, null, null),
                new MateriaDef("7", "Sistemas de Representación", 2, "ANUAL", 1, false, null, null),
                new MateriaDef("8", "Fundamentos de Informática", 2, "2 C", 1, false, null, null),

                // Nivel 2
                new MateriaDef("9", "Introducción a Equipos y Procesos", 3, "ANUAL", 2, false, List.of("1", "6"), List.of("3", "4")),
                new MateriaDef("10", "Probabilidad y Estadística", 3, "ANUAL", 2, false, List.of("3", "4"), null),
                new MateriaDef("11", "Química Inorgánica", 4, "1 C", 2, false, List.of("6"), null),
                new MateriaDef("12", "Análisis Matemático II", 5, "ANUAL", 2, false, List.of("3", "4"), null),
                new MateriaDef("13", "Física II", 5, "ANUAL", 2, false, List.of("4", "5"), null),
                new MateriaDef("14", "Química Orgánica", 5, "ANUAL", 2, false, List.of("6"), null),
                new MateriaDef("15", "Legislación", 2, "2 C", 2, false, List.of("1", "2"), null),
                new MateriaDef("16", "Inglés I", 2, "ANUAL", 2, false, null, null),
                new MateriaDef("17", "Balances de Masa y Energía", 3, "ANUAL", 2, false, List.of("6", "7", "8", "9", "13"), List.of("1", "3", "4")),

                // Nivel 3
                new MateriaDef("18", "Termodinámica", 4, "ANUAL", 3, false, List.of("11", "12", "13"), List.of("4", "6")),
                new MateriaDef("19", "Matemática Superior Aplicada", 3, "ANUAL", 3, false, List.of("12"), List.of("3", "4")),
                new MateriaDef("20", "Ciencia de los Materiales", 2, "ANUAL", 3, false, List.of("9", "11", "14"), List.of("1", "6")),
                new MateriaDef("21", "Fisicoquímica", 4, "ANUAL", 3, false, List.of("9", "12", "13"), List.of("3", "4", "6")),
                new MateriaDef("22", "Fenómenos de Transporte", 5, "ANUAL", 3, false, List.of("9", "12", "13"), List.of("3", "4", "6")),
                new MateriaDef("23", "Química Analítica", 4, "ANUAL", 3, false, List.of("10", "11", "14"), List.of("2", "6")),
                new MateriaDef("24", "Microbiología y Química Biológica", 3, "ANUAL", 3, false, List.of("11", "14"), List.of("6")),
                new MateriaDef("25", "Química Aplicada", 2, "ANUAL", 3, false, List.of("9", "11", "13", "14"), List.of("1", "2", "6", "16")),
                new MateriaDef("26", "Inglés II", 2, "ANUAL", 3, false, List.of("16"), null),
                new MateriaDef("27", "Diseño, simulación, opt. y seg. de procesos", 4, "ANUAL", 3, false, List.of("17", "19"), List.of("7", "8", "9", "12", "26")),

                // Nivel 4
                new MateriaDef("28", "Operaciones Unitarias I", 5, "ANUAL", 4, false, List.of("17", "18", "22"), List.of("9", "12", "13")),
                new MateriaDef("29", "Tecnología de la Energía Térmica", 5, "ANUAL", 4, false, List.of("17", "18", "21", "22"), List.of("9", "12", "13")),
                new MateriaDef("30", "Economía", 3, "ANUAL", 4, false, List.of("9"), List.of("2", "3")),
                new MateriaDef("31", "Operaciones Unitarias II", 5, "ANUAL", 4, false, List.of("18", "21", "22"), List.of("9", "12", "13", "14")),
                new MateriaDef("32", "Ingeniería de las Reacciones Químicas", 5, "ANUAL", 4, false, List.of("17", "18", "21", "22"), List.of("11", "12", "14")),
                new MateriaDef("33", "Calidad y Control Estadístico", 3, "ANUAL", 4, false, List.of("10"), List.of("4")),
                new MateriaDef("34", "Organización Industrial", 3, "ANUAL", 4, false, List.of("10"), List.of("2", "9", "15")),
                new MateriaDef("35", "Control Automático de Procesos", 4, "ANUAL", 4, false, List.of("27", "31"), List.of("17", "19", "23")),

                // Nivel 5
                new MateriaDef("36", "Mecánica Industrial", 3, "ANUAL", 5, false, List.of("9", "21"), List.of("5", "11", "20")),
                new MateriaDef("37", "Ingeniería Ambiental", 3, "ANUAL", 5, false, List.of("25", "28", "31", "32"), List.of("15", "17", "23")),
                new MateriaDef("38", "Procesos Biotecnológicos", 3, "ANUAL", 5, false, List.of("17", "21", "22", "24"), List.of("9", "11", "14")),
                new MateriaDef("39", "Higiene y Seguridad en el Trabajo", 2, "ANUAL", 5, false, List.of("11", "14", "17"), List.of("9")),
                new MateriaDef("40", "Máquinas e Instalaciones Eléctricas", 2, "ANUAL", 5, false, List.of("28"), List.of("9", "13")),
                new MateriaDef("41", "Proyecto Final", 4, "ANUAL", 5, false, List.of("27", "28", "29", "31", "32", "34"), List.of("17", "21", "22", "25", "30")),

                // ELECTIVAS IQ
                new MateriaDef("42", "Introducción a las Tec. de Alimentos", 4, "ANUAL", 2, true, List.of("1", "6"), List.of("1", "6")), // E1
                new MateriaDef("43", "Control de Calidad de los Alimentos", 4, "2 C", 3, true, List.of("10", "11", "14", "15"), List.of("6", "10", "11", "14", "15")), // E2
                new MateriaDef("44", "Introducción a la Bromatología", 4, "2 C", 3, true, List.of("14", "15"), List.of("1", "6", "14", "15")), // E3
                new MateriaDef("45", "Química de los Alimentos", 5, "ANUAL", 4, true, List.of("21"), List.of("6", "11", "14", "21")), // E4
                new MateriaDef("46", "Calidad de los Alimentos", 4, "1 C", 4, true, null, List.of("10", "23")), // E5
                new MateriaDef("47", "Procesos y Equipos (Alimentos)", 4, "ANUAL", 5, true, List.of("28", "29", "31"), List.of("21", "22", "28", "29", "31")), // E6
                new MateriaDef("48", "Gestión Socioambiental Urbana", 4, "1 y 2 C", 2, true, List.of("1", "2"), List.of("1", "2")), // E7
                new MateriaDef("49", "Gestión del Medioambiente", 4, "2 C", 3, true, List.of("11", "13", "14"), List.of("5", "6", "11", "13", "14")), // E8
                new MateriaDef("50", "Ing. Amb. Aplicada a Medios Líquidos", 3, "ANUAL", 5, true, List.of("25", "28", "31", "32"), List.of("17", "23")), // E9
                new MateriaDef("51", "Ing. de Control Contaminación Aire", 3, "ANUAL", 5, true, List.of("25", "28", "31", "32"), List.of("17", "23")), // E10
                new MateriaDef("52", "Gestión de Tecnologías Sustentables", 4, "2 C", 5, true, List.of("28", "31", "32"), List.of("28", "31", "32")), // E11
                new MateriaDef("53", "Formación de Emprendedores", 4, "1 y 2 C", 2, true, null, List.of("2")), // E12
                new MateriaDef("54", "Electrónica Aplicada", 2, "ANUAL", 3, true, List.of("13"), List.of("5", "13")), // E13
                new MateriaDef("55", "Liderazgo en Ingeniería", 4, "ANUAL", 3, true, null, List.of("2", "15")), // E14
                new MateriaDef("56", "Informática Apl. a Ing. Procesos", 4, "ANUAL", 5, true, List.of("21", "27"), List.of("18", "21", "27")), // E15
                new MateriaDef("57", "Procesos Industriales 1", 3, "ANUAL", 5, true, List.of("27", "28", "29", "31", "32"), List.of("17", "18", "22", "27", "28", "29", "31", "32")), // E16
                new MateriaDef("58", "Procesos Industriales 2", 4, "2 C", 5, true, List.of("21", "27"), List.of("17", "18", "21", "27")), // E17
                new MateriaDef("59", "Aplicación de Prog. Matemática", 4, "ANUAL", 5, true, List.of("18", "28", "29", "31"), List.of("18", "28", "29", "31"))); // E18
    }

    // INGENIERÍA MECÁNICA (IM)

    private List<MateriaDef> getDatasetMecanica() {
        return List.of(
                // Nivel 1
                new MateriaDef("1", "Análisis Matemático I", 5, "ANUAL", 1, false, null, null),
                new MateriaDef("2", "Química General", 5, "ANUAL", 1, false, null, null),
                new MateriaDef("3", "Álgebra y Geometría Analítica", 5, "ANUAL", 1, false, null, null),
                new MateriaDef("4", "Física I", 5, "ANUAL", 1, false, null, null),
                new MateriaDef("5", "Ingeniería y Sociedad", 2, "ANUAL", 1, false, null, null),
                new MateriaDef("6", "Ingeniería Mecánica I", 2, "ANUAL", 1, false, null, null),
                new MateriaDef("7", "Sistemas de Representación", 3, "ANUAL", 1, false, null, null),
                new MateriaDef("8", "Fundamentos de Informática", 2, "ANUAL", 1, false, null, null),
                
                // Nivel 2
                new MateriaDef("9", "Materiales No Metálicos", 3, "ANUAL", 2, false, List.of("2", "4"), null),
                new MateriaDef("10", "Estabilidad I", 4, "ANUAL", 2, false, List.of("1", "3", "4"), null),
                new MateriaDef("11", "Materiales Metálicos", 5, "ANUAL", 2, false, List.of("2", "4"), null),
                new MateriaDef("12", "Análisis Matemático II", 5, "ANUAL", 2, false, List.of("1", "3"), null),
                new MateriaDef("13", "Física II", 5, "ANUAL", 2, false, List.of("1", "4"), null),
                new MateriaDef("14", "Ingeniería Ambiental y Seguridad Industrial", 3, "ANUAL", 2, false, List.of("2", "4"), null),
                new MateriaDef("15", "Ingeniería Mecánica II", 2, "ANUAL", 2, false, List.of("4", "6"), null),
                new MateriaDef("16", "Inglés I", 2, "ANUAL", 2, false, null, null),

                // Nivel 3
                new MateriaDef("17", "Termodinámica", 5, "ANUAL", 3, false, List.of("12", "13"), List.of("1", "3", "4")),
                new MateriaDef("18", "Mecánica Racional", 5, "ANUAL", 3, false, List.of("10", "12"), List.of("1", "3", "4")),
                new MateriaDef("19", "Estabilidad II", 4, "ANUAL", 3, false, List.of("10", "12"), List.of("1", "3", "4")),
                new MateriaDef("20", "Mediciones y Ensayos", 4, "ANUAL", 3, false, List.of("10", "11", "13"), List.of("1", "4")),
                new MateriaDef("21", "Diseño Mecánico", 3, "ANUAL", 3, false, List.of("9", "10", "11"), List.of("4", "6", "7", "8")),
                new MateriaDef("22", "Cálculo Avanzado", 3, "ANUAL", 3, false, List.of("12"), List.of("1", "3", "8")),
                new MateriaDef("23", "Ingeniería Mecánica III", 2, "ANUAL", 3, false, List.of("9", "11", "15"), List.of("1", "2", "4", "6")),
                new MateriaDef("24", "Probabilidad y Estadística", 3, "ANUAL", 3, false, List.of("1", "3"), null),
                new MateriaDef("25", "Inglés II", 2, "ANUAL", 3, false, List.of("16"), null),
                new MateriaDef("26", "Economía", 3, "ANUAL", 3, false, List.of("15"), List.of("5")),

                // Nivel 4
                new MateriaDef("27", "Elementos de Máquinas", 5, "ANUAL", 4, false, List.of("9", "11", "18", "19", "23"), List.of("2", "10", "12")),
                new MateriaDef("28", "Tecnología del Calor", 3, "ANUAL", 4, false, List.of("17"), List.of("12", "13")),
                new MateriaDef("29", "Metrología e Ingeniería de la Calidad", 4, "ANUAL", 4, false, List.of("20", "24"), List.of("3", "11", "13")),
                new MateriaDef("30", "Mecánica de los Fluidos", 4, "ANUAL", 4, false, List.of("17"), List.of("12", "13")),
                new MateriaDef("31", "Electrotecnia y Máquinas Eléctricas", 4, "ANUAL", 4, false, List.of("12", "13"), List.of("1", "3", "4")),
                new MateriaDef("32", "Electrónica y Sistemas de Control", 5, "ANUAL", 4, false, List.of("12", "13", "22"), List.of("1", "3", "4")),
                new MateriaDef("33", "Estabilidad III", 3, "ANUAL", 4, false, List.of("19"), List.of("1", "3", "4", "10")),
                new MateriaDef("34", "Tecnología de Fabricación", 5, "ANUAL", 4, false, List.of("27", "29"), List.of("9", "10", "11", "21")),

                // Nivel 5
                new MateriaDef("35", "Máquinas Alternativas y Turbomáquinas", 4, "ANUAL", 5, false, List.of("28"), List.of("13", "17")),
                new MateriaDef("36", "Instalaciones Industriales", 5, "ANUAL", 5, false, List.of("20", "28", "30", "31", "32"), List.of("10", "14", "17")),
                new MateriaDef("37", "Organización Industrial", 3, "ANUAL", 5, false, List.of("26"), List.of("15")),
                new MateriaDef("38", "Legislación", 2, "ANUAL", 5, false, List.of("15"), List.of("5")),
                new MateriaDef("39", "Mantenimiento", 2, "ANUAL", 5, false, List.of("20", "26", "27"), List.of("11", "13", "18", "19")),
                new MateriaDef("40", "Proyecto Final", 10, "ANUAL", 5, false, List.of("27", "29", "31", "32"), List.of("18", "19", "20", "21")),

                // ELECTIVAS IM
                new MateriaDef("41", "Metalografía y Tratamientos Térmicos", 4, "ANUAL", 5, true, List.of("11", "20"), List.of("11", "20")), // E1
                new MateriaDef("42", "Máquinas de Elevación y Transporte", 3, "ANUAL", 5, true, List.of("27", "31"), List.of("21", "27", "31")), // E2
                new MateriaDef("43", "Materiales de Ingeniería", 4, "ANUAL", 5, true, List.of("27", "34"), List.of("21", "23", "27", "34")), // E3
                new MateriaDef("44", "Sistemas de Control en Instalaciones Térmicas", 3, "ANUAL", 5, true, List.of("17", "30"), List.of("13", "17", "30")), // E4
                new MateriaDef("45", "Tecnología del Frío", 4, "ANUAL", 5, true, List.of("17", "30"), List.of("13")), // E5
                new MateriaDef("46", "Transmisión de Calor", 3, "ANUAL", 5, true, List.of("17"), List.of("12", "13")), // E6
                new MateriaDef("47", "Diseño de Instalaciones Térmicas", 2, "ANUAL", 5, true, List.of("17", "28", "30"), List.of("13", "17", "28", "30")), // E7
                new MateriaDef("48", "Maquinaria Agrícola", 4, "ANUAL", 5, true, List.of("27", "30", "31", "32"), List.of("20", "21", "23", "20", "21", "23")), // E8 (duplicates removed automatically or I should remove them. List.of allows dupes? No, Set logic in connection removes them.)
                new MateriaDef("49", "Formación de Emprendedores", 4, "1 y 2 C", 2, true, null, List.of("3"))); // E9
    }

    // INGENIERÍA EN ENERGÍA ELÉCTRICA (IEE)

    private List<MateriaDef> getDatasetElectrica() {
        return List.of(
                // Nivel 1
                new MateriaDef("1", "Análisis Matemático I", 5, "ANUAL", 1, false, null, null),
                new MateriaDef("2", "Álgebra y Geometría Analítica", 5, "ANUAL", 1, false, null, null),
                new MateriaDef("3", "Física I", 5, "ANUAL", 1, false, null, null),
                new MateriaDef("4", "Ingeniería y Sociedad", 2, "ANUAL", 1, false, null, null),
                new MateriaDef("5", "Integradora I", 2, "ANUAL", 1, false, null, null),
                new MateriaDef("6", "Química General", 3, "ANUAL", 1, false, null, null),
                new MateriaDef("7", "Sistemas de Representación", 3, "ANUAL", 1, false, null, null),
                new MateriaDef("8", "Fundamentos de Informática", 2, "ANUAL", 1, false, null, null),
                
                // Nivel 2
                new MateriaDef("9", "Análisis Matemático II", 5, "ANUAL", 2, false, List.of("1", "2"), null),
                new MateriaDef("10", "Física II", 5, "ANUAL", 2, false, List.of("1", "3"), null),
                new MateriaDef("11", "Integradora II", 2, "ANUAL", 2, false, List.of("3", "4", "5", "6", "7", "8"), List.of("1", "3", "4")),
                new MateriaDef("12", "Estabilidad (IEE)", 2, "ANUAL", 2, false, List.of("1", "2", "3"), null),
                new MateriaDef("13", "Ingeniería Ambiental y Seguridad Industrial", 3, "ANUAL", 2, false, List.of("2", "6"), null),
                new MateriaDef("14", "Electrotecnia I", 5, "ANUAL", 2, false, List.of("3", "4"), null),
                new MateriaDef("15", "Inglés I", 2, "ANUAL", 2, false, null, null),
                new MateriaDef("16", "Probabilidad y Estadística", 3, "ANUAL", 2, false, List.of("1", "2"), null),

                // Nivel 3
                new MateriaDef("17", "Matemática Aplicada", 3, "ANUAL", 3, false, List.of("9", "12"), List.of("9", "12")),
                new MateriaDef("18", "Termodinámica Técnica", 3, "ANUAL", 3, false, List.of("9", "10", "12"), List.of("1", "3", "4")),
                new MateriaDef("19", "Mecánica Técnica", 3, "ANUAL", 3, false, List.of("9", "10", "12"), List.of("1", "3", "4")),
                new MateriaDef("20", "Teoría del Campo", 3, "ANUAL", 3, false, List.of("9", "10", "14"), List.of("1", "3", "4")),
                new MateriaDef("21", "Electrotecnia II", 5, "ANUAL", 3, false, List.of("9", "10", "14"), List.of("1", "3", "4")),
                new MateriaDef("22", "Máquinas Eléctricas I", 5, "ANUAL", 3, false, List.of("9", "10", "14"), List.of("1", "3", "4")),
                new MateriaDef("23", "Electrónica I", 5, "ANUAL", 3, false, List.of("10", "14"), List.of("1", "3", "4")),
                new MateriaDef("24", "Inglés II", 2, "ANUAL", 3, false, List.of("15"), null),

                // Nivel 4
                new MateriaDef("25", "Tecnología de los Materiales", 2, "ANUAL", 4, false, List.of("22", "23"), List.of("6", "10", "12")),
                new MateriaDef("26", "Mediciones Eléctricas", 3, "ANUAL", 4, false, List.of("17", "21", "23"), List.of("9", "10", "14")),
                new MateriaDef("27", "Máquinas Eléctricas II", 5, "ANUAL", 4, false, List.of("17", "20", "22"), List.of("9", "10", "14")),
                new MateriaDef("28", "Generación, Transmisión y Distribución de la Energía Eléctrica", 6, "ANUAL", 4, false, List.of("17", "20", "21", "22"), List.of("9", "10", "14")),
                new MateriaDef("29", "Instrumentación y Control Automático", 4, "ANUAL", 4, false, List.of("17", "21", "22", "23"), List.of("9", "10", "14")),
                new MateriaDef("30", "Electrónica II", 4, "ANUAL", 4, false, List.of("17", "21", "23"), List.of("9", "10", "14")),
                new MateriaDef("31", "Economía de la Energía Eléctrica", 3, "ANUAL", 4, false, List.of("11"), List.of("4", "5", "8")),

                // Nivel 5
                new MateriaDef("32", "Centrales y Protecciones", 4, "ANUAL", 5, false, List.of("27", "28", "29"), List.of("9", "10", "14", "17", "20", "21", "22", "23")),
                new MateriaDef("33", "Marco Legal e Ingeniería Legal", 2, "ANUAL", 5, false, List.of("13", "31"), List.of("4", "5", "8", "31")),
                new MateriaDef("34", "Instalaciones Eléctricas", 4, "ANUAL", 5, false, List.of("26", "27", "28"), List.of("9", "10", "14", "17", "20", "21", "22")),
                new MateriaDef("35", "Electrónica de Potencia", 4, "ANUAL", 5, false, List.of("27", "29", "30"), List.of("9", "10", "14", "17", "21", "22", "23")),
                new MateriaDef("36", "Organización y Gestión Industrial", 3, "ANUAL", 5, false, List.of("31"), List.of("4", "5", "8")),
                new MateriaDef("37", "Proyecto Final", 5, "ANUAL", 5, false, List.of("26", "27", "28", "29", "30"), List.of("18", "19", "20", "26", "27", "28", "29", "30")),
                
                // ELECTIVAS IEE
                new MateriaDef("38", "Sistemas de Potencia", 6, "1 C", 5, true, List.of("28"), List.of("28")), // E1
                new MateriaDef("39", "Distribución de Energía Eléctrica", 6, "1 C", 5, true, List.of("28"), List.of("28")), // E2
                new MateriaDef("40", "Máquinas Térmicas e Hidráulicas", 4, "1 C", 5, true, List.of("18", "19"), List.of("18", "19")), // E3
                new MateriaDef("41", "Energías Alternativas", 3, "2 C", 5, true, List.of("28"), List.of("28")), // E4
                new MateriaDef("42", "Técnica de la Alta Tensión", 3, "ANUAL", 5, true, List.of("20", "28"), List.of("20", "28")), // E5
                new MateriaDef("43", "Accionamientos Eléctricos", 3, "2 C", 5, true, List.of("22", "30"), List.of("22", "30")), // E6
                new MateriaDef("44", "Formación de Emprendedores", 4, "1 y 2 C", 2, true, null, List.of("4"))); // E7
    }
}