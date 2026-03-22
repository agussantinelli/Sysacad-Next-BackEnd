package com.sysacad.backend.service;

import com.sysacad.backend.dto.admin.DetalleMesaRequest;
import com.sysacad.backend.dto.admin.MesaExamenRequest;
import com.sysacad.backend.modelo.*;
import com.sysacad.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminMesaServiceTest {

    @Mock private MesaExamenRepository mesaRepository;
    @Mock private DetalleMesaExamenRepository detalleMesaRepository;
    @Mock private InscripcionExamenRepository inscripcionExamenRepository;
    @Mock private MateriaRepository materiaRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private AsignacionMateriaRepository asignacionMateriaRepository;

    @InjectMocks
    private AdminMesaService adminMesaService;

    private UUID mesaId;
    private MesaExamen mesa;

    @BeforeEach
    void setUp() {
        mesaId = UUID.randomUUID();
        mesa = new MesaExamen();
        mesa.setId(mesaId);
        mesa.setNombre("Turno Julio");
        mesa.setFechaInicio(LocalDate.of(2025, 7, 1));
        mesa.setFechaFin(LocalDate.of(2025, 7, 15));
    }

    @Test
    void crearMesaExamen_DeberiaLanzarExcepcion_CuandoSuperponeFechas() {
        MesaExamenRequest request = new MesaExamenRequest();
        request.setNombre("Turno Agosto");
        request.setFechaInicio(LocalDate.of(2025, 8, 1));
        request.setFechaFin(LocalDate.of(2025, 8, 10));

        when(mesaRepository.existsByFechaFinAfterAndFechaInicioBefore(any(), any())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> adminMesaService.crearMesaExamen(request));
    }

    @Test
    void agregarDetalleMesa_DeberiaLanzarExcepcion_CuandoFechaEstaFueraDeRango() {
        DetalleMesaRequest request = new DetalleMesaRequest();
        request.setIdMesaExamen(mesaId);
        request.setIdMateria(UUID.randomUUID());
        request.setIdPresidente(UUID.randomUUID());
        request.setDiaExamen(LocalDate.of(2025, 7, 20)); // Fuera del rango 1-15 Julio
        request.setHoraExamen(LocalTime.of(9, 0));

        when(mesaRepository.findById(mesaId)).thenReturn(Optional.of(mesa));
        when(materiaRepository.findById(any())).thenReturn(Optional.of(new Materia()));
        when(usuarioRepository.findById(any())).thenReturn(Optional.of(new Usuario()));
        when(detalleMesaRepository.findMaxNroDetalle(any())).thenReturn(Optional.of(0));

        assertThrows(RuntimeException.class, () -> adminMesaService.agregarDetalleMesa(request));
    }

    @Test
    void eliminarDetalleMesa_DeberiaLanzarExcepcion_CuandoHayAlumnosInscriptos() {
        Integer nroDetalle = 1;
        DetalleMesaExamen.DetalleId id = new DetalleMesaExamen.DetalleId(mesaId, nroDetalle);
        
        when(inscripcionExamenRepository.countByDetalleMesaExamenId(id)).thenReturn(5L);

        assertThrows(RuntimeException.class, () -> adminMesaService.eliminarDetalleMesa(mesaId, nroDetalle));
    }

    @Test
    void eliminarTurno_DeberiaEliminarTodo_CuandoNoHayInscriptos() {
        when(mesaRepository.findById(mesaId)).thenReturn(Optional.of(mesa));
        when(detalleMesaRepository.findByMesaExamenId(mesaId)).thenReturn(Collections.emptyList());

        adminMesaService.eliminarTurno(mesaId);

        verify(mesaRepository, times(1)).delete(mesa);
    }
}
