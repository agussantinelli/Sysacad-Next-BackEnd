package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.grupo.GrupoResponse;
import com.sysacad.backend.modelo.Grupo;
import com.sysacad.backend.modelo.Materia;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class GrupoMapperTest {

    private final GrupoMapper mapper = Mappers.getMapper(GrupoMapper.class);

    @Test
    @DisplayName("Debe mapear Grupo a DTO")
    void toDTO_Success() {
        Grupo grupo = new Grupo();
        grupo.setId(1L);
        grupo.setNombre("Grupo 1");
        
        Materia materia = new Materia();
        materia.setNombre("Materia X");
        grupo.setMateria(materia);

        GrupoResponse dto = mapper.toDTO(grupo);

        assertNotNull(dto);
        assertEquals("Grupo 1", dto.getNombre());
        assertEquals("Materia X", dto.getNombreMateria());
    }
}
