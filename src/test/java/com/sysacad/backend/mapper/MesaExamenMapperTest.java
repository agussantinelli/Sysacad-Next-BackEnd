package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.mesa_examen.MesaExamenResponse;
import com.sysacad.backend.modelo.MesaExamen;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class MesaExamenMapperTest {

    private final MesaExamenMapper mapper = Mappers.getMapper(MesaExamenMapper.class);

    @Test
    @DisplayName("Debe mapear MesaExamen a DTO")
    void toDTO_Success() {
        UUID id = UUID.randomUUID();
        MesaExamen mesa = new MesaExamen();
        mesa.setId(id);
        mesa.setNombre("Mesa 1");

        MesaExamenResponse dto = mapper.toDTO(mesa);

        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("Mesa 1", dto.getNombre());
    }
}
