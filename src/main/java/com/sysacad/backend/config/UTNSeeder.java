package com.sysacad.backend.config;

import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.enums.*;
import com.sysacad.backend.repository.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class UTNSeeder {

    private final FacultadRegionalRepository facultadRepository;
    private final CarreraRepository carreraRepository;
    private final MateriaRepository materiaRepository;
    private final PlanDeEstudioRepository planRepository;
    private final PlanMateriaRepository planMateriaRepository;

    // Cache para reutilizar materias y no duplicar (Clave: Nombre Materia -> Objeto Materia)
    private final Map<String, Materia> materiasPersistidas = new HashMap<>();
    
    // Cache temporal para resolver correlativas (Clave: Carrera+Codigo -> Objeto Materia)
    // Ejemplo: "ISI-9" -> Análisis Matemático II
    private final Map<String, Materia> mapaCodigoMateria = new HashMap<>();

    public UTNSeeder(FacultadRegionalRepository facultadRepository, CarreraRepository carreraRepository, MateriaRepository materiaRepository, PlanDeEstudioRepository planRepository, PlanMateriaRepository planMateriaRepository) {
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

        System.out.println(">> UTNSeeder: Iniciando carga masiva de planes y correlativas...");

        // 1. Facultad
        FacultadRegional frro = new FacultadRegional();
        frro.setCiudad("Rosario");
        frro.setProvincia("Santa Fe");
        frro = facultadRepository.save(frro);

        // 2. Cargar Datos en Memoria y Persistir Materias/Planes
        cargarCarrera(frro, "ISI", "Ingeniería en Sistemas de Información", getDatasetISI());
        cargarCarrera(frro, "IC", "Ingeniería Civil", getDatasetCivil());
        cargarCarrera(frro, "IM", "Ingeniería Mecánica", getDatasetMecanica());
        cargarCarrera(frro, "IEE", "Ingeniería en Energía Eléctrica", getDatasetElectrica());
        cargarCarrera(frro, "IQ", "Ingeniería Química", getDatasetQuimica());

        // 3. Segunda Pasada: Conectar Correlativas
        // Esto se hace al final para asegurar que todas las materias referenciadas ya existan
        conectarCorrelativas("ISI", getDatasetISI());
        conectarCorrelativas("IC", getDatasetCivil());
        conectarCorrelativas("IM", getDatasetMecanica());
        conectarCorrelativas("IEE", getDatasetElectrica());
        conectarCorrelativas("IQ", getDatasetQuimica());

        System.out.println(">> UTNSeeder: Carga finalizada con éxito.");
    }

    // --- LÓGICA DE CARGA ---

    private void cargarCarrera(FacultadRegional facu, String codigoCarrera, String nombreCarrera, List<MateriaDef> materias) {
        System.out.println("   -> Procesando Carrera: " + nombreCarrera);

        // Crear Carrera
        Carrera carrera = new Carrera();
        carrera.setId(new Carrera.CarreraId(facu.getId(), codigoCarrera));
        carrera.setNombre(nombreCarrera);
        carrera.setFacultad(facu);
        carrera = carreraRepository.save(carrera);

        // Crear Plan
        PlanDeEstudio plan = new PlanDeEstudio();
        plan.setId(new PlanDeEstudio.PlanId(facu.getId(), codigoCarrera, "Plan 2023"));
        plan.setFechaInicio(LocalDate.of(2023, 3, 1));
        plan.setEsVigente(true);
        plan = planRepository.save(plan);

        for (MateriaDef def : materias) {
            // 1. Buscar o Crear Materia (Reutilización por nombre)
            Materia materia = materiasPersistidas.computeIfAbsent(def.nombre, k -> {
                // Intenta buscar en BD por si acaso
                return materiaRepository.findByNombreContainingIgnoreCase(def.nombre).stream().findFirst().orElseGet(() -> {
                    Materia m = new Materia();
                    m.setNombre(def.nombre);
                    m.setTipoMateria(def.tipo);
                    m.setDuracion(def.duracion);
                    m.setHorasCursado(def.horas);
                    m.setOptativa(def.esElectiva);
                    m.setRendirLibre(true);
                    return materiaRepository.save(m);
                });
            });

            // 2. Guardar en mapa temporal para resolución de correlativas (Clave única por carrera: ISI-1, ISI-2)
            mapaCodigoMateria.put(codigoCarrera + "-" + def.codigo, materia);

            // 3. Crear vínculo Plan-Materia
            PlanMateria pm = new PlanMateria();
            pm.setId(new PlanMateria.PlanMateriaId(facu.getId(), codigoCarrera, "Plan 2023", materia.getId()));
            pm.setMateria(materia);
            pm.setPlan(plan);
            pm.setCodigoMateria(def.codigo); // "1", "2", "95-0001"
            pm.setNivel(def.nivel);
            planMateriaRepository.save(pm);
        }
    }

    private void conectarCorrelativas(String codigoCarrera, List<MateriaDef> materias) {
        System.out.println("   -> Conectando Correlativas para: " + codigoCarrera);
        
        for (MateriaDef def : materias) {
            if (def.correlativas == null || def.correlativas.isEmpty()) continue;

            Materia materiaActual = mapaCodigoMateria.get(codigoCarrera + "-" + def.codigo);
            if (materiaActual == null) continue;

            List<Materia> nuevasCorrelativas = new ArrayList<>();
            
            // Preservar correlativas existentes si la materia es compartida
            if (materiaActual.getCorrelativas() != null) {
                nuevasCorrelativas.addAll(materiaActual.getCorrelativas());
            }

            for (String codCorr : def.correlativas) {
                Materia matCorr = mapaCodigoMateria.get(codigoCarrera + "-" + codCorr);
                if (matCorr != null) {
                    if (!nuevasCorrelativas.contains(matCorr)) {
                        nuevasCorrelativas.add(matCorr);
                    }
                } else {
                    System.out.println("      [WARN] No se encontró correlativa código '" + codCorr + "' para " + def.nombre);
                }
            }

            materiaActual.setCorrelativas(nuevasCorrelativas);
            materiaRepository.save(materiaActual);
        }
    }

    // ==========================================
    // DATASETS (Definición de Datos)
    // ==========================================

    @Data
    @AllArgsConstructor
    private static class MateriaDef {
        String codigo;
        String nombre;
        Short nivel;
        Short horas;
        TipoMateria tipo;
        DuracionMateria duracion;
        boolean esElectiva;
        List<String> correlativas; // Lista de códigos (ej: "1", "2")

        // Constructor helper para obligatorias
        public MateriaDef(String codigo, String nombre, int nivel, int horas, List<String> correlativas) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.nivel = (short) nivel;
            this.horas = (short) horas;
            this.tipo = nivel <= 2 ? TipoMateria.BASICA : TipoMateria.ESPECIFICA;
            this.duracion = DuracionMateria.CUATRIMESTRAL; // Default simplificado
            this.esElectiva = false;
            this.correlativas = correlativas;
        }
        
        // Constructor helper para anuales explicitas
        public MateriaDef(String codigo, String nombre, int nivel, int horas, DuracionMateria duracion, List<String> correlativas) {
            this(codigo, nombre, nivel, horas, correlativas);
            this.duracion = duracion;
        }
    }

    private List<MateriaDef> getDatasetISI() {
        return List.of(
            // Nivel 1
            new MateriaDef("1", "Análisis Matemático I", 1, 5, List.of()),
            new MateriaDef("2", "Álgebra y Geometría Analítica", 1, 5, List.of()),
            new MateriaDef("3", "Física I", 1, 5, List.of()),
            new MateriaDef("4", "Inglés I", 1, 2, List.of()),
            new MateriaDef("5", "Lógica y Estructuras Discretas", 1, 3, List.of()),
            new MateriaDef("6", "Algoritmos y Estructuras de Datos", 1, 5, List.of()),
            new MateriaDef("7", "Arquitectura de Computadoras", 1, 4, List.of()),
            new MateriaDef("8", "Sistemas y Procesos de Negocio", 1, 3, List.of()),
            // Nivel 2
            new MateriaDef("9", "Análisis Matemático II", 2, 5, List.of("1", "2")),
            new MateriaDef("10", "Física II", 2, 5, List.of("1", "3")),
            new MateriaDef("11", "Ingeniería y Sociedad", 2, 2, List.of()),
            new MateriaDef("12", "Inglés II", 2, 2, List.of("4")),
            new MateriaDef("13", "Sintaxis y Semántica de los Lenguajes", 2, 4, List.of("5", "6")),
            new MateriaDef("14", "Paradigmas de Programación", 2, 4, List.of("5", "6")),
            new MateriaDef("15", "Sistemas Operativos", 2, 4, List.of("7")),
            new MateriaDef("16", "Análisis de Sistemas de Información", 2, 6, List.of("6", "8")),
            // Nivel 3
            new MateriaDef("17", "Probabilidad y Estadística", 3, 3, List.of("1", "2")),
            new MateriaDef("18", "Economía", 3, 3, List.of("1", "2")),
            new MateriaDef("19", "Bases de Datos", 3, 4, List.of("13", "16")),
            new MateriaDef("20", "Desarrollo de Software", 3, 4, List.of("14", "16")),
            new MateriaDef("21", "Comunicación de Datos", 3, 4, List.of("3", "7")),
            new MateriaDef("22", "Análisis Numérico", 3, 3, List.of("1", "2")),
            new MateriaDef("23", "Diseño de Sistemas de Información", 3, 6, List.of("9", "14", "16")),
            // Nivel 4
            new MateriaDef("24", "Legislación", 4, 2, List.of("11")),
            new MateriaDef("25", "Ingeniería y Calidad de Software", 4, 3, List.of("13", "14")),
            new MateriaDef("26", "Redes de Datos", 4, 4, List.of("15", "21")),
            new MateriaDef("27", "Investigación Operativa", 4, 4, List.of("17", "22")),
            new MateriaDef("28", "Simulación", 4, 3, List.of("17")),
            new MateriaDef("29", "Tecnologías para la Automatización", 4, 3, List.of("10", "22")),
            new MateriaDef("30", "Administración de Sistemas de Información", 4, 6, List.of("18", "23")),
            // Nivel 5
            new MateriaDef("31", "Inteligencia Artificial", 5, 3, List.of("28")),
            new MateriaDef("32", "Ciencia de Datos", 5, 3, List.of("28")),
            new MateriaDef("33", "Sistemas de Gestión", 5, 4, List.of("18", "27")),
            new MateriaDef("34", "Gestión Gerencial", 5, 3, List.of("24", "30")),
            new MateriaDef("35", "Seguridad en los Sistemas de Información", 5, 3, List.of("26", "30")),
            new MateriaDef("36", "Proyecto Final", 5, 6, DuracionMateria.ANUAL, List.of("25", "26", "30"))
        );
    }

    private List<MateriaDef> getDatasetCivil() {
        return List.of(
            new MateriaDef("1", "Análisis Matemático I", 1, 5, DuracionMateria.ANUAL, List.of()),
            new MateriaDef("2", "Álgebra y Geometría Analítica", 1, 5, DuracionMateria.ANUAL, List.of()),
            new MateriaDef("3", "Ingeniería y Sociedad", 1, 2, List.of()),
            new MateriaDef("4", "Ingeniería Civil I", 1, 3, List.of()),
            new MateriaDef("5", "Sistemas de Representación", 1, 3, DuracionMateria.ANUAL, List.of()),
            new MateriaDef("6", "Química General", 1, 5, DuracionMateria.ANUAL, List.of()),
            new MateriaDef("7", "Física I", 1, 5, DuracionMateria.ANUAL, List.of()),
            new MateriaDef("8", "Fundamentos de Informática", 1, 2, List.of()),
            
            new MateriaDef("9", "Análisis Matemático II", 2, 5, DuracionMateria.ANUAL, List.of("1", "2")),
            new MateriaDef("10", "Estabilidad", 2, 5, DuracionMateria.ANUAL, List.of("1", "2", "5", "7")),
            new MateriaDef("11", "Ingeniería Civil II", 2, 3, List.of("3", "4", "5")),
            new MateriaDef("12", "Tecnología de los Materiales", 2, 4, DuracionMateria.ANUAL, List.of("1", "5", "6", "7")),
            new MateriaDef("13", "Física II", 2, 5, DuracionMateria.ANUAL, List.of("1", "7")),
            new MateriaDef("14", "Probabilidad y Estadística", 2, 3, List.of("1", "2")),
            new MateriaDef("15", "Inglés I", 2, 2, List.of()),
            new MateriaDef("16", "Resistencia de Materiales", 2, 4, DuracionMateria.ANUAL, List.of("10")),
            new MateriaDef("17", "Tecnología del Hormigón", 2, 2, List.of("12", "14")),

            new MateriaDef("18", "Tecnología de la Construcción", 3, 6, DuracionMateria.ANUAL, List.of("10", "11", "12")),
            new MateriaDef("19", "Geotopografía", 3, 4, DuracionMateria.ANUAL, List.of("9", "11", "13", "14")),
            new MateriaDef("20", "Hidráulica General y Aplicada", 3, 5, DuracionMateria.ANUAL, List.of("9", "10", "11", "13")),
            new MateriaDef("21", "Cálculo Avanzado", 3, 2, List.of("9", "10", "12")),
            new MateriaDef("22", "Instalaciones Eléctricas y Acústicas", 3, 2, List.of("11", "12", "13")),
            new MateriaDef("23", "Instalaciones Termomecánicas", 3, 2, List.of("11", "12", "13")),
            new MateriaDef("24", "Economía", 3, 3, List.of("11", "14")),
            new MateriaDef("25", "Inglés II", 3, 2, List.of("15")),

            new MateriaDef("26", "Geotécnia", 4, 5, DuracionMateria.ANUAL, List.of("16", "19")),
            new MateriaDef("27", "Instalaciones Sanitarias y de Gas", 4, 3, List.of("18", "20")),
            new MateriaDef("28", "Diseño Arquitec., Planeamiento y Urbanismo", 4, 5, DuracionMateria.ANUAL, List.of("18", "19")),
            new MateriaDef("29", "Análisis Estructural I", 4, 5, DuracionMateria.ANUAL, List.of("16", "17")),
            new MateriaDef("30", "Estructuras de Hormigón", 4, 5, DuracionMateria.ANUAL, List.of("16", "17")),
            new MateriaDef("31", "Hidrología y Obras Hidráulicas", 4, 4, DuracionMateria.ANUAL, List.of("16", "20")),
            new MateriaDef("32", "Ingeniería Legal", 4, 3, List.of("9", "11")),
            new MateriaDef("33", "Construcciones Metálicas y de Maderas", 4, 4, DuracionMateria.ANUAL, List.of("21", "29")),

            new MateriaDef("34", "Cimentaciones", 5, 3, List.of("21", "26")),
            new MateriaDef("35", "Ingeniería Sanitaria", 5, 3, List.of("26", "27")),
            new MateriaDef("36", "Organización y Conducción de Obras", 5, 5, DuracionMateria.ANUAL, List.of("26", "27")),
            new MateriaDef("37", "Vías de Comunicación I", 5, 4, DuracionMateria.ANUAL, List.of("17", "18", "19")),
            new MateriaDef("38", "Análisis Estructural II", 5, 5, DuracionMateria.ANUAL, List.of("21", "29")),
            new MateriaDef("39", "Vías de Comunicación II", 5, 8, DuracionMateria.ANUAL, List.of("26", "37")),
            new MateriaDef("40", "Gestión Ambiental y Desarrollo Sustentable", 5, 6, DuracionMateria.ANUAL, List.of("26", "32")),
            
            new MateriaDef("51", "Proyecto Final", 6, 8, DuracionMateria.ANUAL, List.of("26", "27", "28", "29", "30", "31", "32"))
        );
    }

    private List<MateriaDef> getDatasetMecanica() {
        return List.of(
            new MateriaDef("1", "Análisis Matemático I", 1, 5, DuracionMateria.ANUAL, List.of()),
            new MateriaDef("2", "Química General", 1, 5, DuracionMateria.ANUAL, List.of()),
            new MateriaDef("3", "Álgebra y Geometría Analítica", 1, 5, DuracionMateria.ANUAL, List.of()),
            new MateriaDef("4", "Física I", 1, 5, DuracionMateria.ANUAL, List.of()),
            new MateriaDef("5", "Ingeniería y Sociedad", 1, 2, List.of()),
            new MateriaDef("6", "Ingeniería Mecánica I", 1, 2, List.of()),
            new MateriaDef("7", "Sistemas de Representación", 1, 3, DuracionMateria.ANUAL, List.of()),
            new MateriaDef("8", "Fundamentos de Informática", 1, 2, List.of()),

            new MateriaDef("9", "Materiales No Metálicos", 2, 3, List.of("2", "4")),
            new MateriaDef("10", "Estabilidad I", 2, 4, DuracionMateria.ANUAL, List.of("1", "3", "4")),
            new MateriaDef("11", "Materiales Metálicos", 2, 5, DuracionMateria.ANUAL, List.of("2", "4")),
            new MateriaDef("12", "Análisis Matemático II", 2, 5, DuracionMateria.ANUAL, List.of("1", "3")),
            new MateriaDef("13", "Física II", 2, 5, DuracionMateria.ANUAL, List.of("1", "4")),
            new MateriaDef("14", "Ing. Ambiental y Seguridad Industrial", 2, 3, List.of("2", "4")),
            new MateriaDef("15", "Ingeniería Mecánica II", 2, 2, List.of("4", "6")),
            new MateriaDef("16", "Inglés I", 2, 2, List.of()),
            new MateriaDef("17", "Termodinámica", 2, 5, DuracionMateria.ANUAL, List.of("12", "13")),

            new MateriaDef("18", "Mecánica Racional", 3, 5, DuracionMateria.ANUAL, List.of("10", "12")),
            new MateriaDef("19", "Estabilidad II", 3, 4, DuracionMateria.ANUAL, List.of("10", "12")),
            new MateriaDef("20", "Mediciones y Ensayos", 3, 4, DuracionMateria.ANUAL, List.of("10", "11", "13")),
            new MateriaDef("21", "Diseño Mecánico", 3, 3, List.of("9", "10", "11")),
            new MateriaDef("22", "Cálculo Avanzado", 3, 3, List.of("12")),
            new MateriaDef("23", "Ingeniería Mecánica III", 3, 2, List.of("9", "11", "15")),
            new MateriaDef("24", "Probabilidad y Estadística", 3, 3, List.of("1", "3")),
            new MateriaDef("25", "Inglés II", 3, 2, List.of("16")),
            new MateriaDef("26", "Economía", 3, 3, List.of("15")),

            new MateriaDef("27", "Elementos de Máquinas", 4, 5, DuracionMateria.ANUAL, List.of("18", "19")),
            new MateriaDef("28", "Tecnología del Calor", 4, 3, List.of("17")),
            new MateriaDef("29", "Metrología e Ingeniería de la Calidad", 4, 4, DuracionMateria.ANUAL, List.of("20", "24")),
            new MateriaDef("30", "Mecánica de los Fluidos", 4, 4, DuracionMateria.ANUAL, List.of("17")),
            new MateriaDef("31", "Electrotecnia y Máquinas Eléctricas", 4, 4, DuracionMateria.ANUAL, List.of("12", "13")),
            new MateriaDef("32", "Electrónica y Sistemas de Control", 4, 5, DuracionMateria.ANUAL, List.of("12", "13", "22")),
            new MateriaDef("33", "Estabilidad III", 4, 3, List.of("19")),
            new MateriaDef("34", "Tecnología de Fabricación", 4, 5, DuracionMateria.ANUAL, List.of("27", "29")),

            new MateriaDef("35", "Máquinas Alternativas y Turbomáquinas", 5, 4, DuracionMateria.ANUAL, List.of("28")),
            new MateriaDef("36", "Instalaciones Industriales", 5, 5, DuracionMateria.ANUAL, List.of("28", "30")),
            new MateriaDef("37", "Organización Industrial", 5, 3, List.of("26")),
            new MateriaDef("38", "Legislación", 5, 2, List.of("15")),
            new MateriaDef("39", "Mantenimiento", 5, 2, List.of("20", "26", "27")),
            new MateriaDef("40", "Proyecto Final", 5, 10, DuracionMateria.ANUAL, List.of("27", "29", "31", "32"))
        );
    }

    private List<MateriaDef> getDatasetElectrica() {
        return List.of(
            new MateriaDef("1", "Análisis Matemático I", 1, 5, DuracionMateria.ANUAL, List.of()),
            new MateriaDef("2", "Álgebra y Geometría Analítica", 1, 5, DuracionMateria.ANUAL, List.of()),
            new MateriaDef("3", "Ingeniería y Sociedad", 1, 2, List.of()),
            new MateriaDef("4", "Sistemas de Representación", 1, 3, DuracionMateria.ANUAL, List.of()),
            new MateriaDef("5", "Física I", 1, 5, DuracionMateria.ANUAL, List.of()),
            new MateriaDef("6", "Química General", 1, 5, DuracionMateria.ANUAL, List.of()),
            new MateriaDef("7", "Integración Eléctrica I", 1, 3, List.of()),
            new MateriaDef("8", "Fundamentos de Informática", 1, 2, List.of()),

            new MateriaDef("9", "Física II", 2, 5, DuracionMateria.ANUAL, List.of("1", "5")),
            new MateriaDef("10", "Probabilidad y Estadística", 2, 3, List.of("1", "2")),
            new MateriaDef("11", "Electrotecnia I", 2, 6, DuracionMateria.ANUAL, List.of("1", "2", "5")),
            new MateriaDef("12", "Estabilidad", 2, 4, List.of("1", "2", "5")),
            new MateriaDef("13", "Mecánica Técnica", 2, 2, List.of("1", "5")),
            new MateriaDef("14", "Integración Eléctrica II", 2, 3, List.of("1", "5", "7")),
            new MateriaDef("15", "Inglés I", 2, 2, List.of()),
            new MateriaDef("16", "Análisis Matemático II", 2, 5, DuracionMateria.ANUAL, List.of("1", "2")),
            new MateriaDef("17", "Cálculo Numérico", 2, 2, List.of("1", "2", "5", "8")),

            new MateriaDef("18", "Materia 18 (Desconocida)", 3, 3, List.of()), // Placeholder para integridad
            new MateriaDef("19", "Instrumentos y Mediciones Eléctricas", 3, 6, DuracionMateria.ANUAL, List.of("10", "11")),
            new MateriaDef("20", "Teoría de los Campos", 3, 3, List.of("9", "16")),
            new MateriaDef("21", "Física III", 3, 2, List.of("9", "16")),
            new MateriaDef("22", "Máquinas Eléctricas I", 3, 6, DuracionMateria.ANUAL, List.of("1", "5")),
            new MateriaDef("23", "Electrotecnia II", 3, 4, DuracionMateria.ANUAL, List.of("9", "11", "16")),
            new MateriaDef("24", "Termodinámica", 3, 3, List.of("9", "16")),
            new MateriaDef("25", "Fundamentos para el Análisis de Señales", 3, 3, List.of("16", "17")),
            new MateriaDef("26", "Taller interdisciplinario", 3, 2, List.of()),
            new MateriaDef("27", "Inglés II", 3, 2, List.of("15")),
            new MateriaDef("28", "Economía", 3, 3, List.of("3")),

            new MateriaDef("29", "Electrónica I", 4, 4, List.of("1", "5")),
            new MateriaDef("30", "Máquinas Eléctricas II", 4, 6, DuracionMateria.ANUAL, List.of("19", "22", "23")),
            new MateriaDef("31", "Seguridad, Riesgo Eléctrico y Medio Ambiente", 4, 2, List.of("6", "11")),
            new MateriaDef("32", "Instalaciones Eléctricas y Luminotecnia", 4, 6, DuracionMateria.ANUAL, List.of("6", "11", "14")),
            new MateriaDef("33", "Control Automático", 4, 5, DuracionMateria.ANUAL, List.of("11", "16")),
            new MateriaDef("34", "Máquinas Térmicas, Hidráulicas y de Fluido", 4, 3, List.of("12", "13", "24")),
            new MateriaDef("35", "Legislación", 4, 2, List.of("3")),

            new MateriaDef("36", "Electrónica II", 5, 3, List.of("11", "26")),
            new MateriaDef("37", "Generación, Transmisión y Distr. de Energía", 5, 6, DuracionMateria.ANUAL, List.of("12", "22", "23")),
            new MateriaDef("38", "Sistemas de Potencia", 5, 4, DuracionMateria.ANUAL, List.of("30", "33")),
            new MateriaDef("39", "Accionamientos y Controles Eléctricos", 5, 4, DuracionMateria.ANUAL, List.of("11", "22")),
            new MateriaDef("40", "Organización y Administración de Empresas", 5, 2, List.of("28", "35")),
            new MateriaDef("41", "Proyecto Final", 5, 2, DuracionMateria.ANUAL, List.of("22", "23", "25", "26", "27"))
        );
    }

    private List<MateriaDef> getDatasetQuimica() {
        return List.of(
            new MateriaDef("1", "Introducción a la Ingeniería Química", 1, 3, List.of()),
            new MateriaDef("2", "Ingeniería y Sociedad", 1, 2, List.of()),
            new MateriaDef("3", "Álgebra y Geometría Analítica", 1, 5, DuracionMateria.ANUAL, List.of()),
            new MateriaDef("4", "Análisis Matemático I", 1, 5, DuracionMateria.ANUAL, List.of()),
            new MateriaDef("5", "Física I", 1, 5, DuracionMateria.ANUAL, List.of()),
            new MateriaDef("6", "Química General", 1, 5, DuracionMateria.ANUAL, List.of()),
            new MateriaDef("7", "Sistemas de Representación", 1, 2, List.of()),
            new MateriaDef("8", "Fundamentos de Informática", 1, 2, List.of()),

            new MateriaDef("9", "Introducción a Equipos y Procesos", 2, 3, List.of("1", "6")),
            new MateriaDef("10", "Probabilidad y Estadística", 2, 3, List.of("3", "4")),
            new MateriaDef("11", "Química Inorgánica", 2, 4, List.of("6")),
            new MateriaDef("12", "Análisis Matemático II", 2, 5, DuracionMateria.ANUAL, List.of("3", "4")),
            new MateriaDef("13", "Física II", 2, 5, DuracionMateria.ANUAL, List.of("4", "5")),
            new MateriaDef("14", "Química Orgánica", 2, 5, DuracionMateria.ANUAL, List.of("6")),
            new MateriaDef("15", "Legislación", 2, 2, List.of("1", "2")),
            new MateriaDef("16", "Inglés I", 2, 2, List.of()),
            new MateriaDef("17", "Balances de Masa y Energía", 2, 3, List.of("6", "9")),

            new MateriaDef("18", "Termodinámica", 3, 4, DuracionMateria.ANUAL, List.of("11", "12", "13")),
            new MateriaDef("19", "Matemática Superior Aplicada", 3, 3, List.of("12")),
            new MateriaDef("20", "Ciencia de los Materiales", 3, 2, List.of("9", "11", "14")),
            new MateriaDef("21", "Fisicoquímica", 3, 4, DuracionMateria.ANUAL, List.of("9", "12", "13")),
            new MateriaDef("22", "Fenómenos de Transporte", 3, 5, DuracionMateria.ANUAL, List.of("9", "12", "13")),
            new MateriaDef("23", "Química Analítica", 3, 4, DuracionMateria.ANUAL, List.of("10", "11", "14")),
            new MateriaDef("24", "Microbiología y Química Biológica", 3, 3, List.of("11", "14")),
            new MateriaDef("25", "Química Aplicada", 3, 2, List.of("9", "11", "13", "14")),
            new MateriaDef("26", "Inglés II", 3, 2, List.of("16")),
            new MateriaDef("27", "Diseño, Sim. y Opt. de Procesos", 3, 4, DuracionMateria.ANUAL, List.of("17", "19")),

            new MateriaDef("28", "Operaciones Unitarias I", 4, 5, DuracionMateria.ANUAL, List.of("17", "18", "22")),
            new MateriaDef("29", "Tecnología de la Energía Térmica", 4, 5, DuracionMateria.ANUAL, List.of("17", "18", "21", "22")),
            new MateriaDef("30", "Economía", 4, 3, List.of("9")),
            new MateriaDef("31", "Operaciones Unitarias II", 4, 5, DuracionMateria.ANUAL, List.of("18", "21", "22")),
            new MateriaDef("32", "Ingeniería de las Reacciones Químicas", 4, 5, DuracionMateria.ANUAL, List.of("17", "18", "21", "22")),
            new MateriaDef("33", "Calidad y Control Estadístico de Procesos", 4, 3, List.of("10")),
            new MateriaDef("34", "Organización Industrial", 4, 3, List.of("10")),
            new MateriaDef("35", "Control Automático de Procesos", 4, 4, DuracionMateria.ANUAL, List.of("27", "31")),

            new MateriaDef("36", "Mecánica Industrial", 5, 3, List.of("9", "21")),
            new MateriaDef("37", "Ingeniería Ambiental", 5, 3, List.of("25", "28", "31", "32")),
            new MateriaDef("38", "Procesos Biotecnológicos", 5, 3, List.of("17", "21", "22", "24")),
            new MateriaDef("39", "Higiene y Seguridad en el Trabajo", 5, 2, List.of("11", "14", "17")),
            new MateriaDef("40", "Máquinas e Instalaciones Eléctricas", 5, 2, List.of("28")),
            new MateriaDef("41", "Proyecto Final", 5, 4, DuracionMateria.ANUAL, List.of("27", "28", "29", "31", "32"))
        );
    }
}