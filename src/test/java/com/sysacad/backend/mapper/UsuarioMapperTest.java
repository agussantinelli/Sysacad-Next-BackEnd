package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.usuario.UsuarioResponse;
import com.sysacad.backend.modelo.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioMapperTest {

    private final UsuarioMapper mapper = Mappers.getMapper(UsuarioMapper.class);

    @Test
    @DisplayName("Debe mapear Usuario a DTO")
    void toDTO_Success() {
        Usuario usuario = new Usuario();
        usuario.setId(99L);
        usuario.setNombre("Carlos");
        usuario.setApellido("Vazquez");
        usuario.setEmail("carlos@test.com");

        UsuarioResponse dto = mapper.toDTO(usuario);

        assertNotNull(dto);
        assertEquals(99L, dto.getId());
        assertEquals("Carlos", dto.getNombre());
        assertEquals("Vazquez", dto.getApellido());
    }
}
