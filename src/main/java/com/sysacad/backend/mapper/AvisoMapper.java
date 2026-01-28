package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.aviso.AvisoRequest;
import com.sysacad.backend.dto.aviso.AvisoResponse;
import com.sysacad.backend.modelo.Aviso;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AvisoMapper {

    AvisoMapper INSTANCE = Mappers.getMapper(AvisoMapper.class);

    AvisoResponse toDTO(Aviso aviso);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaEmision", ignore = true) 
    Aviso toEntity(AvisoRequest request);

    List<AvisoResponse> toDTOs(List<Aviso> avisos);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "fechaEmision", ignore = true)
    void updateEntityFromRequest(AvisoRequest request, @MappingTarget Aviso aviso);
}
