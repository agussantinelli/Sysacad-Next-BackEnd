package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.grupo.GrupoResponse;
import com.sysacad.backend.modelo.Grupo;
import com.sysacad.backend.modelo.MiembroGrupo;
import com.sysacad.backend.dto.grupo.MiembroGrupoResponse;
import com.sysacad.backend.dto.grupo.GrupoIntegranteDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GrupoMapper {

    GrupoMapper INSTANCE = Mappers.getMapper(GrupoMapper.class);

    GrupoResponse toDTO(Grupo grupo);

    List<GrupoResponse> toDTOs(List<Grupo> grupos);
    
    @Mapping(source = "usuario.id", target = "idUsuario")
    @Mapping(source = "usuario.nombre", target = "nombre")
    @Mapping(source = "usuario.apellido", target = "apellido")
    @Mapping(source = "rol", target = "rol")
    @Mapping(source = "fechaUnion", target = "fechaUnion")
    @Mapping(source = "ultimoAcceso", target = "ultimoAcceso")
    MiembroGrupoResponse toMiembroDTO(MiembroGrupo miembro);

    List<MiembroGrupoResponse> toMiembroDTOs(List<MiembroGrupo> miembros);

    @Mapping(source = "usuario.id", target = "idUsuario")
    @Mapping(source = "usuario.nombre", target = "nombre")
    @Mapping(source = "usuario.apellido", target = "apellido")
    @Mapping(source = "usuario.fotoPerfil", target = "foto")
    @Mapping(source = "rol", target = "rol")
    GrupoIntegranteDTO toIntegranteDTO(MiembroGrupo miembro);

    List<GrupoIntegranteDTO> toIntegranteDTOs(List<MiembroGrupo> miembros);
}
