package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.grupo.GrupoResponse;
import com.sysacad.backend.modelo.Grupo;
import com.sysacad.backend.modelo.Materia;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GrupoMapperTest {

    private final GrupoMapper mapper = Mappers.getMapper(GrupoMapper.class);

    @Test
    @DisplayName("Debe mapear Grupo a DTO")
    void toDTO_Success() {
        Grupo grupo = new Grupo();
        grupo.setId(UUID.randomUUID());
        grupo.setNombre("Grupo 1");
        grupo.setDescripcion("Una descripcion");

        GrupoResponse dto = mapper.toDTO(grupo);

        assertNotNull(dto);
        assertEquals("Grupo 1", dto.getNombre());
        assertEquals("Una descripcion", dto.getDescripcion());
    }
}
