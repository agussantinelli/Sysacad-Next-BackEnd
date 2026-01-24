package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.carrera.CarreraRequest;
import com.sysacad.backend.dto.carrera.CarreraResponse;
import com.sysacad.backend.modelo.Carrera;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CarreraMapper {

    CarreraMapper INSTANCE = Mappers.getMapper(CarreraMapper.class);

    CarreraResponse toDTO(Carrera carrera);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "facultades", ignore = true)
    @Mapping(target = "planes", ignore = true)
    Carrera toEntity(CarreraRequest request);

    List<CarreraResponse> toDTOs(List<Carrera> carreras);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "facultades", ignore = true)
    @Mapping(target = "planes", ignore = true)
    void updateEntityFromRequest(CarreraRequest request, @MappingTarget Carrera carrera);
}
