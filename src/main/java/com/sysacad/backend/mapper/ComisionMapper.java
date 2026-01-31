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

    public ComisionResponse toDTO(Comision comision) {
        if (comision == null) {
            return null;
        }
        return new ComisionResponse(comision);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "materias", ignore = true) 
    @Mapping(target = "profesores", ignore = true)
    public abstract Comision toEntity(ComisionRequest request);

    public abstract List<ComisionResponse> toDTOs(List<Comision> comisiones);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "materias", ignore = true)
    @Mapping(target = "profesores", ignore = true)
    public abstract void updateEntityFromRequest(ComisionRequest request, @MappingTarget Comision comision);
}
