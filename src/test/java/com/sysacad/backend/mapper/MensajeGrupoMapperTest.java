package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.grupo.MensajeGrupoResponse;
import com.sysacad.backend.modelo.Grupo;
import com.sysacad.backend.modelo.MensajeGrupo;
import com.sysacad.backend.modelo.Usuario;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MensajeGrupoMapperTest {

    private final MensajeGrupoMapper mapper = Mappers.getMapper(MensajeGrupoMapper.class);

    @Test
    @DisplayName("Debe mapear MensajeGrupo a DTO")
    void toDTO_Success() {
        // Arrange
        MensajeGrupo mensaje = new MensajeGrupo();
        mensaje.setContenido("Hola");
        mensaje.setFechaEnvio(LocalDateTime.now());
        
        Usuario remitente = new Usuario();
        remitente.setId(1L);
        remitente.setNombre("Admin");
        remitente.setApellido("User");
        mensaje.setUsuario(remitente);
        
        Grupo grupo = new Grupo();
        grupo.setId(10L);
        mensaje.setGrupo(grupo);

        // Act
        MensajeGrupoResponse dto = mapper.toDTO(mensaje);

        // Assert
        assertNotNull(dto);
        assertEquals("Hola", dto.getContenido());
        assertEquals("Admin", dto.getNombreRemitente());
        assertEquals(10L, dto.getIdGrupo());
    }
}
