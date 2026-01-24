package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.facultad.FacultadRegionalRequest;
import com.sysacad.backend.dto.facultad.FacultadRegionalResponse;
import com.sysacad.backend.modelo.FacultadRegional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FacultadMapper {

    FacultadMapper INSTANCE = Mappers.getMapper(FacultadMapper.class);

    FacultadRegionalResponse toDTO(FacultadRegional facultad);

    @Mapping(target = "id", ignore = true)
    FacultadRegional toEntity(FacultadRegionalRequest request);

    List<FacultadRegionalResponse> toDTOs(List<FacultadRegional> facultades);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(FacultadRegionalRequest request, @MappingTarget FacultadRegional facultad);
}
