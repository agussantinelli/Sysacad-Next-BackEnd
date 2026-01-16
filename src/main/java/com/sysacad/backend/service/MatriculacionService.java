package com.sysacad.backend.service;

import com.sysacad.backend.dto.CarreraMateriasDTO;
import com.sysacad.backend.dto.MateriaResponse;
import com.sysacad.backend.modelo.EstudioUsuario;
import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.modelo.PlanDeEstudio;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.repository.EstudioUsuarioRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sysacad.backend.repository.InscripcionRepository;
import com.sysacad.backend.repository.CalificacionRepository;
import com.sysacad.backend.modelo.Inscripcion;
import com.sysacad.backend.modelo.Calificacion;
import com.sysacad.backend.modelo.PlanMateria;
import com.sysacad.backend.modelo.Comision;
import com.sysacad.backend.dto.EstudianteMateriaDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MatriculacionService {

    private final EstudioUsuarioRepository estudioUsuarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final InscripcionRepository inscripcionRepository;
    private final CalificacionRepository calificacionRepository;

    @Autowired
    public MatriculacionService(EstudioUsuarioRepository estudioUsuarioRepository,
            UsuarioRepository usuarioRepository,
            InscripcionRepository inscripcionRepository,
            CalificacionRepository calificacionRepository) {
        this.estudioUsuarioRepository = estudioUsuarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.inscripcionRepository = inscripcionRepository;
        this.calificacionRepository = calificacionRepository;
    }

    @Transactional
    public EstudioUsuario matricularAlumno(EstudioUsuario estudio) {
        Usuario alumno = usuarioRepository.findById(estudio.getId().getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (alumno.getRol() != RolUsuario.ESTUDIANTE) {
            throw new RuntimeException("Solo se pueden matricular usuarios con rol ESTUDIANTE");
        }

        estudio.setFechaInscripcion(LocalDate.now());
        estudio.setEstado("ACTIVO");
        return estudioUsuarioRepository.save(estudio);
    }

    @Transactional(readOnly = true)
    public List<EstudioUsuario> obtenerCarrerasPorAlumno(UUID idAlumno) {
        return estudioUsuarioRepository.findByIdIdUsuario(idAlumno);
    }

    @Transactional(readOnly = true)
    public List<CarreraMateriasDTO> obtenerMateriasPorCarreraDelAlumno(String legajo) {
        // 1. Obtener usuario real
        Usuario alumno = usuarioRepository.findByLegajo(legajo)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado con legajo: " + legajo));

        // 2. Obtener inscripciones (EstudioUsuario - Matriculación en carreras)
        List<EstudioUsuario> matriculaciones = estudioUsuarioRepository.findByIdIdUsuario(alumno.getId());
        List<CarreraMateriasDTO> resultado = new ArrayList<>();

        // 3. Pre-cargar el historial académico completo del alumno para evitar N+1
        // queries
        List<Inscripcion> historialInscripciones = inscripcionRepository.findByIdIdUsuario(alumno.getId());
        List<Calificacion> historialNotas = calificacionRepository.findByIdIdUsuario(alumno.getId());

        // 3.1 Construir mapa de ESTADO por Materia ID
        Map<UUID, EstadoMateria> mapaEstadoMaterias = construirMapaEstadoMaterias(historialInscripciones,
                historialNotas);

        // 4. Iterar sobre cada carrera
        for (EstudioUsuario matricula : matriculaciones) {
            PlanDeEstudio plan = matricula.getPlan();

            if (plan != null) {
                String idCarrera = plan.getCarrera().getId().getIdCarrera();
                String nombreCarrera = plan.getCarrera().getNombre();
                String nombrePlan = plan.getId().getNombre();

                // Obtener las materias del plan con su metadata (nivel, correlativas)
                List<PlanMateria> planMaterias = plan.getPlanMaterias(); // Usamos la nueva relación directa

                List<EstudianteMateriaDTO> materiasDTO = new ArrayList<>();

                for (PlanMateria pm : planMaterias) {
                    Materia materia = pm.getMateria();

                    // Obtener estado actual
                    EstadoMateria estadoActual = mapaEstadoMaterias.getOrDefault(materia.getId(),
                            new EstadoMateria("PENDIENTE", "-"));

                    // Verificar correlativas para saber si se puede inscribir
                    boolean sePuedeInscribir = false;
                    if (estadoActual.estado.equals("PENDIENTE") || estadoActual.estado.equals("LIBRE")) {
                        sePuedeInscribir = verificarCorrelativas(materia, mapaEstadoMaterias);
                    }

                    EstudianteMateriaDTO dto = new EstudianteMateriaDTO(
                            materia.getId(),
                            materia.getNombre(),
                            pm.getNivel(),
                            estadoActual.estado,
                            estadoActual.nota,
                            sePuedeInscribir,
                            materia.getOptativa());
                    materiasDTO.add(dto);
                }

                // Ordenar por nivel y luego nombre para prolijidad
                materiasDTO.sort(Comparator.comparing(EstudianteMateriaDTO::getNivel)
                        .thenComparing(EstudianteMateriaDTO::getNombre));

                resultado.add(new CarreraMateriasDTO(idCarrera, nombreCarrera, nombrePlan, materiasDTO));
            }
        }

        return resultado;
    }

    // --- Helpers de Lógica de Negocio ---

    private Map<UUID, EstadoMateria> construirMapaEstadoMaterias(List<Inscripcion> inscripciones,
            List<Calificacion> calificaciones) {
        Map<UUID, EstadoMateria> mapa = new HashMap<>();

        // Paso 1: Procesar Inscripciones (Define CURSANDO o REGULAR)
        for (Inscripcion insc : inscripciones) {
            // Una inscripción es a una comisión, que tiene Múltiples Materias.
            // Asumimos que la inscripción aplica a TODAS las materias de la comisión.
            Comision com = insc.getComision();
            if (com != null && com.getMaterias() != null) {
                for (Materia mat : com.getMaterias()) {
                    String estado = "CURSANDO";
                    if ("REGULAR".equalsIgnoreCase(insc.getCondicion())) {
                        estado = "REGULAR";
                    } else if ("LIBRE".equalsIgnoreCase(insc.getCondicion())) {
                        estado = "LIBRE";
                    }
                    // Guardamos el mejor estado encontrado hasta ahora (Jerarquía: REGULAR >
                    // CURSANDO > PENDIENTE)
                    mapa.put(mat.getId(), new EstadoMateria(estado, "-"));
                }
            }
        }

        // Paso 2: Procesar Calificaciones (Define APROBADA y Nota)
        // Esto es "loose coupling" porque la calificacion no apunta directo a materia,
        // sino via String concepto
        // Intentaremos matchear inteligentemente o usar la inscripción previa
        for (Calificacion calif : calificaciones) {
            // La calificación está ligada a una inscripción.
            // Buscamos las materias de esa inscripción.
            Inscripcion insc = calif.getInscripcion();
            if (insc != null && insc.getComision() != null && insc.getComision().getMaterias() != null) {
                for (Materia mat : insc.getComision().getMaterias()) {
                    // Checkeo difuso: Si el concepto contiene el nombre de la materia OR es un
                    // final genérico
                    // Para simplificar, si la inscripción tiene nota final >= 6, asumimos APROBADA
                    // todas las materias de esa comisión?
                    // NO, eso es peligroso.
                    // Vamos a confiar en el concepto.
                    boolean esEstaMateria = calif.getId().getConcepto().toLowerCase()
                            .contains(mat.getNombre().toLowerCase());
                    boolean esAprobada = calif.getNota().doubleValue() >= 6.0;

                    if (esEstaMateria && esAprobada) {
                        mapa.put(mat.getId(), new EstadoMateria("APROBADA", calif.getNota().toString()));
                    }
                }
            }
        }

        // Paso 3: Revisar notas directas en Inscripción (algunos modelos guardan nota
        // final ahí)
        for (Inscripcion insc : inscripciones) {
            if (insc.getNotaFinal() != null && insc.getNotaFinal().doubleValue() >= 6.0) {
                if (insc.getComision() != null && insc.getComision().getMaterias() != null) {
                    for (Materia mat : insc.getComision().getMaterias()) {
                        // Si tiene nota final en la inscripción, asumimos que aprobó TODO el bloque
                        // (ej. cuatrimestre) o es inscripción única
                        mapa.put(mat.getId(), new EstadoMateria("APROBADA", insc.getNotaFinal().toString()));
                    }
                }
            }
        }

        return mapa;
    }

    private boolean verificarCorrelativas(Materia materia, Map<UUID, EstadoMateria> historial) {
        if (materia.getCorrelativas() == null || materia.getCorrelativas().isEmpty()) {
            return true; // Sin correlativas = libre inscripción
        }

        for (Materia correlativa : materia.getCorrelativas()) {
            EstadoMateria estadoCorr = historial.get(correlativa.getId());
            // Regla de negocio estándar: Para cursar, necesitas la correlativa REGULARIZADA
            // o APROBADA
            boolean correlativaOk = estadoCorr != null &&
                    (estadoCorr.estado.equals("REGULAR") || estadoCorr.estado.equals("APROBADA"));

            if (!correlativaOk) {
                return false; // Falló una correlativa
            }
        }
        return true;
    }

    // Helper interno
    private static class EstadoMateria {
        String estado;
        String nota;

        public EstadoMateria(String estado, String nota) {
            this.estado = estado;
            this.nota = nota;
        }
    }
}