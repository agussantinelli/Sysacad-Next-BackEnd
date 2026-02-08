package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.detalle_mesa_examen.DetalleMesaExamenRequest;
import com.sysacad.backend.dto.detalle_mesa_examen.DetalleMesaExamenResponse;
import com.sysacad.backend.modelo.DetalleMesaExamen;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface DetalleMesaExamenMapper {

    DetalleMesaExamenMapper INSTANCE = Mappers.getMapper(DetalleMesaExamenMapper.class);

    @Mapping(source = "id.idMesaExamen", target = "idMesaExamen")
    @Mapping(source = "id.nroDetalle", target = "nroDetalle")
    @Mapping(source = "materia.nombre", target = "nombreMateria")
    @Mapping(source = "materia.id", target = "idMateria")
    @Mapping(source = "presidente.id", target = "idPresidente")
    @Mapping(expression = "java(detalle.getPresidente() != null ? detalle.getPresidente().getNombre() + \" \" + detalle.getPresidente().getApellido() : null)", target = "nombrePresidente")
    DetalleMesaExamenResponse toDTO(DetalleMesaExamen detalle);

    @Mapping(target = "id", ignore = true) 
    @Mapping(target = "mesaExamen", ignore = true)
    @Mapping(target = "materia", ignore = true)
    @Mapping(target = "presidente", ignore = true)
    DetalleMesaExamen toEntity(DetalleMesaExamenRequest request);
    
    List<DetalleMesaExamenResponse> toDTOs(List<DetalleMesaExamen> detalles);
}
