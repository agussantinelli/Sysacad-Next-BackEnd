package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.salon.SalonResponse;
import com.sysacad.backend.modelo.Salon;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SalonMapperTest {

    private final SalonMapper mapper = Mappers.getMapper(SalonMapper.class);

    @Test
    @DisplayName("Debe mapear Salon a DTO")
    void toDTO_Success() {
        UUID id = UUID.randomUUID();
        Salon salon = new Salon();
        salon.setId(id);
        salon.setNombre("Aula 101");

        SalonResponse dto = mapper.toDTO(salon);

        assertNotNull(dto);
        assertEquals(id, dto.getId());
        assertEquals("Aula 101", dto.getNombre());
    }
}
