package com.sysacad.backend.service;

import com.sysacad.backend.dto.inscripcion_cursado.InscripcionCursadoRequest;
import com.sysacad.backend.dto.inscripcion_cursado.InscripcionCursadoResponse;
import com.sysacad.backend.exception.BusinessLogicException;
import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.enums.EstadoCursada;
import com.sysacad.backend.repository.*;
import com.sysacad.backend.mapper.InscripcionCursadoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class InscripcionCursadoServiceTest {

    @Mock private InscripcionCursadoRepository inscripcionCursadoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private MateriaRepository materiaRepository;
    @Mock private ComisionRepository comisionRepository;
    @Mock private InscripcionCursadoMapper inscripcionCursadoMapper;
    @Mock private CorrelatividadService correlatividadService;
    @Mock private GrupoService grupoService;
    @Mock private GrupoRepository grupoRepository;
    @Mock private HorarioCursadoRepository horarioCursadoRepository;

    @InjectMocks
    private InscripcionCursadoService inscripcionCursadoService;

    private UUID alumnoId;
    private UUID materiaId;
    private UUID comisionId;
    private Usuario alumno;
    private Materia materia;
    private Comision comision;

    @BeforeEach
    void setUp() {
        alumnoId = UUID.randomUUID();
        materiaId = UUID.randomUUID();
        comisionId = UUID.randomUUID();

        alumno = new Usuario();
        alumno.setId(alumnoId);

        materia = new Materia();
        materia.setId(materiaId);
        materia.setNombre("Materia Test");

        comision = new Comision();
        comision.setId(comisionId);
        comision.setNombre("1K1");
        comision.setMaterias(new ArrayList<>(List.of(materia)));
    }

    @Test
    void inscribir_DeberiaLanzarExcepcion_CuandoComisionNoDictaMateria() {
        comision.getMaterias().clear();
        when(usuarioRepository.findById(alumnoId)).thenReturn(Optional.of(alumno));
        when(materiaRepository.findById(materiaId)).thenReturn(Optional.of(materia));
        when(comisionRepository.findById(comisionId)).thenReturn(Optional.of(comision));

        InscripcionCursadoRequest request = new InscripcionCursadoRequest();
        request.setIdUsuario(alumnoId);
        request.setIdMateria(materiaId);
        request.setIdComision(comisionId);

        assertThrows(BusinessLogicException.class, () -> inscripcionCursadoService.inscribir(request));
    }

    @Test
    void inscribir_DeberiaLanzarExcepcion_CuandoNoCumpleCorrelativas() {
        when(usuarioRepository.findById(alumnoId)).thenReturn(Optional.of(alumno));
        when(materiaRepository.findById(materiaId)).thenReturn(Optional.of(materia));
        when(comisionRepository.findById(comisionId)).thenReturn(Optional.of(comision));
        when(correlatividadService.puedeCursar(alumnoId, materiaId)).thenReturn(false);

        InscripcionCursadoRequest request = new InscripcionCursadoRequest();
        request.setIdUsuario(alumnoId);
        request.setIdMateria(materiaId);
        request.setIdComision(comisionId);

        assertThrows(BusinessLogicException.class, () -> inscripcionCursadoService.inscribir(request));
    }

    @Test
    void inscribir_DeberiaGuardar_CuandoTodoEsCorrecto() {
        when(usuarioRepository.findById(alumnoId)).thenReturn(Optional.of(alumno));
        when(materiaRepository.findById(materiaId)).thenReturn(Optional.of(materia));
        when(comisionRepository.findById(comisionId)).thenReturn(Optional.of(comision));
        when(correlatividadService.puedeCursar(alumnoId, materiaId)).thenReturn(true);
        when(inscripcionCursadoRepository.findByUsuarioIdAndEstado(any(), any())).thenReturn(new ArrayList<>());
        when(horarioCursadoRepository.findByIdIdComisionAndIdIdMateria(any(), any())).thenReturn(new ArrayList<>());
        when(inscripcionCursadoRepository.save(any())).thenReturn(new InscripcionCursado());

        InscripcionCursadoRequest request = new InscripcionCursadoRequest();
        request.setIdUsuario(alumnoId);
        request.setIdMateria(materiaId);
        request.setIdComision(comisionId);

        inscripcionCursadoService.inscribir(request);

        verify(inscripcionCursadoRepository).save(any(InscripcionCursado.class));
    }

    @Test
    void finalizarCursada_DeberiaLanzarExcepcion_CuandoNotaNoAlcanzaPromocion() {
        InscripcionCursado insc = new InscripcionCursado();
        when(inscripcionCursadoRepository.findById(any())).thenReturn(Optional.of(insc));

        assertThrows(BusinessLogicException.class, () -> 
            inscripcionCursadoService.finalizarCursada(UUID.randomUUID(), new BigDecimal("5.00"), EstadoCursada.PROMOCIONADO));
    }
}
