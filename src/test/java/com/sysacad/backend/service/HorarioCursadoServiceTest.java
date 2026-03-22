package com.sysacad.backend.service;

import com.sysacad.backend.modelo.HorarioCursado;
import com.sysacad.backend.modelo.HorarioCursado.HorarioCursadoId;
import com.sysacad.backend.modelo.enums.DiaSemana;
import com.sysacad.backend.repository.HorarioCursadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HorarioCursadoServiceTest {

    @Mock private HorarioCursadoRepository horarioCursadoRepository;

    @InjectMocks
    private HorarioCursadoService horarioCursadoService;

    private HorarioCursado horario;
    private HorarioCursadoId id;

    @BeforeEach
    void setUp() {
        UUID comisionId = UUID.randomUUID();
        UUID materiaId = UUID.randomUUID();
        LocalTime inicio = LocalTime.of(8, 0);
        LocalTime fin = LocalTime.of(10, 0);
        
        id = new HorarioCursadoId(comisionId, materiaId, DiaSemana.LUNES, inicio);
        horario = new HorarioCursado();
        horario.setId(id);
        horario.setHoraHasta(fin);
    }

    @Test
    void registrarHorario_DeberiaGuardar_CuandoNoHayConflictos() {
        when(horarioCursadoRepository.encontrarSolapamientos(any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(horarioCursadoRepository.save(any())).thenReturn(horario);

        HorarioCursado resultado = horarioCursadoService.registrarHorario(horario);

        assertNotNull(resultado);
        verify(horarioCursadoRepository).save(horario);
    }

    @Test
    void registrarHorario_DeberiaLanzarExcepcion_CuandoHoraHastaEsIgualAInicio() {
        horario.setHoraHasta(id.getHoraDesde());

        assertThrows(RuntimeException.class, () -> horarioCursadoService.registrarHorario(horario));
    }

    @Test
    void registrarHorario_DeberiaLanzarExcepcion_CuandoHaySolapamiento() {
        when(horarioCursadoRepository.encontrarSolapamientos(any(), any(), any(), any()))
                .thenReturn(List.of(new HorarioCursado()));

        assertThrows(RuntimeException.class, () -> horarioCursadoService.registrarHorario(horario));
    }

    @Test
    void eliminarHorario_DeberiaLlamarAlRepositorio_CuandoExiste() {
        when(horarioCursadoRepository.existsById(id)).thenReturn(true);

        horarioCursadoService.eliminarHorario(id.getIdComision(), id.getIdMateria(), id.getDia(), id.getHoraDesde());

        verify(horarioCursadoRepository).deleteById(id);
    }
}
