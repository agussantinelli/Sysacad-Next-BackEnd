package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.salon.SalonRequest;
import com.sysacad.backend.dto.salon.SalonResponse;
import com.sysacad.backend.modelo.Salon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SalonMapper {

    SalonMapper INSTANCE = Mappers.getMapper(SalonMapper.class);

    @Mapping(source = "facultad.ciudad", target = "nombreFacultad")
    SalonResponse toDTO(Salon salon);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "facultad", ignore = true)
    Salon toEntity(SalonRequest request);

    List<SalonResponse> toDTOs(List<Salon> salones);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "facultad", ignore = true)
    void updateEntityFromRequest(SalonRequest request, @MappingTarget Salon salon);
}
