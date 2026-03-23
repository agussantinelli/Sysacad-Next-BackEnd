package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.facultad.FacultadResponse;
import com.sysacad.backend.modelo.Carrera;
import com.sysacad.backend.modelo.FacultadRegional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class FacultadMapperTest {

    private final FacultadMapper mapper = Mappers.getMapper(FacultadMapper.class);

    @Test
    @DisplayName("Debe mapear FacultadRegional a DTO con nombre completo generado y lista de carreras")
    void toDTO_Success() {
        // Arrange
        FacultadRegional facultad = new FacultadRegional();
        facultad.setId(UUID.randomUUID());
        facultad.setCiudad("Rosario");
        
        Carrera carrera = new Carrera();
        carrera.setId(UUID.randomUUID());
        carrera.setNombre("ISI");
        facultad.setCarreras(Set.of(carrera));

        // Act
        FacultadResponse dto = mapper.toDTO(facultad);

        // Assert
        assertNotNull(dto);
        assertEquals("UTN - Rosario", dto.getNombreCompleto());
        assertTrue(dto.getCarreras().contains("ISI"));
    }
}
