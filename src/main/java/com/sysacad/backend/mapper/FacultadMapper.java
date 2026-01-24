package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.facultad.FacultadRequest;
import com.sysacad.backend.dto.facultad.FacultadResponse;
import com.sysacad.backend.modelo.FacultadRegional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FacultadMapper {

    FacultadMapper INSTANCE = Mappers.getMapper(FacultadMapper.class);

    FacultadResponse toDTO(FacultadRegional facultad);

    @Mapping(target = "id", ignore = true)
    FacultadRegional toEntity(FacultadRequest request);

    List<FacultadResponse> toDTOs(List<FacultadRegional> facultades);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(FacultadRequest request, @MappingTarget FacultadRegional facultad);
}
