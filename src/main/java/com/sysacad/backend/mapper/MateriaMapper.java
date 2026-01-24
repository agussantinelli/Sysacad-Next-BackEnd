package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.materia.MateriaRequest;
import com.sysacad.backend.dto.materia.MateriaResponse;
import com.sysacad.backend.modelo.Materia;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MateriaMapper {

    MateriaMapper INSTANCE = Mappers.getMapper(MateriaMapper.class);

    MateriaResponse toDTO(Materia materia);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "correlativas", ignore = true) // Se manejan por separado
    Materia toEntity(MateriaRequest request);

    List<MateriaResponse> toDTOs(List<Materia> materias);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "correlativas", ignore = true)
    void updateEntityFromRequest(MateriaRequest request, @MappingTarget Materia materia);
}
