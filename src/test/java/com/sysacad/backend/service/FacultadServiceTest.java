package com.sysacad.backend.service;

import com.sysacad.backend.modelo.FacultadRegional;
import com.sysacad.backend.repository.FacultadRegionalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FacultadServiceTest {

    @Mock private FacultadRegionalRepository facultadRepository;

    @InjectMocks
    private FacultadService facultadService;

    private FacultadRegional facultad;
    private UUID facultadId;

    @BeforeEach
    void setUp() {
        facultadId = UUID.randomUUID();
        facultad = new FacultadRegional();
        facultad.setId(facultadId);
        facultad.setCiudad("Rosario");
        facultad.setProvincia("Santa Fe");
    }

    @Test
    void crearFacultad_DeberiaGuardarCorrectamente_CuandoNoExiste() {
        when(facultadRepository.existsByCiudadAndProvincia("Rosario", "Santa Fe")).thenReturn(false);
        when(facultadRepository.save(any())).thenReturn(facultad);

        FacultadRegional resultado = facultadService.crearFacultad(facultad);

        assertNotNull(resultado);
        verify(facultadRepository).save(facultad);
    }

    @Test
    void crearFacultad_DeberiaLanzarExcepcion_CuandoYaExiste() {
        when(facultadRepository.existsByCiudadAndProvincia("Rosario", "Santa Fe")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> facultadService.crearFacultad(facultad));
    }

    @Test
    void buscarPorId_DeberiaRetornarFacultad_CuandoExiste() {
        when(facultadRepository.findById(facultadId)).thenReturn(Optional.of(facultad));

        FacultadRegional resultado = facultadService.buscarPorId(facultadId);

        assertEquals(facultadId, resultado.getId());
    }

    @Test
    void buscarPorId_DeberiaLanzarExcepcion_CuandoNoExiste() {
        when(facultadRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> facultadService.buscarPorId(UUID.randomUUID()));
    }
}
