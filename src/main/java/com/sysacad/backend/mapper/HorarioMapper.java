package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.horario.HorarioCursadoRequest;
import com.sysacad.backend.dto.horario.HorarioCursadoResponse;
import com.sysacad.backend.modelo.HorarioCursado;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface HorarioMapper {

    HorarioMapper INSTANCE = Mappers.getMapper(HorarioMapper.class);

    HorarioCursadoResponse toDTO(HorarioCursado horario);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "comision", ignore = true)
    @Mapping(target = "materia", ignore = true)
    @Mapping(target = "salon", ignore = true)
    HorarioCursado toEntity(HorarioCursadoRequest request);

    List<HorarioCursadoResponse> toDTOs(List<HorarioCursado> horarios);
    
    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(HorarioCursadoRequest request, @MappingTarget HorarioCursado horario);
}
