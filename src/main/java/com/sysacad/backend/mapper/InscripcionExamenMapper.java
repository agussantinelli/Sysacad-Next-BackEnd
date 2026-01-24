package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.inscripcion_examen.InscripcionExamenResponse;
import com.sysacad.backend.modelo.InscripcionExamen;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface InscripcionExamenMapper {

    InscripcionExamenMapper INSTANCE = Mappers.getMapper(InscripcionExamenMapper.class);

    @Mapping(source = "usuario.nombre", target = "nombreAlumno") // Mapping complejo mejor en expresión o default method si es concatenación
    @Mapping(source = "usuario.legajo", target = "legajoAlumno")
    @Mapping(source = "detalleMesaExamen.materia.nombre", target = "nombreMateria")
    @Mapping(source = "detalleMesaExamen.diaExamen", target = "fechaExamen")
    @Mapping(source = "detalleMesaExamen.horaExamen", target = "horaExamen")
    InscripcionExamenResponse toDTO(InscripcionExamen inscripcion);

    List<InscripcionExamenResponse> toDTOs(List<InscripcionExamen> inscripciones);
    
    // Custom mapping para nombre completo si es necesario
    default String mapNombreCompleto(com.sysacad.backend.modelo.Usuario usuario) {
        return usuario.getNombre() + " " + usuario.getApellido();
    }
}
