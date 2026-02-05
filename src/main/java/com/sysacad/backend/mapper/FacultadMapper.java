package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.facultad.FacultadRequest;
import com.sysacad.backend.dto.facultad.FacultadResponse;
import com.sysacad.backend.modelo.Carrera;
import com.sysacad.backend.modelo.FacultadRegional;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FacultadMapper {

    FacultadMapper INSTANCE = Mappers.getMapper(FacultadMapper.class);

    @Mapping(target = "nombreCompleto", expression = "java(\"UTN - \" + facultad.getCiudad())")
    @Mapping(target = "cantidadMatriculados", ignore = true)
    @Mapping(target = "carreras", source = "carreras", qualifiedByName = "mapCarreras")
    FacultadResponse toDTO(FacultadRegional facultad);

    @Named("mapCarreras")
    default List<String> mapCarreras(java.util.Set<Carrera> carreras) {
        if (carreras == null) {
            return java.util.Collections.emptyList();
        }
        return carreras.stream()
                .map(Carrera::getNombre)
                .collect(java.util.stream.Collectors.toList());
    }

    @Mapping(target = "id", ignore = true)
    FacultadRegional toEntity(FacultadRequest request);

    List<FacultadResponse> toDTOs(List<FacultadRegional> facultades);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(FacultadRequest request, @MappingTarget FacultadRegional facultad);
}
