package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.plan.PlanDeEstudioRequest;
import com.sysacad.backend.dto.plan.PlanDeEstudioResponse;
import com.sysacad.backend.modelo.PlanDeEstudio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PlanDeEstudioMapper {

    PlanDeEstudioMapper INSTANCE = Mappers.getMapper(PlanDeEstudioMapper.class);

    @Mapping(source = "carrera.nombre", target = "nombreCarrera")
    PlanDeEstudioResponse toDTO(PlanDeEstudio plan);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "carrera", ignore = true)
    @Mapping(source = "nombrePlan", target = "nombre") 
    PlanDeEstudio toEntity(PlanDeEstudioRequest request);

    List<PlanDeEstudioResponse> toDTOs(List<PlanDeEstudio> planes);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "carrera", ignore = true)
    void updateEntityFromRequest(PlanDeEstudioRequest request, @MappingTarget PlanDeEstudio plan);
}
