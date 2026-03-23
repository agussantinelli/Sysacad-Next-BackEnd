package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.usuario.UsuarioResponse;
import com.sysacad.backend.modelo.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class UsuarioMapperTest {

    private final UsuarioMapper mapper = Mappers.getMapper(UsuarioMapper.class);

    @Test
    @DisplayName("Debe mapear Usuario a DTO")
    void toDTO_Success() {
        UUID id = UUID.randomUUID();
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre("Carlos");
        usuario.setApellido("Vazquez");
        usuario.setMail("carlos@test.com");

        UsuarioResponse dto = mapper.toDTO(usuario);

        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("Carlos", dto.getNombre());
        assertEquals("Vazquez", dto.getApellido());
    }
}
