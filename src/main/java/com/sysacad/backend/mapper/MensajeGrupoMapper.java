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
    @Mapping(source = "usuario.nombre", target = "nombreRemitente")
    @Mapping(source = "usuario.apellido", target = "apellidoRemitente")
    @Mapping(source = "usuario.fotoPerfil", target = "fotoRemitente")
    @Mapping(source = "fechaEnvio", target = "fechaEnvio")
    @Mapping(target = "leido", ignore = true)
    MensajeGrupoResponse toDTO(MensajeGrupo mensaje);

    List<MensajeGrupoResponse> toDTOs(List<MensajeGrupo> mensajes);
}
