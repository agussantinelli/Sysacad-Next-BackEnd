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

    @Autowired
    public ProfesorService(AsignacionMateriaRepository asignacionMateriaRepository,
                           ComisionRepository comisionRepository,
                           HorarioCursadoRepository horarioCursadoRepository,
                           PlanMateriaRepository planMateriaRepository) {
        this.asignacionMateriaRepository = asignacionMateriaRepository;
        this.comisionRepository = comisionRepository;
        this.horarioCursadoRepository = horarioCursadoRepository;
        this.planMateriaRepository = planMateriaRepository;
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
        List<Comision> comisiones = comisionRepository.findByMateriasIdAndProfesoresId(idMateria, idProfesor);

        return comisiones.stream()
                .map(comision -> mapToComisionHorarioDTO(comision, idMateria))
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
        
        return new MateriaProfesorDTO(
                materia.getId(),
                materia.getNombre(),
                nivel,
                planDescripcion,
                asignacion.getCargo()
        );
    }

    private ComisionHorarioDTO mapToComisionHorarioDTO(Comision comision, UUID idMateria) {
        // Fetch schedules for this commission-subject pair
        List<HorarioCursado> horarios = horarioCursadoRepository.findByIdIdComisionAndIdIdMateria(
                comision.getId(), idMateria);

        List<String> horarioFormateado = horarios.stream()
                .map(h -> String.format("%s %s - %s",
                        h.getId().getDia().name(),
                        h.getId().getHoraDesde().toString(),
                        h.getHoraHasta().toString()))
                .collect(Collectors.toList());

        return new ComisionHorarioDTO(
                comision.getId(),
                comision.getNombre(),
                comision.getAnio(),
                comision.getTurno(),
                comision.getSalon().getNombre(),
                horarioFormateado
        );
    }
}
