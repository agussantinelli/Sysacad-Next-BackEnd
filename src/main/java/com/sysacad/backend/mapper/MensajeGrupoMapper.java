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
    @Mapping(source = "usuario.id", target = "idUsuarioRemitente")
    @Mapping(target = "nombreRemitente", expression = "java(mensaje.getUsuario().getNombre() + \" \" + mensaje.getUsuario().getApellido())")
    @Mapping(source = "fechaEnvio", target = "fechaEnvio")
    MensajeGrupoResponse toDTO(MensajeGrupo mensaje);

    List<MensajeGrupoResponse> toDTOs(List<MensajeGrupo> mensajes);
}
