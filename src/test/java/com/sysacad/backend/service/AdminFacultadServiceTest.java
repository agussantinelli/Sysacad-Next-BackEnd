package com.sysacad.backend.service;

import com.sysacad.backend.dto.admin.FacultadRequest;
import com.sysacad.backend.dto.facultad.FacultadResponse;
import com.sysacad.backend.modelo.FacultadRegional;
import com.sysacad.backend.repository.FacultadRegionalRepository;
import com.sysacad.backend.repository.MatriculacionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminFacultadServiceTest {

    @Mock
    private FacultadRegionalRepository facultadRepository;

    @Mock
    private MatriculacionRepository matriculacionRepository;

    @InjectMocks
    private AdminFacultadService adminFacultadService;

    private FacultadRegional facultad;
    private UUID facultadId;

    @BeforeEach
    void setUp() {
        facultadId = UUID.randomUUID();
        facultad = new FacultadRegional();
        facultad.setId(facultadId);
        facultad.setCiudad("Rosario");
        facultad.setProvincia("Santa Fe");
        facultad.setCarreras(new HashSet<>());
    }

    @Test
    void obtenerTodas_DeberiaRetornarListaVacia_CuandoNoHayFacultades() {
        when(facultadRepository.findAll()).thenReturn(new ArrayList<>());

        List<FacultadResponse> resultado = adminFacultadService.obtenerTodas();

        assertTrue(resultado.isEmpty());
        verify(facultadRepository, times(1)).findAll();
    }

    @Test
    void obtenerTodas_DeberiaRetornarListaDeFacultades_CuandoExisten() {
        List<FacultadRegional> facultades = List.of(facultad);
        when(facultadRepository.findAll()).thenReturn(facultades);
        when(matriculacionRepository.countByFacultad_Id(any())).thenReturn(10L);

        List<FacultadResponse> resultado = adminFacultadService.obtenerTodas();

        assertEquals(1, resultado.size());
        assertEquals("Rosario", resultado.getFirst().getCiudad());
        assertEquals(10L, resultado.getFirst().getCantidadMatriculados());
    }

    @Test
    void crearFacultad_DeberiaGuardarNuevaFacultad_CuandoNoExiste() {
        FacultadRequest request = new FacultadRequest();
        request.setCiudad("Córdoba");
        request.setProvincia("Córdoba");

        when(facultadRepository.existsByCiudadAndProvincia("Córdoba", "Córdoba")).thenReturn(false);

        adminFacultadService.crearFacultad(request);

        verify(facultadRepository, times(1)).save(any(FacultadRegional.class));
    }

    @Test
    void crearFacultad_DeberiaLanzarExcepcion_CuandoYaExisteLaFacultad() {
        FacultadRequest request = new FacultadRequest();
        request.setCiudad("Rosario");
        request.setProvincia("Santa Fe");

        when(facultadRepository.existsByCiudadAndProvincia("Rosario", "Santa Fe")).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            adminFacultadService.crearFacultad(request)
        );

        assertTrue(exception.getMessage().contains("Ya existe una facultad"));
        verify(facultadRepository, never()).save(any());
    }

    @Test
    void eliminarFacultad_DeberiaBorrarFacultad_CuandoNoTieneInscripciones() {
        when(matriculacionRepository.existsByFacultad_Id(facultadId)).thenReturn(false);
        when(facultadRepository.existsById(facultadId)).thenReturn(true);

        adminFacultadService.eliminarFacultad(facultadId);

        verify(facultadRepository, times(1)).deleteById(facultadId);
    }

    @Test
    void eliminarFacultad_DeberiaLanzarExcepcion_CuandoTieneInscripciones() {
        when(matriculacionRepository.existsByFacultad_Id(facultadId)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            adminFacultadService.eliminarFacultad(facultadId)
        );

        assertTrue(exception.getMessage().contains("tiene inscripciones activas"));
        verify(facultadRepository, never()).deleteById(any());
    }

    @Test
    void eliminarFacultad_DeberiaLanzarExcepcion_CuandoNoExisteLaFacultad() {
        when(matriculacionRepository.existsByFacultad_Id(facultadId)).thenReturn(false);
        when(facultadRepository.existsById(facultadId)).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, () -> 
            adminFacultadService.eliminarFacultad(facultadId)
        );

        assertTrue(exception.getMessage().contains("Facultad no encontrada"));
        verify(facultadRepository, never()).deleteById(any());
    }
}
