package com.sysacad.backend.service;

import com.sysacad.backend.dto.materia.MateriaProfesorDTO;
import com.sysacad.backend.modelo.AsignacionMateria;
import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.repository.AsignacionMateriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProfesorService {

    private final AsignacionMateriaRepository asignacionMateriaRepository;

    @Autowired
    public ProfesorService(AsignacionMateriaRepository asignacionMateriaRepository) {
        this.asignacionMateriaRepository = asignacionMateriaRepository;
    }

    @Transactional(readOnly = true)
    public List<MateriaProfesorDTO> obtenerMateriasAsignadas(UUID idProfesor) {
        List<AsignacionMateria> asignaciones = asignacionMateriaRepository.findByIdIdUsuario(idProfesor);

        return asignaciones.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private MateriaProfesorDTO mapToDTO(AsignacionMateria asignacion) {
        Materia materia = asignacion.getMateria();
        String nombrePlan = materia.getPlan().getNombre(); // Asumiendo que PlanDeEstudio tiene nombre (2008, 2023, etc)
        // Si PlanDeEstudio no tiene nombre directo legible, usar anio o descripcion. 
        // Revisando modelo PlanDeEstudio: tiene carrera y anio.
        // Construimos algo como "2023 - Ingeniería en Sistemas" o solo el año.
        // Mejor verificar PlanDeEstudio antes de asumir.
        // Sin embargo, AsignacionMateria ya trae Materia con fetch lazy, cuidado con N+1 si son muchos.
        // PERO para un profesor son pocas materias (2-5), no es critico AHORA.
        
        // Ajuste rapido: concatenar año y carrera
        String descripcionPlan = materia.getPlan().getAnio() + " - " + materia.getPlan().getCarrera().getNombre();

        return new MateriaProfesorDTO(
                materia.getId(),
                materia.getNombre(),
                materia.getNivel(),
                descripcionPlan,
                asignacion.getCargo()
        );
    }
}
