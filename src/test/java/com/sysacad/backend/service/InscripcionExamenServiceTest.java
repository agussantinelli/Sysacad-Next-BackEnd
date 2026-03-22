package com.sysacad.backend.service;

import com.sysacad.backend.dto.inscripcion_examen.CargaNotaExamenRequest;
import com.sysacad.backend.dto.inscripcion_examen.InscripcionExamenRequest;
import com.sysacad.backend.dto.inscripcion_examen.InscripcionExamenResponse;
import com.sysacad.backend.exception.BusinessLogicException;
import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.enums.EstadoCursada;
import com.sysacad.backend.modelo.enums.EstadoExamen;
import com.sysacad.backend.repository.DetalleMesaExamenRepository;
import com.sysacad.backend.repository.InscripcionCursadoRepository;
import com.sysacad.backend.repository.InscripcionExamenRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import com.sysacad.backend.mapper.InscripcionExamenMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InscripcionExamenServiceTest {

    @Mock private InscripcionExamenRepository inscripcionExamenRepository;
    @Mock private DetalleMesaExamenRepository detalleMesaExamenRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private InscripcionExamenMapper inscripcionExamenMapper;
    @Mock private InscripcionCursadoRepository inscripcionCursadoRepository;
    @Mock private CorrelatividadService correlatividadService;

    @InjectMocks
    private InscripcionExamenService inscripcionExamenService;

    private UUID usuarioId;
    private Usuario usuario;
    private DetalleMesaExamen detalle;
    private Materia materia;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        usuario = new Usuario();
        usuario.setId(usuarioId);

        materia = new Materia();
        materia.setId(UUID.randomUUID());
        materia.setNombre("Materia Test");

        detalle = new DetalleMesaExamen();
        detalle.setMateria(materia);
        detalle.setDiaExamen(java.time.LocalDate.now().plusDays(1));
        detalle.setHoraExamen(LocalTime.of(9, 0));
    }

    @Test
    void inscribirAlumno_DeberiaLanzarExcepcion_CuandoYaTeniaAprobada() {
        when(detalleMesaExamenRepository.findById(any())).thenReturn(Optional.of(detalle));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(inscripcionExamenRepository.existsByUsuarioIdAndDetalleMesaExamen_MateriaIdAndEstado(any(), any(), any())).thenReturn(true);

        InscripcionExamenRequest request = new InscripcionExamenRequest();
        request.setIdUsuario(usuarioId);
        request.setIdDetalleMesa(UUID.randomUUID());
        request.setNroDetalle(1);

        assertThrows(BusinessLogicException.class, () -> inscripcionExamenService.inscribirAlumno(request));
    }

    @Test
    void inscribirAlumno_DeberiaLanzarExcepcion_CuandoHaySuperposicionHoraria() {
        when(detalleMesaExamenRepository.findById(any())).thenReturn(Optional.of(detalle));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(correlatividadService.puedeRendir(any(), any())).thenReturn(true);
        
        InscripcionExamen pendiente = new InscripcionExamen();
        pendiente.setDetalleMesaExamen(detalle); // Misma mesa -> Superposición
        when(inscripcionExamenRepository.findByUsuarioIdAndEstado(any(), any())).thenReturn(List.of(pendiente));

        InscripcionExamenRequest request = new InscripcionExamenRequest();
        request.setIdUsuario(usuarioId);
        request.setIdDetalleMesa(UUID.randomUUID());
        request.setNroDetalle(1);

        assertThrows(BusinessLogicException.class, () -> inscripcionExamenService.inscribirAlumno(request));
    }

    @Test
    void calificarExamen_DeberiaPerderRegularidad_CuandoLlegaAlCuartoAplazo() {
        InscripcionExamen insc = new InscripcionExamen();
        insc.setUsuario(usuario);
        insc.setDetalleMesaExamen(detalle);
        
        when(inscripcionExamenRepository.findById(any())).thenReturn(Optional.of(insc));
        
        InscripcionCursado cursada = new InscripcionCursado();
        cursada.setEstado(EstadoCursada.REGULAR);
        when(inscripcionCursadoRepository.findByUsuarioIdAndMateriaId(any(), any())).thenReturn(Optional.of(cursada));
        when(inscripcionExamenRepository.countByUsuarioIdAndDetalleMesaExamen_MateriaIdAndEstadoIn(any(), any(), any())).thenReturn(4L);

        CargaNotaExamenRequest request = new CargaNotaExamenRequest();
        request.setNota(new BigDecimal("2.00"));
        request.setEstado(EstadoExamen.DESAPROBADO);

        inscripcionExamenService.calificarExamen(UUID.randomUUID(), request);

        assertEquals(EstadoCursada.LIBRE, cursada.getEstado());
        verify(inscripcionCursadoRepository).save(cursada);
    }
}
