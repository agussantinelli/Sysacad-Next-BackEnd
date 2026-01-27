package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.inscripcion_cursado.InscripcionCursadoResponse;
import com.sysacad.backend.modelo.InscripcionCursado;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CalificacionCursadaMapper.class})
public interface InscripcionCursadoMapper {

    InscripcionCursadoMapper INSTANCE = Mappers.getMapper(InscripcionCursadoMapper.class);

    @Mapping(source = "materia.nombre", target = "nombreMateria")
    @Mapping(source = "comision.nombre", target = "nombreComision")
    @Mapping(source = "comision.anio", target = "anioCursado")
    @Mapping(source = "estado", target = "estado")
    InscripcionCursadoResponse toDTO(InscripcionCursado inscripcion);

    List<InscripcionCursadoResponse> toDTOs(List<InscripcionCursado> inscripciones);
}
