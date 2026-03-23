package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.comision.SalonResponse;
import com.sysacad.backend.modelo.Salon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class SalonMapperTest {

    private final SalonMapper mapper = Mappers.getMapper(SalonMapper.class);

    @Test
    @DisplayName("Debe mapear Salon a DTO")
    void toDTO_Success() {
        Salon salon = new Salon();
        salon.setId(7L);
        salon.setNombre("Aula 101");

        SalonResponse dto = mapper.toDTO(salon);

        assertNotNull(dto);
        assertEquals(7L, dto.getId());
        assertEquals("Aula 101", dto.getNombre());
    }
}
