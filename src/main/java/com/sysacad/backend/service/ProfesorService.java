package com.sysacad.backend.service;

import com.sysacad.backend.dto.comision.ComisionDetalladaDTO;
import com.sysacad.backend.dto.comision.ComisionHorarioDTO;
import com.sysacad.backend.dto.materia.MateriaProfesorDTO;
import com.sysacad.backend.modelo.AsignacionMateria;
import com.sysacad.backend.modelo.Comision;
import com.sysacad.backend.modelo.HorarioCursado;
import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.modelo.PlanMateria;
import com.sysacad.backend.repository.AsignacionMateriaRepository;
import com.sysacad.backend.repository.ComisionRepository;
import com.sysacad.backend.repository.HorarioCursadoRepository;
import com.sysacad.backend.repository.PlanMateriaRepository;
import com.sysacad.backend.repository.InscripcionCursadoRepository;
import com.sysacad.backend.repository.InstanciaEvaluacionRepository;
import com.sysacad.backend.dto.examen.ProfesorMesaExamenDTO;
import com.sysacad.backend.dto.examen.ProfesorDetalleExamenDTO;
import com.sysacad.backend.dto.examen.MiembroTribunalDTO;
import com.sysacad.backend.repository.DetalleMesaExamenRepository;
import com.sysacad.backend.repository.InscripcionExamenRepository;
import com.sysacad.backend.modelo.DetalleMesaExamen;
import com.sysacad.backend.modelo.MesaExamen;
import com.sysacad.backend.repository.CalificacionCursadaRepository;
import com.sysacad.backend.modelo.CalificacionCursada;
import com.sysacad.backend.dto.comision.AlumnoCursadaDTO;
import com.sysacad.backend.dto.comision.CargaNotasCursadaDTO;
import com.sysacad.backend.dto.comision.NotaCursadaItemDTO;
import com.sysacad.backend.dto.comision.CalificacionDTO;
import java.util.Set;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.sysacad.backend.dto.examen.AlumnoExamenDTO;
import com.sysacad.backend.dto.examen.CargaNotaItemDTO;
import com.sysacad.backend.modelo.InscripcionExamen;
import com.sysacad.backend.modelo.enums.EstadoExamen;

@Service
public class ProfesorService {

    private final AsignacionMateriaRepository asignacionMateriaRepository;
    private final ComisionRepository comisionRepository;
    private final HorarioCursadoRepository horarioCursadoRepository;
    private final PlanMateriaRepository planMateriaRepository;
    private final InscripcionCursadoRepository inscripcionCursadoRepository;
    private final DetalleMesaExamenRepository detalleMesaExamenRepository;
    private final InscripcionExamenRepository inscripcionExamenRepository;
    private final CalificacionCursadaRepository calificacionCursadaRepository;

    private final InstanciaEvaluacionRepository instanciaEvaluacionRepository;
    private final GrupoService grupoService;
    private final IEmailService emailService;

    @Autowired
    public ProfesorService(AsignacionMateriaRepository asignacionMateriaRepository,
                           ComisionRepository comisionRepository,
                           HorarioCursadoRepository horarioCursadoRepository,
                           PlanMateriaRepository planMateriaRepository,
                           InscripcionCursadoRepository inscripcionCursadoRepository,
                           DetalleMesaExamenRepository detalleMesaExamenRepository,
                           InscripcionExamenRepository inscripcionExamenRepository,
                           CalificacionCursadaRepository calificacionCursadaRepository,
                           com.sysacad.backend.repository.InstanciaEvaluacionRepository instanciaEvaluacionRepository,
                           GrupoService grupoService,
                           IEmailService emailService) {
        this.asignacionMateriaRepository = asignacionMateriaRepository;
        this.comisionRepository = comisionRepository;
        this.horarioCursadoRepository = horarioCursadoRepository;
        this.planMateriaRepository = planMateriaRepository;
        this.inscripcionCursadoRepository = inscripcionCursadoRepository;
        this.detalleMesaExamenRepository = detalleMesaExamenRepository;
        this.inscripcionExamenRepository = inscripcionExamenRepository;
        this.calificacionCursadaRepository = calificacionCursadaRepository;
        this.instanciaEvaluacionRepository = instanciaEvaluacionRepository;
        this.grupoService = grupoService;
        this.emailService = emailService;
    }

    @Transactional(readOnly = true)
    public List<MateriaProfesorDTO> obtenerMateriasAsignadas(UUID idProfesor) {
        List<AsignacionMateria> asignaciones = asignacionMateriaRepository.findByIdIdUsuario(idProfesor);

        return asignaciones.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ComisionHorarioDTO> obtenerComisionesDeMateria(UUID idProfesor, UUID idMateria) {
        // 1. Verificar el cargo del profesor en esta materia
        List<AsignacionMateria> asignaciones = asignacionMateriaRepository.findByIdIdUsuario(idProfesor);
        AsignacionMateria asignacionMateria = asignaciones.stream()
                .filter(a -> a.getMateria().getId().equals(idMateria))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Profesor no asignado a esta materia"));

        boolean esJefeCatedra = asignacionMateria.getCargo() == com.sysacad.backend.modelo.enums.RolCargo.JEFE_CATEDRA;

        // 2. Obtener las comisiones según el rol
        List<Comision> comisiones;
        if (esJefeCatedra) {
            // Si es jefe de cátedra: traer TODAS las comisiones de esta materia
            comisiones = comisionRepository.findByMateriasId(idMateria);
        } else {
            // Si NO es jefe: traer solo las comisiones donde este profesor participa
            comisiones = comisionRepository.findByMateriasIdAndProfesoresId(idMateria, idProfesor);
        }

        // 3. Mapear a DTO
        return comisiones.stream()
                .map(comision -> mapToComisionHorarioDTO(comision, idMateria, esJefeCatedra))
                .collect(Collectors.toList());
    }

    private MateriaProfesorDTO mapToDTO(AsignacionMateria asignacion) {
        Materia materia = asignacion.getMateria();

        // Query PlanMateria to get nivel and plan information
        List<PlanMateria> planMaterias = planMateriaRepository.findByIdIdMateria(materia.getId());

        // Use the first plan (in case a subject appears in multiple plans, we take the first one)
        // In a real scenario, you might want to filter by vigente or current year
        Integer nivel = null;
        String planDescripcion = "N/A";

        if (!planMaterias.isEmpty()) {
            PlanMateria planMateria = planMaterias.get(0);
            nivel = planMateria.getNivel() != null ? planMateria.getNivel().intValue() : null;

            if (planMateria.getPlan() != null) {
                planDescripcion = planMateria.getPlan().getNombre() + " - " +
                        planMateria.getPlan().getCarrera().getNombre();
            }
        }

        // Si NO es jefe de cátedra, buscar quién es el jefe
        String nombreJefe = null;
        boolean esJefe = asignacion.getCargo() == com.sysacad.backend.modelo.enums.RolCargo.JEFE_CATEDRA;

        if (!esJefe) {
            List<AsignacionMateria> jefes = asignacionMateriaRepository.findByIdIdMateriaAndCargo(
                    materia.getId(), com.sysacad.backend.modelo.enums.RolCargo.JEFE_CATEDRA);

            if (!jefes.isEmpty()) {
                AsignacionMateria jefe = jefes.get(0);
                nombreJefe = jefe.getProfesor().getNombre() + " " + jefe.getProfesor().getApellido();
            }
        }

        return new MateriaProfesorDTO(
                materia.getId(),
                materia.getNombre(),
                nivel,
                planDescripcion,
                asignacion.getCargo(),
                nombreJefe
        );
    }

    private ComisionHorarioDTO mapToComisionHorarioDTO(Comision comision, UUID idMateria, boolean esJefeCatedra) {
        // Fetch schedules for this commission-subject pair
        List<HorarioCursado> horarios = horarioCursadoRepository.findByIdIdComisionAndIdIdMateria(
                comision.getId(), idMateria);

        List<String> horarioFormateado = horarios.stream()
                .map(h -> String.format("%s %s - %s",
                        h.getId().getDia().name(),
                        h.getId().getHoraDesde().toString(),
                        h.getHoraHasta().toString()))
                .collect(Collectors.toList());

        // Mostrar todos los profesores que dan clase en esta comisión para esta materia
        List<String> profesores = comision.getProfesores().stream()
                .filter(profesor -> {
                    List<AsignacionMateria> asignaciones = asignacionMateriaRepository
                            .findByIdIdUsuario(profesor.getId());
                    return asignaciones.stream()
                            .anyMatch(a -> a.getMateria().getId().equals(idMateria));
                })
                .map(profesor -> profesor.getNombre() + " " + profesor.getApellido())
                .collect(Collectors.toList());

        // Contar alumnos activos o recién calificados
        java.time.LocalDate fechaLimite = java.time.LocalDate.now().minusMonths(3);
        long cantidadAlumnos = inscripcionCursadoRepository.countInscriptosActivosOSemiesActivos(
                comision.getId(), idMateria, fechaLimite);

        return new ComisionHorarioDTO(
                comision.getId(),
                comision.getNombre(),
                comision.getAnio(),
                comision.getTurno(),
                comision.getSalon().getNombre(),
                horarioFormateado,
                profesores,
                cantidadAlumnos
        );
    }

    @Transactional(readOnly = true)
    public List<ComisionDetalladaDTO> obtenerTodasLasComisiones(UUID idProfesor) {
        // 1. Obtener todas las comisiones donde el profesor da clases
        List<Comision> comisiones = comisionRepository.findByProfesoresId(idProfesor);

        // 2. Obtener todas las asignaciones del profesor para saber qué materias dicta
        List<AsignacionMateria> misAsignaciones = asignacionMateriaRepository.findByIdIdUsuario(idProfesor);

        // 3. Generar DTOs
        // Una comisión puede tener varias materias, pero el profesor solo dicta algunas de ellas.
        // Generamos un DTO por cada par (Comisión, Materia) válido.

        List<ComisionDetalladaDTO> resultado = new java.util.ArrayList<>();

        for (Comision comision : comisiones) {
            // Filtrar las materias de la comisión que el profesor dicta
            List<Materia> materiasQueDicta = comision.getMaterias().stream()
                    .filter(materiaComision -> misAsignaciones.stream()
                            .anyMatch(asignacion -> asignacion.getMateria().getId().equals(materiaComision.getId())))
                    .collect(Collectors.toList());

            // Si es jefe de cátedra en alguna de ellas, necesitamos saberlo para el mapeo
            for (Materia materia : materiasQueDicta) {
                // Determinar si es jefe de cátedra para ESTA materia
                boolean esJefe = misAsignaciones.stream()
                        .anyMatch(a -> a.getMateria().getId().equals(materia.getId()) &&
                                a.getCargo() == com.sysacad.backend.modelo.enums.RolCargo.JEFE_CATEDRA);

                resultado.add(mapToComisionDetalladaDTO(comision, materia, esJefe, idProfesor));
            }
        }

        return resultado;
    }

    private ComisionDetalladaDTO mapToComisionDetalladaDTO(Comision comision, Materia materia, boolean esJefeCatedra, UUID idProfesor) {
        // Obtenemos el DTO base reutilizando lógica similar (pero adaptada)
        // Fetch schedules for this commission-subject pair
        List<HorarioCursado> horarios = horarioCursadoRepository.findByIdIdComisionAndIdIdMateria(
                comision.getId(), materia.getId());

        List<String> horarioFormateado = horarios.stream()
                .map(h -> String.format("%s %s - %s",
                        h.getId().getDia().name(),
                        h.getId().getHoraDesde().toString(),
                        h.getHoraHasta().toString()))
                .collect(Collectors.toList());

        List<String> profesores = comision.getProfesores().stream()
                .filter(profesor -> {
                    List<AsignacionMateria> asignaciones = asignacionMateriaRepository
                            .findByIdIdUsuario(profesor.getId());
                    return asignaciones.stream()
                            .anyMatch(a -> a.getMateria().getId().equals(materia.getId()));
                })
                .map(profesor -> profesor.getNombre() + " " + profesor.getApellido())
                .collect(Collectors.toList());

        // Contar alumnos activos o recién calificados
        java.time.LocalDate fechaLimite = java.time.LocalDate.now().minusMonths(3);
        long cantidadAlumnos = inscripcionCursadoRepository.countInscriptosActivosOSemiesActivos(
                comision.getId(), materia.getId(), fechaLimite);

        return new ComisionDetalladaDTO(
                comision.getId(),
                comision.getNombre(),
                comision.getAnio(),
                comision.getTurno(),
                comision.getSalon().getNombre(),
                horarioFormateado,
                profesores,
                cantidadAlumnos,
                materia.getNombre(),
                materia.getId()
        );
    }

    @Transactional(readOnly = true)
    public List<ProfesorMesaExamenDTO> obtenerMesasExamenProfesor(UUID idProfesor) {
        // Obtenemos todos los detalles relevantes
        Set<DetalleMesaExamen> detallesRelevantes = obtenerDetallesExamenRelevantes(idProfesor);

        // Agrupar por MesaExamen
        java.util.Map<MesaExamen, Long> mesasCount = detallesRelevantes.stream()
                .collect(Collectors.groupingBy(DetalleMesaExamen::getMesaExamen, Collectors.counting()));

        // Mapear a DTO
        return mesasCount.entrySet().stream()
                .map(entry -> {
                    MesaExamen mesa = entry.getKey();
                    return new ProfesorMesaExamenDTO(
                            mesa.getId(),
                            mesa.getNombre(),
                            mesa.getFechaInicio(),
                            mesa.getFechaFin(),
                            entry.getValue().intValue()
                    );
                })
                .sorted(java.util.Comparator.comparing(ProfesorMesaExamenDTO::fechaInicio).reversed())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProfesorDetalleExamenDTO> obtenerDetallesMesaProfesor(UUID idProfesor, UUID idMesa) {
        // Filtrar detalles relevantes solo de esta mesa
        List<DetalleMesaExamen> detallesDeMesa = detalleMesaExamenRepository.findByMesaExamenId(idMesa);

        List<AsignacionMateria> asignaciones = asignacionMateriaRepository.findByIdIdUsuario(idProfesor);

        // Logica de filtrado manual para asegurar coherencia
        return detallesDeMesa.stream()
                .filter(detalle -> esProfesorRelevanteParaDetalle(detalle, idProfesor, asignaciones))
                .map(detalle -> mapToProfesorDetalleExamenDTO(detalle))
                .collect(Collectors.toList());
    }

    private Set<DetalleMesaExamen> obtenerDetallesExamenRelevantes(UUID idProfesor) {
        Set<DetalleMesaExamen> detalles = new HashSet<>();

        // 1. Donde es Presidente
        detalles.addAll(detalleMesaExamenRepository.findByPresidenteId(idProfesor));

        // 2. Donde es Auxiliar
        detalles.addAll(detalleMesaExamenRepository.findByAuxiliaresId(idProfesor));

        // 3. Donde es Jefe de Cátedra de la materia
        List<AsignacionMateria> asignacionesJefe = asignacionMateriaRepository.findByIdIdUsuario(idProfesor).stream()
                .filter(a -> a.getCargo() == com.sysacad.backend.modelo.enums.RolCargo.JEFE_CATEDRA)
                .collect(Collectors.toList());

        for (AsignacionMateria asignacion : asignacionesJefe) {
            detalles.addAll(detalleMesaExamenRepository.findByMateriaId(asignacion.getMateria().getId()));
        }

        return detalles;
    }

    private boolean esProfesorRelevanteParaDetalle(DetalleMesaExamen detalle, UUID idProfesor, List<AsignacionMateria> asignaciones) {
        // Es Presidente?
        if (detalle.getPresidente().getId().equals(idProfesor)) return true;

        // Es Auxiliar?
        boolean esAuxiliar = detalle.getAuxiliares() != null && detalle.getAuxiliares().stream()
                .anyMatch(aux -> aux.getId().equals(idProfesor));
        if (esAuxiliar) return true;

        // Es Jefe de Catedra de la materia?
        return asignaciones.stream()
                .anyMatch(a -> a.getMateria().getId().equals(detalle.getMateria().getId()) &&
                        a.getCargo() == com.sysacad.backend.modelo.enums.RolCargo.JEFE_CATEDRA);
    }

    private ProfesorDetalleExamenDTO mapToProfesorDetalleExamenDTO(DetalleMesaExamen detalle) {
        // Obtener inscriptos
        Long inscriptos = inscripcionExamenRepository.countByDetalleMesaExamenId(detalle.getId());

        // Obtener anio materia
        String anioMateria = "N/A";
        List<PlanMateria> pm = planMateriaRepository.findByIdIdMateria(detalle.getMateria().getId());
        if (!pm.isEmpty()) {
            anioMateria = pm.get(0).getNivel().toString(); // Simplification
        }

        // Construir tribunal
        List<MiembroTribunalDTO> tribunal = new java.util.ArrayList<>();

        // Presidente
        tribunal.add(new MiembroTribunalDTO(
                detalle.getPresidente().getNombre() + " " + detalle.getPresidente().getApellido(),
                "PRESIDENTE"
        ));

        // Auxiliares
        if (detalle.getAuxiliares() != null) {
            detalle.getAuxiliares().forEach(aux -> {
                tribunal.add(new MiembroTribunalDTO(
                        aux.getNombre() + " " + aux.getApellido(),
                        "AUXILIAR"
                ));
            });
        }

        // Check if all are corrected
        long pendientes = inscripcionExamenRepository.countByDetalleMesaExamenIdAndEstado(
                detalle.getId(), com.sysacad.backend.modelo.enums.EstadoExamen.PENDIENTE);
        boolean todosCorregidos = (pendientes == 0 && inscriptos > 0); // If 0 enrolled, also consider corrected? Or N/A. usually true or false.
        // Let's say if 0 enrolled, todosCorregidos = true (trivially).

        return new ProfesorDetalleExamenDTO(
                detalle.getMesaExamen().getId(),
                detalle.getId().getNroDetalle(),
                detalle.getMateria().getId(),
                detalle.getMateria().getNombre(),
                anioMateria,
                detalle.getDiaExamen(),
                detalle.getHoraExamen(),
                inscriptos,
                todosCorregidos,
                tribunal
        );
    }

    @Transactional(readOnly = true)
    public List<AlumnoExamenDTO> obtenerInscriptosExamen(UUID idProfesor, UUID idMesa, Integer nroDetalle) {
        DetalleMesaExamen.DetalleId idDetalle = new DetalleMesaExamen.DetalleId(idMesa, nroDetalle);
        DetalleMesaExamen detalle = detalleMesaExamenRepository.findById(idDetalle)
                .orElseThrow(() -> new RuntimeException("Detalle de mesa no encontrado"));

        List<AsignacionMateria> asignaciones = asignacionMateriaRepository.findByIdIdUsuario(idProfesor);

        if (!esProfesorRelevanteParaDetalle(detalle, idProfesor, asignaciones)) {
            throw new RuntimeException("Acceso denegado: No eres parte del tribunal de este examen.");
        }

        List<InscripcionExamen> inscriptos = inscripcionExamenRepository.findByDetalleMesaExamenId(idDetalle);

        return inscriptos.stream()
                .map(i -> new AlumnoExamenDTO(
                        i.getId(),
                        i.getUsuario().getNombre(),
                        i.getUsuario().getApellido(),
                        Long.parseLong(i.getUsuario().getLegajo()),
                        i.getEstado(),
                        i.getNota(),
                        i.getTomo(),
                        i.getFolio()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void cargarNotasLote(UUID idProfesor, List<CargaNotaItemDTO> notas) {
        List<AsignacionMateria> asignaciones = asignacionMateriaRepository.findByIdIdUsuario(idProfesor);

        for (CargaNotaItemDTO notaDTO : notas) {
            InscripcionExamen inscripcion = inscripcionExamenRepository.findById(notaDTO.getIdInscripcion())
                    .orElseThrow(() -> new RuntimeException("Inscripción no encontrada: " + notaDTO.getIdInscripcion()));

            DetalleMesaExamen detalle = inscripcion.getDetalleMesaExamen();
            if (detalle == null) {
                 throw new RuntimeException("Inscripción sin detalle de mesa asociado");
            }

            if (!esProfesorRelevanteParaDetalle(detalle, idProfesor, asignaciones)) {
                throw new RuntimeException("Acceso denegado: No tienes permiso para calificar este examen.");
            }

            // Calcular estado basado en la nota
            if (notaDTO.getNota() != null) {
                inscripcion.setNota(notaDTO.getNota());
                if (notaDTO.getNota().compareTo(new java.math.BigDecimal("6.00")) >= 0) {
                    inscripcion.setEstado(EstadoExamen.APROBADO);
                } else {
                    inscripcion.setEstado(EstadoExamen.DESAPROBADO);
                }
            } else if (notaDTO.getEstado() == EstadoExamen.AUSENTE) {
                inscripcion.setNota(null);
                inscripcion.setEstado(EstadoExamen.AUSENTE);
            }

            if (inscripcion.getEstado() == EstadoExamen.APROBADO) {
                // Tomo: Guardar si viene en DTO, sino generar random solo si no existe
                if (notaDTO.getTomo() != null && !notaDTO.getTomo().isBlank()) {
                    inscripcion.setTomo(notaDTO.getTomo().trim());
                } else if (inscripcion.getTomo() == null) {
                   inscripcion.setTomo(String.valueOf(100 + (int)(Math.random() * 900)));
                }

                // Folio: Guardar si viene en DTO, sino generar random solo si no existe
                if (notaDTO.getFolio() != null && !notaDTO.getFolio().isBlank()) {
                    inscripcion.setFolio(notaDTO.getFolio().trim());
                } else if (inscripcion.getFolio() == null) {
                    inscripcion.setFolio(String.valueOf(10 + (int)(Math.random() * 900)));
                }
            } else {
                // Resetear si no está aprobado
                inscripcion.setTomo(null);
                inscripcion.setFolio(null);
            }
            
            inscripcionExamenRepository.save(inscripcion);

            // Notificación por Email
            try {
                java.util.Map<String, Object> vars = new java.util.HashMap<>();
                vars.put("nombreAlumno", inscripcion.getUsuario().getNombre());
                vars.put("materia", detalle.getMateria().getNombre());
                vars.put("concepto", "Examen Final");
                vars.put("nota", inscripcion.getNota() != null ? inscripcion.getNota().toString() : "N/A");
                vars.put("estado", inscripcion.getEstado().name());
                vars.put("dashboardUrl", "http://localhost:4200/dashboard");

                emailService.sendHtmlEmail(inscripcion.getUsuario().getMail(), 
                        "Nota de Examen: " + detalle.getMateria().getNombre(), "grade-notification", vars);
            } catch (Exception e) {
                System.err.println("Error enviando notificación de nota de examen: " + e.getMessage());
            }
        }
    }


    @Transactional(readOnly = true)
    public List<AlumnoCursadaDTO> obtenerInscriptosCursada(UUID idProfesor, UUID idComision, UUID idMateria) {
        // Verificar si el profesor tiene acceso a esta comisión y materia
        verificarAccesoComisionMateria(idProfesor, idComision, idMateria);

        java.time.LocalDate fechaLimite = java.time.LocalDate.now().minusMonths(3);

        List<com.sysacad.backend.modelo.InscripcionCursado> inscripciones = 
                inscripcionCursadoRepository.findInscriptosActivosOSemiesActivos(
                        idComision, idMateria, fechaLimite);

        return inscripciones.stream()
                .map(i -> new AlumnoCursadaDTO(
                        i.getId(),
                        i.getUsuario().getNombre(),
                        i.getUsuario().getApellido(),
                        Long.parseLong(i.getUsuario().getLegajo()),
                        i.getEstado(),
                        i.getNotaFinal(),
                        i.getCalificaciones().stream()
                            .map(c -> new CalificacionDTO(
                                c.getInstanciaEvaluacion().getNombre(),
                                c.getNota(),
                                c.getFecha()
                            ))
                            .collect(Collectors.toList())
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void cargarNotasCursada(UUID idProfesor, UUID idComision, UUID idMateria, CargaNotasCursadaDTO dto) {
        verificarAccesoComisionMateria(idProfesor, idComision, idMateria);

        for (NotaCursadaItemDTO notaDTO : dto.getNotas()) {
            var inscripcion = inscripcionCursadoRepository.findById(notaDTO.getIdInscripcion())
                    .orElseThrow(() -> new RuntimeException("Inscripción no encontrada: " + notaDTO.getIdInscripcion()));

            // Validar coherencia
            if (!inscripcion.getComision().getId().equals(idComision) || 
                !inscripcion.getMateria().getId().equals(idMateria)) {
                continue; // O lanzar error
            }

            if (Boolean.TRUE.equals(dto.getEsNotaFinal())) {
                // Lógica de Nota Final: Actualizar estado de cursada e inscripción
                if (notaDTO.getEstado() != null) {
                    try {
                        com.sysacad.backend.modelo.enums.EstadoCursada nuevoEstado = 
                                com.sysacad.backend.modelo.enums.EstadoCursada.valueOf(notaDTO.getEstado());
                        
                        if (nuevoEstado == com.sysacad.backend.modelo.enums.EstadoCursada.CURSANDO) {
                            continue;
                        }

                        inscripcion.setEstado(nuevoEstado);
                        
                        // Setear fechas según estado
                        if (nuevoEstado == com.sysacad.backend.modelo.enums.EstadoCursada.REGULAR) {
                             inscripcion.setFechaRegularidad(java.time.LocalDate.now());
                        } else if (nuevoEstado == com.sysacad.backend.modelo.enums.EstadoCursada.PROMOCIONADO) {
                             inscripcion.setFechaPromocion(java.time.LocalDate.now());
                        }
                    } catch (IllegalArgumentException e) {
                        // Log error or ignore invalid status
                    }
                }
                
                if (notaDTO.getNota() != null) {
                    inscripcion.setNotaFinal(notaDTO.getNota());
                }
                
                inscripcionCursadoRepository.save(inscripcion);
                // No creamos calificación parcial si es nota final
                continue;
            }

            // Buscar o crear InstanciaEvaluacion
            String descripcionConcepto = dto.getConcepto().trim();
            com.sysacad.backend.modelo.InstanciaEvaluacion instancia = instanciaEvaluacionRepository.findByNombre(descripcionConcepto)
                    .orElseGet(() -> {
                         com.sysacad.backend.modelo.InstanciaEvaluacion nueva = new com.sysacad.backend.modelo.InstanciaEvaluacion();
                         nueva.setNombre(descripcionConcepto);
                         return instanciaEvaluacionRepository.save(nueva);
                    });

            // Buscar si ya existe la calificacion para este concepto (usando ID de instancia)
            CalificacionCursada calificacion = calificacionCursadaRepository.findByInscripcionCursadoIdAndInstanciaEvaluacionId(
                    inscripcion.getId(), instancia.getId())
                    .orElse(null);

            if (calificacion == null) {
                calificacion = new CalificacionCursada();
                calificacion.setInscripcionCursado(inscripcion);
                calificacion.setInstanciaEvaluacion(instancia);
            }
            
            calificacion.setNota(notaDTO.getNota());
            calificacion.setFecha(java.time.LocalDate.now());
            
            calificacionCursadaRepository.save(calificacion);

            // Notificación por Email
            try {
                java.util.Map<String, Object> vars = new java.util.HashMap<>();
                vars.put("nombreAlumno", inscripcion.getUsuario().getNombre());
                vars.put("materia", inscripcion.getMateria().getNombre());
                vars.put("concepto", instancia.getNombre());
                vars.put("nota", calificacion.getNota().toString());
                vars.put("dashboardUrl", "http://localhost:4200/dashboard");

                emailService.sendHtmlEmail(inscripcion.getUsuario().getMail(),
                        "Nueva Calificación: " + inscripcion.getMateria().getNombre(), "grade-notification", vars);
            } catch (Exception e) {
                System.err.println("Error enviando notificación de calificación de cursada: " + e.getMessage());
            }
        }
    }

    @Transactional(readOnly = true)
    public com.sysacad.backend.dto.profesor.ProfesorEstadisticasDTO obtenerEstadisticasGenerales(UUID idProfesor, Integer anio) {
        long totalAlumnos = inscripcionCursadoRepository.countAlumnosByProfesor(idProfesor, anio);
        long promo = inscripcionCursadoRepository.countAlumnosByProfesorAndEstado(idProfesor, anio, com.sysacad.backend.modelo.enums.EstadoCursada.PROMOCIONADO);
        long regular = inscripcionCursadoRepository.countAlumnosByProfesorAndEstado(idProfesor, anio, com.sysacad.backend.modelo.enums.EstadoCursada.REGULAR);
        long libre = inscripcionCursadoRepository.countAlumnosByProfesorAndEstado(idProfesor, anio, com.sysacad.backend.modelo.enums.EstadoCursada.LIBRE);
        Double avg = inscripcionCursadoRepository.calculateAverageGradeByProfesor(idProfesor, anio);

        long totalExamen = inscripcionExamenRepository.countExamenesByProfesor(idProfesor, anio);
        long aprobados = inscripcionExamenRepository.countExamenesByProfesorAndEstado(idProfesor, anio, com.sysacad.backend.modelo.enums.EstadoExamen.APROBADO);
        long desaprobados = inscripcionExamenRepository.countExamenesByProfesorAndEstado(idProfesor, anio, com.sysacad.backend.modelo.enums.EstadoExamen.DESAPROBADO);
        long ausentes = inscripcionExamenRepository.countExamenesByProfesorAndEstado(idProfesor, anio, com.sysacad.backend.modelo.enums.EstadoExamen.AUSENTE);

        return new com.sysacad.backend.dto.profesor.ProfesorEstadisticasDTO(
                totalAlumnos, promo, regular, libre,
                avg != null ? java.math.BigDecimal.valueOf(avg) : java.math.BigDecimal.ZERO,
                totalExamen, aprobados, desaprobados, ausentes
        );
    }

    @Transactional(readOnly = true)
    public com.sysacad.backend.dto.profesor.ProfesorEstadisticasDTO obtenerEstadisticasPorMateria(UUID idProfesor, UUID idMateria, Integer anio) {
        long totalAlumnos = inscripcionCursadoRepository.countAlumnosByProfesorAndMateria(idProfesor, idMateria, anio);
        long promo = inscripcionCursadoRepository.countAlumnosByProfesorAndMateriaAndEstado(idProfesor, idMateria, anio, com.sysacad.backend.modelo.enums.EstadoCursada.PROMOCIONADO);
        long regular = inscripcionCursadoRepository.countAlumnosByProfesorAndMateriaAndEstado(idProfesor, idMateria, anio, com.sysacad.backend.modelo.enums.EstadoCursada.REGULAR);
        long libre = inscripcionCursadoRepository.countAlumnosByProfesorAndMateriaAndEstado(idProfesor, idMateria, anio, com.sysacad.backend.modelo.enums.EstadoCursada.LIBRE);
        Double avg = inscripcionCursadoRepository.calculateAverageGradeByProfesorAndMateria(idProfesor, idMateria, anio);

        long totalExamen = inscripcionExamenRepository.countExamenesByProfesorAndMateria(idProfesor, idMateria, anio);
        long aprobados = inscripcionExamenRepository.countExamenesByProfesorAndMateriaAndEstado(idProfesor, idMateria, anio, com.sysacad.backend.modelo.enums.EstadoExamen.APROBADO);
        long desaprobados = inscripcionExamenRepository.countExamenesByProfesorAndMateriaAndEstado(idProfesor, idMateria, anio, com.sysacad.backend.modelo.enums.EstadoExamen.DESAPROBADO);
        long ausentes = inscripcionExamenRepository.countExamenesByProfesorAndMateriaAndEstado(idProfesor, idMateria, anio, com.sysacad.backend.modelo.enums.EstadoExamen.AUSENTE);

        return new com.sysacad.backend.dto.profesor.ProfesorEstadisticasDTO(
                totalAlumnos, promo, regular, libre,
                avg != null ? java.math.BigDecimal.valueOf(avg) : java.math.BigDecimal.ZERO,
                totalExamen, aprobados, desaprobados, ausentes
        );
    }

    @Transactional(readOnly = true)
    public List<Integer> obtenerAniosConActividad(UUID idProfesor) {
        List<Integer> aniosComision = comisionRepository.findDistinctAniosByProfesorId(idProfesor);
        List<Integer> aniosExamen = detalleMesaExamenRepository.findDistinctYearsByProfesorId(idProfesor);

        Set<Integer> aniosSet = new HashSet<>(aniosComision);
        aniosSet.addAll(aniosExamen);

        return aniosSet.stream()
                .filter(anio -> anio != null)
                .sorted(java.util.Comparator.reverseOrder())
                .collect(Collectors.toList());
    }

    private void verificarAccesoComisionMateria(UUID idProfesor, UUID idComision, UUID idMateria) {
        // Simple verificación: si el profesor está en esa comisión
        Comision comision = comisionRepository.findById(idComision)
                .orElseThrow(() -> new RuntimeException("Comisión no encontrada"));

        boolean esProfeDeComision = comision.getProfesores().stream()
                .anyMatch(p -> p.getId().equals(idProfesor));
        
        if (!esProfeDeComision) {
            // Verificar si es jefe de catedra global de la materia, quiza quiera ver/editar
            // Por simplicidad, requerimos que esté asignado a la comisión O sea jefe de cátedra
            List<AsignacionMateria> asignaciones = asignacionMateriaRepository.findByIdIdUsuario(idProfesor);
            boolean esJefe = asignaciones.stream()
                    .anyMatch(a -> a.getMateria().getId().equals(idMateria) && 
                              a.getCargo() == com.sysacad.backend.modelo.enums.RolCargo.JEFE_CATEDRA);
            
            if (!esJefe) {
                throw new RuntimeException("Acceso denegado a esta comisión/materia");
            }
        }
    }
}