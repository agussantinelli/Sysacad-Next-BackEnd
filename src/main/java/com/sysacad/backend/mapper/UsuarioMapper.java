package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.usuario.UsuarioRequest;
import com.sysacad.backend.dto.usuario.UsuarioResponse;
import com.sysacad.backend.modelo.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {
    
    UsuarioMapper INSTANCE = Mappers.getMapper(UsuarioMapper.class);

    UsuarioResponse toDTO(Usuario usuario);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // La contraseña se maneja aparte
    @Mapping(target = "rol", ignore = true) // El rol suele asigna por lógica
    @Mapping(target = "fotoPerfil", ignore = true)
    Usuario toEntity(UsuarioRequest request);
    
    List<UsuarioResponse> toDTOs(List<Usuario> usuarios);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) 
    @Mapping(target = "rol", ignore = true)
    @Mapping(target = "fotoPerfil", ignore = true) // Se actualiza con lógica específica si viene
    void updateEntityFromRequest(UsuarioRequest request, @MappingTarget Usuario usuario);
}
