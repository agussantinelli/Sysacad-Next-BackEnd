package com.sysacad.backend.service;

import com.sysacad.backend.modelo.Carrera;
import com.sysacad.backend.repository.CarreraRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarreraServiceTest {

    @Mock
    private CarreraRepository carreraRepository;

    @InjectMocks
    private CarreraService carreraService;

    @Test
    void registrarCarrera_DeberiaGuardarYYRetornarCarrera() {
        Carrera carrera = new Carrera();
        carrera.setNombre("Ingeniería en Sistemas");
        when(carreraRepository.save(any(Carrera.class))).thenReturn(carrera);

        Carrera resultado = carreraService.registrarCarrera(carrera);

        assertNotNull(resultado);
        assertEquals("Ingeniería en Sistemas", resultado.getNombre());
        verify(carreraRepository, times(1)).save(carrera);
    }

    @Test
    void listarCarrerasPorFacultad_DeberiaRetornarListaDeCarreras() {
        UUID facultadId = UUID.randomUUID();
        Carrera carrera = new Carrera();
        when(carreraRepository.findByFacultades_Id(facultadId)).thenReturn(List.of(carrera));

        List<Carrera> resultado = carreraService.listarCarrerasPorFacultad(facultadId);

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(carreraRepository, times(1)).findByFacultades_Id(facultadId);
    }
}
