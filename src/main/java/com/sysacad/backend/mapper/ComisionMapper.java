package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.comision.ComisionRequest;
import com.sysacad.backend.dto.comision.ComisionResponse;
import com.sysacad.backend.modelo.Comision;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ComisionMapper {

    public static final ComisionMapper INSTANCE = Mappers.getMapper(ComisionMapper.class);

    @org.springframework.beans.factory.annotation.Autowired
    protected com.sysacad.backend.repository.AsignacionMateriaRepository asignacionMateriaRepository;

    public ComisionResponse toDTO(Comision comision) {
        if (comision == null) {
            return null;
        }
        ComisionResponse dto = new ComisionResponse(comision);
        
        // Populate Materias Detalles (Profesores per Materia)
        if (comision.getMaterias() != null && comision.getProfesores() != null) {
            java.util.List<ComisionResponse.MateriaDetalleDTO> detalles = new java.util.ArrayList<>();
            
            for (com.sysacad.backend.modelo.Materia m : comision.getMaterias()) {
                java.util.List<ComisionResponse.ProfesorResumenDTO> profesDocentes = new java.util.ArrayList<>();
                
                for (com.sysacad.backend.modelo.Usuario p : comision.getProfesores()) {
                    // Check if professor is assigned to this materia
                    com.sysacad.backend.modelo.AsignacionMateria.AsignacionMateriaId id = 
                        new com.sysacad.backend.modelo.AsignacionMateria.AsignacionMateriaId(p.getId(), m.getId());
                    
                    if (asignacionMateriaRepository.existsById(id)) {
                         profesDocentes.add(new ComisionResponse.ProfesorResumenDTO(p.getLegajo(), p.getNombre() + " " + p.getApellido()));
                    }
                }
                detalles.add(new ComisionResponse.MateriaDetalleDTO(m.getNombre(), m.getId(), profesDocentes));
            }
            dto.setMateriasDetalle(detalles);
        }
        
        return dto;
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "materias", ignore = true) 
    @Mapping(target = "profesores", ignore = true)
    @Mapping(target = "salon", ignore = true)
    public abstract Comision toEntity(ComisionRequest request);

    public abstract List<ComisionResponse> toDTOs(List<Comision> comisiones);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "materias", ignore = true)
    @Mapping(target = "profesores", ignore = true)
    @Mapping(target = "salon", ignore = true)
    public abstract void updateEntityFromRequest(ComisionRequest request, @MappingTarget Comision comision);
}
