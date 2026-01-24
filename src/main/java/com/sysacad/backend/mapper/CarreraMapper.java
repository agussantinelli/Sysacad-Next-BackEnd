package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.carrera.CarreraRequest;
import com.sysacad.backend.dto.carrera.CarreraResponse;
import com.sysacad.backend.modelo.Carrera;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CarreraMapper {

    CarreraMapper INSTANCE = Mappers.getMapper(CarreraMapper.class);

    @Mapping(target = "facultades", source = "facultades", qualifiedByName = "mapFacultades")
    CarreraResponse toDTO(Carrera carrera);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "facultades", ignore = true)
    Carrera toEntity(CarreraRequest request);

    List<CarreraResponse> toDTOs(List<Carrera> carreras);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "facultades", ignore = true)
    void updateEntityFromRequest(CarreraRequest request, @MappingTarget Carrera carrera);

    @Named("mapFacultades")
    default List<String> mapFacultades(Set<com.sysacad.backend.modelo.FacultadRegional> facultades) {
        if (facultades == null) {
            return null;
        }
        return facultades.stream()
                .map(com.sysacad.backend.modelo.FacultadRegional::getCiudad)
                .collect(Collectors.toList());
    }
}
