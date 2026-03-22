package com.sysacad.backend.service;

import com.sysacad.backend.modelo.Salon;
import com.sysacad.backend.repository.SalonRepository;
import org.junit.jupiter.api.BeforeEach;
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
public class SalonServiceTest {

    @Mock private SalonRepository salonRepository;

    @InjectMocks
    private SalonService salonService;

    @Test
    void crearSalon_DeberiaGuardarCorrectamente() {
        Salon salon = new Salon();
        salon.setNombre("Aula 101");

        when(salonRepository.save(any())).thenReturn(salon);

        Salon resultado = salonService.crearSalon(salon);

        assertNotNull(resultado);
        verify(salonRepository).save(salon);
    }

    @Test
    void listarPorFacultad_DeberiaLlamarAlRepositorio() {
        UUID facultadId = UUID.randomUUID();
        when(salonRepository.findByFacultadId(facultadId)).thenReturn(List.of(new Salon()));

        List<Salon> resultado = salonService.listarPorFacultad(facultadId);

        assertFalse(resultado.isEmpty());
        verify(salonRepository).findByFacultadId(facultadId);
    }
}
