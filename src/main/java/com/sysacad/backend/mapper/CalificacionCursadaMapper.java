package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.calificacion_cursada.CalificacionCursadaResponse;
import com.sysacad.backend.modelo.CalificacionCursada;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CalificacionCursadaMapper {

    CalificacionCursadaMapper INSTANCE = Mappers.getMapper(CalificacionCursadaMapper.class);

    CalificacionCursadaResponse toDTO(CalificacionCursada calificacion);

    List<CalificacionCursadaResponse> toDTOs(List<CalificacionCursada> calificaciones);
}
