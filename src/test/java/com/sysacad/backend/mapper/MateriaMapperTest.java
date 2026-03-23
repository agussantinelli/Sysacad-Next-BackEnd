package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.materia.MateriaResponse;
import com.sysacad.backend.modelo.Materia;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MateriaMapperTest {

    private final MateriaMapper mapper = Mappers.getMapper(MateriaMapper.class);

    @Test
    @DisplayName("Debe mapear Materia a DTO")
    void toDTO_Success() {
        UUID id = UUID.randomUUID();
        Materia materia = new Materia();
        materia.setId(id);
        materia.setNombre("Quimica");

        MateriaResponse dto = mapper.toDTO(materia);

        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("Quimica", dto.getNombre());
    }
}
