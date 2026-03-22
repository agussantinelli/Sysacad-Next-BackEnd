package com.sysacad.backend.service;

import com.sysacad.backend.dto.detalle_mesa_examen.DetalleMesaExamenRequest;
import com.sysacad.backend.modelo.*;
import com.sysacad.backend.repository.*;
import com.sysacad.backend.mapper.MesaExamenMapper;
import com.sysacad.backend.mapper.DetalleMesaExamenMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MesaExamenServiceTest {

    @Mock private MesaExamenRepository mesaExamenRepository;
    @Mock private DetalleMesaExamenRepository detalleMesaExamenRepository;
    @Mock private MateriaRepository materiaRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private MesaExamenMapper mesaExamenMapper;
    @Mock private DetalleMesaExamenMapper detalleMesaExamenMapper;
    @Mock private InscripcionCursadoRepository inscripcionCursadoRepository;
    @Mock private InscripcionExamenRepository inscripcionExamenRepository;
    @Mock private CorrelatividadService correlatividadService;

    @InjectMocks
    private MesaExamenService mesaExamenService;

    private UUID mesaId;
    private UUID materiaId;
    private UUID presidenteId;
    private MesaExamen mesa;
    private Materia materia;
    private Usuario presidente;

    @BeforeEach
    void setUp() {
        mesaId = UUID.randomUUID();
        materiaId = UUID.randomUUID();
        presidenteId = UUID.randomUUID();

        mesa = new MesaExamen();
        mesa.setId(mesaId);
        mesa.setNombre("Febrero 2025");

        materia = new Materia();
        materia.setId(materiaId);
        materia.setNombre("Materia Test");

        presidente = new Usuario();
        presidente.setId(presidenteId);
    }

    @Test
    void addDetalle_DeberiaAutoIncrementarNroDetalle() {
        DetalleMesaExamenRequest request = new DetalleMesaExamenRequest();
        request.setIdMesaExamen(mesaId);
        request.setIdMateria(materiaId);
        request.setIdPresidente(presidenteId);
        request.setDiaExamen(LocalDate.now());
        request.setHoraExamen(LocalTime.of(9,0));

        when(mesaExamenRepository.findById(mesaId)).thenReturn(Optional.of(mesa));
        when(materiaRepository.findById(materiaId)).thenReturn(Optional.of(materia));
        when(usuarioRepository.findById(presidenteId)).thenReturn(Optional.of(presidente));
        
        DetalleMesaExamen existing = new DetalleMesaExamen();
        existing.setId(new DetalleMesaExamen.DetalleId(mesaId, 1));
        when(detalleMesaExamenRepository.findByMesaExamenId(mesaId)).thenReturn(List.of(existing));
        
        DetalleMesaExamen nuevo = new DetalleMesaExamen();
        when(detalleMesaExamenMapper.toEntity(any())).thenReturn(nuevo);
        when(detalleMesaExamenRepository.save(any())).thenReturn(nuevo);

        mesaExamenService.addDetalle(request);

        assertEquals(2, nuevo.getId().getNroDetalle());
        verify(detalleMesaExamenRepository).save(nuevo);
    }

    @Test
    void obtenerMesasDisponibles_DeberiaMarcarComoHabilitada_CuandoPuedeRendir() {
        UUID alumnoId = UUID.randomUUID();
        when(materiaRepository.findById(materiaId)).thenReturn(Optional.of(materia));
        
        DetalleMesaExamen detalle = new DetalleMesaExamen();
        detalle.setId(new DetalleMesaExamen.DetalleId(mesaId, 1));
        detalle.setMesaExamen(mesa);
        when(detalleMesaExamenRepository.findByMateriaIdWithDetails(materiaId)).thenReturn(List.of(detalle));
        
        when(inscripcionExamenRepository.existsByUsuarioIdAndDetalleMesaExamen_MateriaIdAndEstado(any(), any(), any())).thenReturn(false);
        when(inscripcionCursadoRepository.existsByUsuarioIdAndMateriaIdAndEstado(any(), any(), any())).thenReturn(false);
        when(inscripcionExamenRepository.findByUsuarioIdAndDetalleMesaExamenId(any(), any())).thenReturn(Optional.empty());
        when(correlatividadService.puedeRendir(alumnoId, materiaId)).thenReturn(true);

        var resultado = mesaExamenService.obtenerMesasDisponibles(materiaId, alumnoId);

        assertFalse(resultado.isEmpty());
        assertTrue(resultado.get(0).isHabilitada());
        assertEquals("Disponible", resultado.get(0).getMensaje());
    }
}
