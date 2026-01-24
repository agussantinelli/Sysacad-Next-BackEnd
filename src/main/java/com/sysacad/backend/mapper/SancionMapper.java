package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.sancion.SancionRequest;
import com.sysacad.backend.dto.sancion.SancionResponse;
import com.sysacad.backend.modelo.Sancion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SancionMapper {

    SancionMapper INSTANCE = Mappers.getMapper(SancionMapper.class);

    SancionResponse toDTO(Sancion sancion);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "usuario", ignore = true)
    Sancion toEntity(SancionRequest request);

    List<SancionResponse> toDTOs(List<Sancion> sanciones);
}
