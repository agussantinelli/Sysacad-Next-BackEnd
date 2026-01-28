package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.grupo.MensajeGrupoResponse;
import com.sysacad.backend.modelo.MensajeGrupo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface MensajeGrupoMapper {

    MensajeGrupoMapper INSTANCE = Mappers.getMapper(MensajeGrupoMapper.class);

    @Mapping(source = "grupo.id", target = "idGrupo")
    @Mapping(source = "usuario.id", target = "idRemitente")
    @Mapping(source = "usuario.nombre", target = "nombreRemitente")
    @Mapping(source = "usuario.apellido", target = "apellidoRemitente")
    @Mapping(source = "fechaEnvio", target = "fechaEnvio")
    MensajeGrupoResponse toDTO(MensajeGrupo mensaje);

    List<MensajeGrupoResponse> toDTOs(List<MensajeGrupo> mensajes);
}
