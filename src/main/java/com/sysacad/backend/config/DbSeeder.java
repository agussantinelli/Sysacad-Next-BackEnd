package com.sysacad.backend.config;

import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.enums.*;
import com.sysacad.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DbSeeder {

    // Cache local para no re-consultar materias comunes (como Análisis Matemático I)
    private final Map<String, Materia> materiasCache = new HashMap<>();

    @Bean
    @Transactional
    CommandLineRunner initDatabase(
            FacultadRegionalRepository facultadRepository,
            CarreraRepository carreraRepository,
            MateriaRepository materiaRepository,
            PlanDeEstudioRepository planRepository,
            PlanMateriaRepository planMateriaRepository,
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {
            if (facultadRepository.count() > 0) {
                System.out.println(">> La base de datos ya tiene datos. Omitiendo seed.");
                return;
            }

            System.out.println(">> 🚀 Iniciando Carga Masiva de Planes de Estudio UTN FRRO 2023...");

            // 1. Crear Facultad
            FacultadRegional frro = new FacultadRegional();
            frro.setCiudad("Rosario");
            frro.setProvincia("Santa Fe");
            frro = facultadRepository.save(frro);

            // 2. Cargar Carreras y Planes
            cargarISI(frro, carreraRepository, materiaRepository, planRepository, planMateriaRepository);
            cargarCivil(frro, carreraRepository, materiaRepository, planRepository, planMateriaRepository);
            cargarMecanica(frro, carreraRepository, materiaRepository, planRepository, planMateriaRepository);
            cargarElectrica(frro, carreraRepository, materiaRepository, planRepository, planMateriaRepository);
            cargarQuimica(frro, carreraRepository, materiaRepository, planRepository, planMateriaRepository);

            // 3. Cargar Usuarios
            cargarUsuarios(usuarioRepository, passwordEncoder);

            System.out.println(">> ✅ Seeding Completado con Éxito.");
        };
    }

    // INGENIERÍA EN SISTEMAS DE INFORMACIÓN

    private void cargarISI(FacultadRegional facu, CarreraRepository carRepo, MateriaRepository matRepo, PlanDeEstudioRepository planRepo, PlanMateriaRepository pmRepo) {
        System.out.println("... Cargando Ingeniería en Sistemas");
        Carrera carrera = crearCarrera(facu, "ISI", "Ingeniería en Sistemas de Información", carRepo);
        PlanDeEstudio plan = crearPlan(carrera, "Plan 2023", planRepo);

        // Nivel 1
        asociar(plan, "Análisis Matemático I", TipoMateria.BASICA, 5, 1, matRepo, pmRepo);
        asociar(plan, "Álgebra y Geometría Analítica", TipoMateria.BASICA, 5, 1, matRepo, pmRepo);
        asociar(plan, "Física I", TipoMateria.BASICA, 5, 1, matRepo, pmRepo);
        asociar(plan, "Inglés I", TipoMateria.BASICA, 2, 1, matRepo, pmRepo);
        asociar(plan, "Lógica y Estructuras Discretas", TipoMateria.ESPECIFICA, 3, 1, matRepo, pmRepo);
        asociar(plan, "Algoritmos y Estructuras de Datos", TipoMateria.ESPECIFICA, 5, 1, matRepo, pmRepo);
        asociar(plan, "Arquitectura de Computadoras", TipoMateria.ESPECIFICA, 4, 1, matRepo, pmRepo);
        asociar(plan, "Sistemas y Procesos de Negocio", TipoMateria.ESPECIFICA, 3, 1, matRepo, pmRepo);

        // Nivel 2
        asociar(plan, "Análisis Matemático II", TipoMateria.BASICA, 5, 2, matRepo, pmRepo);
        asociar(plan, "Física II", TipoMateria.BASICA, 5, 2, matRepo, pmRepo);
        asociar(plan, "Ingeniería y Sociedad", TipoMateria.BASICA, 2, 2, matRepo, pmRepo);
        asociar(plan, "Inglés II", TipoMateria.BASICA, 2, 2, matRepo, pmRepo);
        asociar(plan, "Sintaxis y Semántica de los Lenguajes", TipoMateria.ESPECIFICA, 4, 2, matRepo, pmRepo);
        asociar(plan, "Paradigmas de Programación", TipoMateria.ESPECIFICA, 4, 2, matRepo, pmRepo);
        asociar(plan, "Sistemas Operativos", TipoMateria.ESPECIFICA, 4, 2, matRepo, pmRepo);
        asociar(plan, "Análisis de Sistemas", TipoMateria.ESPECIFICA, 6, 2, matRepo, pmRepo);

        // Nivel 3
        asociar(plan, "Probabilidad y Estadística", TipoMateria.BASICA, 3, 3, matRepo, pmRepo);
        asociar(plan, "Economía", TipoMateria.BASICA, 3, 3, matRepo, pmRepo);
        asociar(plan, "Bases de Datos", TipoMateria.ESPECIFICA, 4, 3, matRepo, pmRepo);
        asociar(plan, "Desarrollo de Software", TipoMateria.ESPECIFICA, 4, 3, matRepo, pmRepo);
        asociar(plan, "Comunicación de Datos", TipoMateria.ESPECIFICA, 4, 3, matRepo, pmRepo);
        asociar(plan, "Análisis Numérico", TipoMateria.BASICA, 3, 3, matRepo, pmRepo);
        asociar(plan, "Diseño de Sistemas", TipoMateria.ESPECIFICA, 6, 3, matRepo, pmRepo);

        // Nivel 4
        asociar(plan, "Legislación", TipoMateria.BASICA, 2, 4, matRepo, pmRepo);
        asociar(plan, "Ingeniería y Calidad de Software", TipoMateria.ESPECIFICA, 3, 4, matRepo, pmRepo);
        asociar(plan, "Redes de Datos", TipoMateria.ESPECIFICA, 4, 4, matRepo, pmRepo);
        asociar(plan, "Investigación Operativa", TipoMateria.BASICA, 4, 4, matRepo, pmRepo);
        asociar(plan, "Simulación", TipoMateria.ESPECIFICA, 3, 4, matRepo, pmRepo);
        asociar(plan, "Tecnologías para la Automatización", TipoMateria.ESPECIFICA, 3, 4, matRepo, pmRepo);
        asociar(plan, "Administración de Sistemas", TipoMateria.ESPECIFICA, 6, 4, matRepo, pmRepo);

        // Nivel 5
        asociar(plan, "Inteligencia Artificial", TipoMateria.ESPECIFICA, 3, 5, matRepo, pmRepo);
        asociar(plan, "Ciencia de Datos", TipoMateria.ESPECIFICA, 3, 5, matRepo, pmRepo);
        asociar(plan, "Sistemas de Gestión", TipoMateria.ESPECIFICA, 4, 5, matRepo, pmRepo);
        asociar(plan, "Gestión Gerencial", TipoMateria.ESPECIFICA, 3, 5, matRepo, pmRepo);
        asociar(plan, "Seguridad en los Sistemas", TipoMateria.ESPECIFICA, 3, 5, matRepo, pmRepo);
        asociar(plan, "Proyecto Final", TipoMateria.ESPECIFICA, 6, 5, matRepo, pmRepo);

        // Electivas ISI (Lista Completa según documento)
        // Nivel 2
        cargarElectivas(plan, matRepo, pmRepo, 2,
                "Entornos Gráficos",
                "Análisis y Diseño de Datos e Información",
                "Sistemas de Información Geográfica",
                "Formación de Emprendedores"
        );

        // Nivel 3
        cargarElectivas(plan, matRepo, pmRepo, 3,
                "Algoritmos Genéticos",
                "Información Jurídica",
                "Lenguaje de Programación JAVA",
                "Tecnologías de Desarrollo de Software IDE",
                "Gestión Ingenieril",
                "Introducción a la Práctica Profesional",
                "Química Aplicada a la Informática"
        );

        // Nivel 4
        cargarElectivas(plan, matRepo, pmRepo, 4,
                "Infraestructura Tecnológica",
                "Soporte a Bases de Datos con Prog. Visual",
                "Metodología de la Investigación",
                "Metodologías Ágiles en Des. de Software"
        );

        // Nivel 5
        cargarElectivas(plan, matRepo, pmRepo, 5,
                "Fabricación Aditiva",
                "Dirección de Recursos Humanos",
                "Informática en la Administración Pública",
                "Sist. de Inf. Integrados para la Industria",
                "Minería de Datos"
        );
    }

    // INGENIERÍA CIVIL

    private void cargarCivil(FacultadRegional facu, CarreraRepository carRepo, MateriaRepository matRepo, PlanDeEstudioRepository planRepo, PlanMateriaRepository pmRepo) {
        System.out.println("... Cargando Ingeniería Civil");
        Carrera carrera = crearCarrera(facu, "IC", "Ingeniería Civil", carRepo);
        PlanDeEstudio plan = crearPlan(carrera, "Plan 2023", planRepo);

        // Nivel 1
        asociar(plan, "Análisis Matemático I", TipoMateria.BASICA, 5, 1, matRepo, pmRepo);
        asociar(plan, "Álgebra y Geometría Analítica", TipoMateria.BASICA, 5, 1, matRepo, pmRepo);
        asociar(plan, "Ingeniería y Sociedad", TipoMateria.BASICA, 2, 1, matRepo, pmRepo);
        asociar(plan, "Ingeniería Civil I", TipoMateria.ESPECIFICA, 3, 1, matRepo, pmRepo);
        asociar(plan, "Sistemas de Representación", TipoMateria.BASICA, 3, 1, matRepo, pmRepo);
        asociar(plan, "Química General", TipoMateria.BASICA, 5, 1, matRepo, pmRepo);
        asociar(plan, "Física I", TipoMateria.BASICA, 5, 1, matRepo, pmRepo);
        asociar(plan, "Fundamentos de Informática", TipoMateria.BASICA, 2, 1, matRepo, pmRepo);

        // Nivel 2
        asociar(plan, "Análisis Matemático II", TipoMateria.BASICA, 5, 2, matRepo, pmRepo);
        asociar(plan, "Estabilidad", TipoMateria.ESPECIFICA, 5, 2, matRepo, pmRepo);
        asociar(plan, "Ingeniería Civil II", TipoMateria.ESPECIFICA, 3, 2, matRepo, pmRepo);
        asociar(plan, "Tecnología de los Materiales", TipoMateria.ESPECIFICA, 4, 2, matRepo, pmRepo);
        asociar(plan, "Física II", TipoMateria.BASICA, 5, 2, matRepo, pmRepo);
        asociar(plan, "Probabilidad y Estadística", TipoMateria.BASICA, 3, 2, matRepo, pmRepo);
        asociar(plan, "Inglés I", TipoMateria.BASICA, 2, 2, matRepo, pmRepo);
        asociar(plan, "Resistencia de Materiales", TipoMateria.ESPECIFICA, 4, 2, matRepo, pmRepo);
        asociar(plan, "Tecnología del Hormigón", TipoMateria.ESPECIFICA, 2, 2, matRepo, pmRepo);

        // Nivel 3
        asociar(plan, "Tecnología de la Construcción", TipoMateria.ESPECIFICA, 6, 3, matRepo, pmRepo);
        asociar(plan, "Geotopografía", TipoMateria.ESPECIFICA, 4, 3, matRepo, pmRepo);
        asociar(plan, "Hidráulica General y Aplicada", TipoMateria.ESPECIFICA, 5, 3, matRepo, pmRepo);
        asociar(plan, "Cálculo Avanzado", TipoMateria.BASICA, 2, 3, matRepo, pmRepo);
        asociar(plan, "Instalaciones Eléctricas y Acústicas", TipoMateria.ESPECIFICA, 2, 3, matRepo, pmRepo);
        asociar(plan, "Instalaciones Termomecánicas", TipoMateria.ESPECIFICA, 2, 3, matRepo, pmRepo);
        asociar(plan, "Economía", TipoMateria.BASICA, 3, 3, matRepo, pmRepo);
        asociar(plan, "Inglés II", TipoMateria.BASICA, 2, 3, matRepo, pmRepo);

        // Nivel 4
        asociar(plan, "Geotecnia", TipoMateria.ESPECIFICA, 5, 4, matRepo, pmRepo);
        asociar(plan, "Instalaciones Sanitarias y de Gas", TipoMateria.ESPECIFICA, 3, 4, matRepo, pmRepo);
        asociar(plan, "Diseño Arquitectónico y Planeamiento", TipoMateria.ESPECIFICA, 5, 4, matRepo, pmRepo);
        asociar(plan, "Análisis Estructural I", TipoMateria.ESPECIFICA, 5, 4, matRepo, pmRepo);
        asociar(plan, "Estructuras de Hormigón", TipoMateria.ESPECIFICA, 5, 4, matRepo, pmRepo);
        asociar(plan, "Hidrología y Obras Hidráulicas", TipoMateria.ESPECIFICA, 4, 4, matRepo, pmRepo);
        asociar(plan, "Ingeniería Legal", TipoMateria.BASICA, 3, 4, matRepo, pmRepo);
        asociar(plan, "Construcciones Metálicas y de Madera", TipoMateria.ESPECIFICA, 4, 4, matRepo, pmRepo);

        // Nivel 5
        asociar(plan, "Cimentaciones", TipoMateria.ESPECIFICA, 3, 5, matRepo, pmRepo);
        asociar(plan, "Ingeniería Sanitaria", TipoMateria.ESPECIFICA, 3, 5, matRepo, pmRepo);
        asociar(plan, "Organización y Conducción de Obras", TipoMateria.ESPECIFICA, 5, 5, matRepo, pmRepo);
        asociar(plan, "Gestión Ambiental", TipoMateria.ESPECIFICA, 6, 5, matRepo, pmRepo);
        asociar(plan, "Vías de Comunicación I", TipoMateria.ESPECIFICA, 4, 5, matRepo, pmRepo);
        asociar(plan, "Análisis Estructural II", TipoMateria.ESPECIFICA, 5, 5, matRepo, pmRepo);
        asociar(plan, "Vías de Comunicación II", TipoMateria.ESPECIFICA, 8, 5, matRepo, pmRepo);

        // Nivel 6
        asociar(plan, "Proyecto Final Civil", TipoMateria.ESPECIFICA, 8, 6, matRepo, pmRepo);
    }

    // INGENIERÍA MECÁNICA

    private void cargarMecanica(FacultadRegional facu, CarreraRepository carRepo, MateriaRepository matRepo, PlanDeEstudioRepository planRepo, PlanMateriaRepository pmRepo) {
        System.out.println("... Cargando Ingeniería Mecánica");
        Carrera carrera = crearCarrera(facu, "IM", "Ingeniería Mecánica", carRepo);
        PlanDeEstudio plan = crearPlan(carrera, "Plan 2023", planRepo);

        // Nivel 1
        asociar(plan, "Análisis Matemático I", TipoMateria.BASICA, 5, 1, matRepo, pmRepo);
        asociar(plan, "Química General", TipoMateria.BASICA, 5, 1, matRepo, pmRepo);
        asociar(plan, "Álgebra y Geometría Analítica", TipoMateria.BASICA, 5, 1, matRepo, pmRepo);
        asociar(plan, "Física I", TipoMateria.BASICA, 5, 1, matRepo, pmRepo);
        asociar(plan, "Ingeniería y Sociedad", TipoMateria.BASICA, 2, 1, matRepo, pmRepo);
        asociar(plan, "Ingeniería Mecánica I", TipoMateria.ESPECIFICA, 2, 1, matRepo, pmRepo);
        asociar(plan, "Sistemas de Representación", TipoMateria.BASICA, 3, 1, matRepo, pmRepo);
        asociar(plan, "Fundamentos de Informática", TipoMateria.BASICA, 2, 1, matRepo, pmRepo);

        // Nivel 2
        asociar(plan, "Materiales No Metálicos", TipoMateria.ESPECIFICA, 3, 2, matRepo, pmRepo);
        asociar(plan, "Estabilidad I", TipoMateria.ESPECIFICA, 4, 2, matRepo, pmRepo);
        asociar(plan, "Materiales Metálicos", TipoMateria.ESPECIFICA, 5, 2, matRepo, pmRepo);
        asociar(plan, "Análisis Matemático II", TipoMateria.BASICA, 5, 2, matRepo, pmRepo);
        asociar(plan, "Física II", TipoMateria.BASICA, 5, 2, matRepo, pmRepo);
        asociar(plan, "Seguridad Industrial y Ambiental", TipoMateria.ESPECIFICA, 3, 2, matRepo, pmRepo);
        asociar(plan, "Ingeniería Mecánica II", TipoMateria.ESPECIFICA, 2, 2, matRepo, pmRepo);
        asociar(plan, "Inglés I", TipoMateria.BASICA, 2, 2, matRepo, pmRepo);
        asociar(plan, "Termodinámica", TipoMateria.ESPECIFICA, 5, 2, matRepo, pmRepo);

        // Nivel 3
        asociar(plan, "Mecánica Racional", TipoMateria.ESPECIFICA, 5, 3, matRepo, pmRepo);
        asociar(plan, "Estabilidad II", TipoMateria.ESPECIFICA, 4, 3, matRepo, pmRepo);
        asociar(plan, "Mediciones y Ensayos", TipoMateria.ESPECIFICA, 4, 3, matRepo, pmRepo);
        asociar(plan, "Diseño Mecánico", TipoMateria.ESPECIFICA, 3, 3, matRepo, pmRepo);
        asociar(plan, "Cálculo Avanzado", TipoMateria.BASICA, 3, 3, matRepo, pmRepo);
        asociar(plan, "Ingeniería Mecánica III", TipoMateria.ESPECIFICA, 2, 3, matRepo, pmRepo);
        asociar(plan, "Probabilidad y Estadística", TipoMateria.BASICA, 3, 3, matRepo, pmRepo);
        asociar(plan, "Inglés II", TipoMateria.BASICA, 2, 3, matRepo, pmRepo);
        asociar(plan, "Economía", TipoMateria.BASICA, 3, 3, matRepo, pmRepo);

        // Nivel 4
        asociar(plan, "Elementos de Máquinas", TipoMateria.ESPECIFICA, 5, 4, matRepo, pmRepo);
        asociar(plan, "Tecnología del Calor", TipoMateria.ESPECIFICA, 3, 4, matRepo, pmRepo);
        asociar(plan, "Metrología y Calidad", TipoMateria.ESPECIFICA, 4, 4, matRepo, pmRepo);
        asociar(plan, "Mecánica de los Fluidos", TipoMateria.ESPECIFICA, 4, 4, matRepo, pmRepo);
        asociar(plan, "Electrotecnia y Máquinas Eléctricas", TipoMateria.ESPECIFICA, 4, 4, matRepo, pmRepo);
        asociar(plan, "Electrónica y Control", TipoMateria.ESPECIFICA, 5, 4, matRepo, pmRepo);
        asociar(plan, "Estabilidad III", TipoMateria.ESPECIFICA, 3, 4, matRepo, pmRepo);
        asociar(plan, "Tecnología de Fabricación", TipoMateria.ESPECIFICA, 5, 4, matRepo, pmRepo);

        // Nivel 5
        asociar(plan, "Máquinas Alternativas y Turbomáquinas", TipoMateria.ESPECIFICA, 4, 5, matRepo, pmRepo);
        asociar(plan, "Instalaciones Industriales", TipoMateria.ESPECIFICA, 5, 5, matRepo, pmRepo);
        asociar(plan, "Organización Industrial", TipoMateria.BASICA, 3, 5, matRepo, pmRepo);
        asociar(plan, "Legislación", TipoMateria.BASICA, 2, 5, matRepo, pmRepo);
        asociar(plan, "Mantenimiento", TipoMateria.ESPECIFICA, 2, 5, matRepo, pmRepo);
        asociar(plan, "Proyecto Final Mecánica", TipoMateria.ESPECIFICA, 10, 5, matRepo, pmRepo);
    }

    // INGENIERÍA EN ENERGÍA ELÉCTRICA

    private void cargarElectrica(FacultadRegional facu, CarreraRepository carRepo, MateriaRepository matRepo, PlanDeEstudioRepository planRepo, PlanMateriaRepository pmRepo) {
        System.out.println("... Cargando Ingeniería Eléctrica");
        Carrera carrera = crearCarrera(facu, "IEE", "Ingeniería en Energía Eléctrica", carRepo);
        PlanDeEstudio plan = crearPlan(carrera, "Plan 2023", planRepo);

        asociar(plan, "Análisis Matemático I", TipoMateria.BASICA, 5, 1, matRepo, pmRepo);
        asociar(plan, "Álgebra y Geometría Analítica", TipoMateria.BASICA, 5, 1, matRepo, pmRepo);
        asociar(plan, "Ingeniería y Sociedad", TipoMateria.BASICA, 2, 1, matRepo, pmRepo);
        asociar(plan, "Sistemas de Representación", TipoMateria.BASICA, 3, 1, matRepo, pmRepo);
        asociar(plan, "Física I", TipoMateria.BASICA, 5, 1, matRepo, pmRepo);
        asociar(plan, "Química General", TipoMateria.BASICA, 5, 1, matRepo, pmRepo);
        asociar(plan, "Integración Eléctrica I", TipoMateria.ESPECIFICA, 3, 1, matRepo, pmRepo);
        asociar(plan, "Fundamentos de Informática", TipoMateria.BASICA, 2, 1, matRepo, pmRepo);

        // Nivel 2
        asociar(plan, "Física II", TipoMateria.BASICA, 5, 2, matRepo, pmRepo);
        asociar(plan, "Probabilidad y Estadística", TipoMateria.BASICA, 3, 2, matRepo, pmRepo);
        asociar(plan, "Electrotecnia I", TipoMateria.ESPECIFICA, 6, 2, matRepo, pmRepo);
        asociar(plan, "Estabilidad", TipoMateria.ESPECIFICA, 4, 2, matRepo, pmRepo);
        asociar(plan, "Mecánica Técnica", TipoMateria.ESPECIFICA, 2, 2, matRepo, pmRepo);
        asociar(plan, "Integración Eléctrica II", TipoMateria.ESPECIFICA, 3, 2, matRepo, pmRepo);
        asociar(plan, "Inglés I", TipoMateria.BASICA, 2, 2, matRepo, pmRepo);
        asociar(plan, "Análisis Matemático II", TipoMateria.BASICA, 5, 2, matRepo, pmRepo);
        asociar(plan, "Cálculo Numérico", TipoMateria.BASICA, 2, 2, matRepo, pmRepo);

        // Nivel 3
        asociar(plan, "Inglés II", TipoMateria.BASICA, 2, 3, matRepo, pmRepo);
        asociar(plan, "Economía", TipoMateria.BASICA, 3, 3, matRepo, pmRepo);
        asociar(plan, "Instrumentos y Mediciones", TipoMateria.ESPECIFICA, 6, 3, matRepo, pmRepo);
        asociar(plan, "Teoría de los Campos", TipoMateria.ESPECIFICA, 3, 3, matRepo, pmRepo);
        asociar(plan, "Física III", TipoMateria.BASICA, 2, 3, matRepo, pmRepo);
        asociar(plan, "Máquinas Eléctricas I", TipoMateria.ESPECIFICA, 6, 3, matRepo, pmRepo);
        asociar(plan, "Electrotecnia II", TipoMateria.ESPECIFICA, 4, 3, matRepo, pmRepo);
        asociar(plan, "Termodinámica", TipoMateria.ESPECIFICA, 3, 3, matRepo, pmRepo);
        asociar(plan, "Análisis de Señales", TipoMateria.ESPECIFICA, 3, 3, matRepo, pmRepo);
        asociar(plan, "Taller Interdisciplinario", TipoMateria.ESPECIFICA, 2, 3, matRepo, pmRepo);

        // Nivel 4
        asociar(plan, "Electrónica I", TipoMateria.ESPECIFICA, 4, 4, matRepo, pmRepo);
        asociar(plan, "Máquinas Eléctricas II", TipoMateria.ESPECIFICA, 6, 4, matRepo, pmRepo);
        asociar(plan, "Instalaciones Eléctricas y Luminotecnia", TipoMateria.ESPECIFICA, 6, 4, matRepo, pmRepo);
        asociar(plan, "Control Automático", TipoMateria.ESPECIFICA, 5, 4, matRepo, pmRepo);
        asociar(plan, "Máquinas Térmicas e Hidráulicas", TipoMateria.ESPECIFICA, 3, 4, matRepo, pmRepo);
        asociar(plan, "Legislación", TipoMateria.BASICA, 2, 4, matRepo, pmRepo);
        asociar(plan, "Seguridad y Medio Ambiente", TipoMateria.ESPECIFICA, 2, 4, matRepo, pmRepo);

        // Nivel 5
        asociar(plan, "Electrónica II", TipoMateria.ESPECIFICA, 3, 5, matRepo, pmRepo);
        asociar(plan, "Gen. Transmisión y Distr. de Energía", TipoMateria.ESPECIFICA, 6, 5, matRepo, pmRepo);
        asociar(plan, "Sistemas de Potencia", TipoMateria.ESPECIFICA, 4, 5, matRepo, pmRepo);
        asociar(plan, "Accionamientos y Controles", TipoMateria.ESPECIFICA, 4, 5, matRepo, pmRepo);
        asociar(plan, "Org. y Adm. de Empresas", TipoMateria.BASICA, 2, 5, matRepo, pmRepo);
        asociar(plan, "Proyecto Final Eléctrica", TipoMateria.ESPECIFICA, 2, 5, matRepo, pmRepo);
    }

    // INGENIERÍA QUÍMICA

    private void cargarQuimica(FacultadRegional facu, CarreraRepository carRepo, MateriaRepository matRepo, PlanDeEstudioRepository planRepo, PlanMateriaRepository pmRepo) {
        System.out.println("... Cargando Ingeniería Química");
        Carrera carrera = crearCarrera(facu, "IQ", "Ingeniería Química", carRepo);
        PlanDeEstudio plan = crearPlan(carrera, "Plan 2023", planRepo);

        // Nivel 1
        asociar(plan, "Introd. a la Ingeniería Química", TipoMateria.ESPECIFICA, 3, 1, matRepo, pmRepo);
        asociar(plan, "Ingeniería y Sociedad", TipoMateria.BASICA, 2, 1, matRepo, pmRepo);
        asociar(plan, "Álgebra y Geometría Analítica", TipoMateria.BASICA, 5, 1, matRepo, pmRepo);
        asociar(plan, "Análisis Matemático I", TipoMateria.BASICA, 5, 1, matRepo, pmRepo);
        asociar(plan, "Física I", TipoMateria.BASICA, 5, 1, matRepo, pmRepo);
        asociar(plan, "Química General", TipoMateria.BASICA, 5, 1, matRepo, pmRepo);
        asociar(plan, "Sistemas de Representación", TipoMateria.BASICA, 2, 1, matRepo, pmRepo);
        asociar(plan, "Fundamentos de Informática", TipoMateria.BASICA, 2, 1, matRepo, pmRepo);

        // Nivel 2
        asociar(plan, "Introd. a Equipos y Procesos", TipoMateria.ESPECIFICA, 3, 2, matRepo, pmRepo);
        asociar(plan, "Probabilidad y Estadística", TipoMateria.BASICA, 3, 2, matRepo, pmRepo);
        asociar(plan, "Química Inorgánica", TipoMateria.ESPECIFICA, 4, 2, matRepo, pmRepo);
        asociar(plan, "Análisis Matemático II", TipoMateria.BASICA, 5, 2, matRepo, pmRepo);
        asociar(plan, "Física II", TipoMateria.BASICA, 5, 2, matRepo, pmRepo);
        asociar(plan, "Química Orgánica", TipoMateria.ESPECIFICA, 5, 2, matRepo, pmRepo);
        asociar(plan, "Legislación", TipoMateria.BASICA, 2, 2, matRepo, pmRepo);
        asociar(plan, "Inglés I", TipoMateria.BASICA, 2, 2, matRepo, pmRepo);
        asociar(plan, "Balances de Masa y Energía", TipoMateria.ESPECIFICA, 3, 2, matRepo, pmRepo);
        asociar(plan, "Termodinámica", TipoMateria.ESPECIFICA, 4, 2, matRepo, pmRepo);

        // Nivel 3
        asociar(plan, "Matemática Superior Aplicada", TipoMateria.BASICA, 3, 3, matRepo, pmRepo);
        asociar(plan, "Ciencia de los Materiales", TipoMateria.ESPECIFICA, 2, 3, matRepo, pmRepo);
        asociar(plan, "Fisicoquímica", TipoMateria.ESPECIFICA, 4, 3, matRepo, pmRepo);
        asociar(plan, "Fenómenos de Transporte", TipoMateria.ESPECIFICA, 5, 3, matRepo, pmRepo);
        asociar(plan, "Química Analítica", TipoMateria.ESPECIFICA, 4, 3, matRepo, pmRepo);
        asociar(plan, "Microbiología y Química Biológica", TipoMateria.ESPECIFICA, 3, 3, matRepo, pmRepo);
        asociar(plan, "Química Aplicada", TipoMateria.ESPECIFICA, 2, 3, matRepo, pmRepo);
        asociar(plan, "Inglés II", TipoMateria.BASICA, 2, 3, matRepo, pmRepo);
        asociar(plan, "Diseño y Simulación de Procesos", TipoMateria.ESPECIFICA, 4, 3, matRepo, pmRepo);

        // Nivel 4
        asociar(plan, "Operaciones Unitarias I", TipoMateria.ESPECIFICA, 5, 4, matRepo, pmRepo);
        asociar(plan, "Tecnología de la Energía Térmica", TipoMateria.ESPECIFICA, 5, 4, matRepo, pmRepo);
        asociar(plan, "Economía", TipoMateria.BASICA, 3, 4, matRepo, pmRepo);
        asociar(plan, "Operaciones Unitarias II", TipoMateria.ESPECIFICA, 5, 4, matRepo, pmRepo);
        asociar(plan, "Ingeniería de las Reacciones", TipoMateria.ESPECIFICA, 5, 4, matRepo, pmRepo);
        asociar(plan, "Organización Industrial", TipoMateria.BASICA, 3, 4, matRepo, pmRepo);
        asociar(plan, "Calidad y Control Estadístico", TipoMateria.ESPECIFICA, 3, 4, matRepo, pmRepo);
        asociar(plan, "Control Automático de Procesos", TipoMateria.ESPECIFICA, 4, 4, matRepo, pmRepo);

        // Nivel 5
        asociar(plan, "Mecánica Industrial", TipoMateria.ESPECIFICA, 3, 5, matRepo, pmRepo);
        asociar(plan, "Ingeniería Ambiental", TipoMateria.ESPECIFICA, 3, 5, matRepo, pmRepo);
        asociar(plan, "Procesos Biotecnológicos", TipoMateria.ESPECIFICA, 3, 5, matRepo, pmRepo);
        asociar(plan, "Higiene y Seguridad", TipoMateria.ESPECIFICA, 2, 5, matRepo, pmRepo);
        asociar(plan, "Máquinas e Instalaciones Eléctricas", TipoMateria.ESPECIFICA, 2, 5, matRepo, pmRepo);
        asociar(plan, "Proyecto Final Química", TipoMateria.ESPECIFICA, 4, 5, matRepo, pmRepo);
    }

    // USUARIOS DE PRUEBA

    private void cargarUsuarios(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        // ADMIN
        crearUsuario(usuarioRepository, passwordEncoder, "1", "Admin", "Sistema", "admin@sysacad.com", RolUsuario.ADMIN);
        // PROFESOR
        crearUsuario(usuarioRepository, passwordEncoder, "DOC-2024", "Nicolas", "Cabello", "nic@sysacad.com", RolUsuario.PROFESOR);
        // ESTUDIANTE
        crearUsuario(usuarioRepository, passwordEncoder, "45123", "Marty", "McFly", "marty@sysacad.com", RolUsuario.ESTUDIANTE);
    }



    private Carrera crearCarrera(FacultadRegional facu, String idCarrera, String nombre, CarreraRepository repo) {
        Carrera c = new Carrera();
        c.setId(new Carrera.CarreraId(facu.getId(), idCarrera));
        c.setNombre(nombre);
        c.setFacultad(facu);
        return repo.save(c);
    }

    private PlanDeEstudio crearPlan(Carrera carrera, String nombre, PlanDeEstudioRepository repo) {
        PlanDeEstudio p = new PlanDeEstudio();
        p.setId(new PlanDeEstudio.PlanId(carrera.getId().getIdFacultad(), carrera.getId().getIdCarrera(), nombre));
        p.setFechaInicio(LocalDate.of(2023, 3, 1));
        p.setEsVigente(true);
        return repo.save(p);
    }

    private void asociar(PlanDeEstudio plan, String nombreMat, TipoMateria tipo, int horas, int nivel, MateriaRepository mRepo, PlanMateriaRepository pmRepo) {
        // 1. Busco o creo la materia (Evita duplicar "Análisis I" 5 veces)
        Materia materia = materiasCache.computeIfAbsent(nombreMat, k -> {
            Materia m = new Materia();
            m.setNombre(nombreMat);
            m.setTipoMateria(tipo);
            m.setDuracion(DuracionMateria.CUATRIMESTRAL); // Default, ajustar si es anual
            m.setHorasCursado((short) horas);
            m.setOptativa(false);
            m.setRendirLibre(true);
            return mRepo.save(m);
        });

        // 2. La asocio al plan
        PlanMateria pm = new PlanMateria();
        pm.setId(new PlanMateria.PlanMateriaId(
                plan.getId().getIdFacultad(),
                plan.getId().getIdCarrera(),
                plan.getId().getNombre(),
                materia.getId()
        ));
        pm.setMateria(materia);
        pm.setPlan(plan);
        pm.setCodigoMateria("COD-" + nombreMat.hashCode()); // Generamos un código ficticio
        pm.setNivel((short) nivel);
        pmRepo.save(pm);
    }

    private void cargarElectivas(PlanDeEstudio plan, MateriaRepository mRepo, PlanMateriaRepository pmRepo, int nivelMinimo, String... nombres) {
        for (String nombre : nombres) {
            Materia m = new Materia();
            m.setNombre(nombre);
            m.setTipoMateria(TipoMateria.ESPECIFICA); // O Electiva si tuvieras el Enum
            m.setOptativa(true);
            m.setHorasCursado((short) 4);
            m.setDuracion(DuracionMateria.CUATRIMESTRAL);
            m = mRepo.save(m);

            PlanMateria pm = new PlanMateria();
            pm.setId(new PlanMateria.PlanMateriaId(plan.getId().getIdFacultad(), plan.getId().getIdCarrera(), plan.getId().getNombre(), m.getId()));
            pm.setMateria(m);
            pm.setPlan(plan);
            pm.setCodigoMateria("ELEC-" + nombre.hashCode());
            pm.setNivel((short) nivelMinimo);
            pmRepo.save(pm);
        }
    }

    private void crearUsuario(UsuarioRepository repo, PasswordEncoder encoder, String legajo, String nombre, String apellido, String mail, RolUsuario rol) {
        Usuario u = new Usuario();
        u.setLegajo(legajo);
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setMail(mail);
        u.setDni(legajo + "000"); // Fake DNI
        u.setPassword(encoder.encode("1234")); // Password hasheada
        u.setRol(rol);
        u.setTipoDocumento(TipoDocumento.DNI);
        u.setGenero(Genero.M);
        u.setEstado("ACTIVO");
        u.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        u.setFechaIngreso(LocalDate.now());
        repo.save(u);
    }
}