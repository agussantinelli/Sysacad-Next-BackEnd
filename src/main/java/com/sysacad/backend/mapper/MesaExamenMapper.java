package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.mesa_examen.MesaExamenRequest;
import com.sysacad.backend.dto.mesa_examen.MesaExamenResponse;
import com.sysacad.backend.modelo.MesaExamen;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring", uses = {DetalleMesaExamenMapper.class})
public interface MesaExamenMapper {

    MesaExamenMapper INSTANCE = Mappers.getMapper(MesaExamenMapper.class);

    @Mapping(source = "id", target = "id")
    MesaExamenResponse toDTO(MesaExamen mesa);

    @Mapping(target = "id", ignore = true)
    MesaExamen toEntity(MesaExamenRequest request);

    List<MesaExamenResponse> toDTOs(List<MesaExamen> mesas);

    @Mapping(target = "id", ignore = true)
    void updateEntityFromRequest(MesaExamenRequest request, @MappingTarget MesaExamen mesa);
}
