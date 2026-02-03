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
import com.sysacad.backend.dto.examen.ProfesorMesaExamenDTO;
import com.sysacad.backend.dto.examen.ProfesorDetalleExamenDTO;
import com.sysacad.backend.dto.examen.MiembroTribunalDTO;
import com.sysacad.backend.repository.DetalleMesaExamenRepository;
import com.sysacad.backend.repository.InscripcionExamenRepository;
import com.sysacad.backend.modelo.DetalleMesaExamen;
import com.sysacad.backend.modelo.MesaExamen;
import java.util.Set;
import java.util.HashSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProfesorService {

    private final AsignacionMateriaRepository asignacionMateriaRepository;
    private final ComisionRepository comisionRepository;
    private final HorarioCursadoRepository horarioCursadoRepository;
    private final PlanMateriaRepository planMateriaRepository;
    private final InscripcionCursadoRepository inscripcionCursadoRepository;
    private final DetalleMesaExamenRepository detalleMesaExamenRepository;
    private final InscripcionExamenRepository inscripcionExamenRepository;

    @Autowired
    public ProfesorService(AsignacionMateriaRepository asignacionMateriaRepository,
                           ComisionRepository comisionRepository,
                           HorarioCursadoRepository horarioCursadoRepository,
                           PlanMateriaRepository planMateriaRepository,
                           InscripcionCursadoRepository inscripcionCursadoRepository,
                           DetalleMesaExamenRepository detalleMesaExamenRepository,
                           InscripcionExamenRepository inscripcionExamenRepository) {
        this.asignacionMateriaRepository = asignacionMateriaRepository;
        this.comisionRepository = comisionRepository;
        this.horarioCursadoRepository = horarioCursadoRepository;
        this.planMateriaRepository = planMateriaRepository;
        this.inscripcionCursadoRepository = inscripcionCursadoRepository;
        this.detalleMesaExamenRepository = detalleMesaExamenRepository;
        this.inscripcionExamenRepository = inscripcionExamenRepository;
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

        // Contar alumnos cursando esta materia en esta comisión (solo estado CURSANDO)
        long cantidadAlumnos = inscripcionCursadoRepository.countByComisionIdAndMateriaIdAndEstado(
                comision.getId(), idMateria, com.sysacad.backend.modelo.enums.EstadoCursada.CURSANDO);

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

                resultado.add(mapToComisionDetalladaDTO(comision, materia, esJefe));
            }
        }

        return resultado;
    }

    private ComisionDetalladaDTO mapToComisionDetalladaDTO(Comision comision, Materia materia, boolean esJefeCatedra) {
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

        // Contar alumnos cursando esta materia en esta comisión (solo estado CURSANDO)
        long cantidadAlumnos = inscripcionCursadoRepository.countByComisionIdAndMateriaIdAndEstado(
                comision.getId(), materia.getId(), com.sysacad.backend.modelo.enums.EstadoCursada.CURSANDO);

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

        return new ProfesorDetalleExamenDTO(
                detalle.getMesaExamen().getId(),
                detalle.getId().getNroDetalle(),
                detalle.getMateria().getId(),
                detalle.getMateria().getNombre(),
                anioMateria,
                detalle.getDiaExamen(),
                detalle.getHoraExamen(),
                inscriptos,
                tribunal
        );
    }
}