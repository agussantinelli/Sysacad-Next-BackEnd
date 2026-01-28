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
public interface ComisionMapper {

    ComisionMapper INSTANCE = Mappers.getMapper(ComisionMapper.class);

    ComisionResponse toDTO(Comision comision);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "materias", ignore = true) 
    @Mapping(target = "profesores", ignore = true)
    Comision toEntity(ComisionRequest request);

    List<ComisionResponse> toDTOs(List<Comision> comisiones);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "materias", ignore = true)
    @Mapping(target = "profesores", ignore = true)
    void updateEntityFromRequest(ComisionRequest request, @MappingTarget Comision comision);
}
