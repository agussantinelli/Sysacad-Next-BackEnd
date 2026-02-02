package com.sysacad.backend.service;

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

    @Autowired
    public ProfesorService(AsignacionMateriaRepository asignacionMateriaRepository,
                           ComisionRepository comisionRepository,
                           HorarioCursadoRepository horarioCursadoRepository,
                           PlanMateriaRepository planMateriaRepository,
                           InscripcionCursadoRepository inscripcionCursadoRepository) {
        this.asignacionMateriaRepository = asignacionMateriaRepository;
        this.comisionRepository = comisionRepository;
        this.horarioCursadoRepository = horarioCursadoRepository;
        this.planMateriaRepository = planMateriaRepository;
        this.inscripcionCursadoRepository = inscripcionCursadoRepository;
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

        List<String> profesores = new java.util.ArrayList<>();
        
        if (esJefeCatedra) {
            // Solo si es jefe: mostrar todos los profesores que dan clase en esta comisión para esta materia
            profesores = comision.getProfesores().stream()
                    .filter(profesor -> {
                        List<AsignacionMateria> asignaciones = asignacionMateriaRepository
                                .findByIdIdUsuario(profesor.getId());
                        return asignaciones.stream()
                                .anyMatch(a -> a.getMateria().getId().equals(idMateria));
                    })
                    .map(profesor -> profesor.getNombre() + " " + profesor.getApellido())
                    .collect(Collectors.toList());
        }
        // Si NO es jefe: lista vacía

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
}
