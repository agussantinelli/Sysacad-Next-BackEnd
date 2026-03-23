package com.sysacad.backend.mapper;

import com.sysacad.backend.dto.carrera.CarreraResponse;
import com.sysacad.backend.modelo.Carrera;
import com.sysacad.backend.modelo.FacultadRegional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CarreraMapperTest {

    private final CarreraMapper mapper = Mappers.getMapper(CarreraMapper.class);

    @Test
    @DisplayName("Debe mapear Carrera y sus facultades asociadas (lógica default)")
    void toDTO_WithFacultades() {
        // Arrange
        Carrera carrera = new Carrera();
        carrera.setId(UUID.randomUUID());
        carrera.setNombre("Ingeniería");
        
        FacultadRegional frro = new FacultadRegional();
        frro.setId(UUID.randomUUID());
        frro.setCiudad("Rosario");
        
        FacultadRegional frsf = new FacultadRegional();
        frsf.setId(UUID.randomUUID());
        frsf.setCiudad("Santa Fe");
        
        carrera.setFacultades(new HashSet<>(Set.of(frro, frsf)));

        // Act
        CarreraResponse dto = mapper.toDTO(carrera);

        // Assert
        assertNotNull(dto);
        assertEquals("Ingeniería", dto.getNombre());
        assertEquals(2, dto.getFacultades().size());
        assertTrue(dto.getFacultades().contains("Rosario"));
        assertTrue(dto.getFacultades().contains("Santa Fe"));
    }
}
