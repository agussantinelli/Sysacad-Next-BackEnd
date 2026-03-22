package com.sysacad.backend.service;

import com.sysacad.backend.modelo.Correlatividad;
import com.sysacad.backend.modelo.InscripcionCursado;
import com.sysacad.backend.modelo.InscripcionExamen;
import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.modelo.enums.EstadoCursada;
import com.sysacad.backend.modelo.enums.EstadoExamen;
import com.sysacad.backend.modelo.enums.TipoCorrelatividad;
import com.sysacad.backend.repository.InscripcionCursadoRepository;
import com.sysacad.backend.repository.InscripcionExamenRepository;
import com.sysacad.backend.repository.MateriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CorrelatividadServiceTest {

    @Mock private InscripcionCursadoRepository inscripcionCursadoRepository;
    @Mock private InscripcionExamenRepository inscripcionExamenRepository;
    @Mock private MateriaRepository materiaRepository;

    @InjectMocks
    private CorrelatividadService correlatividadService;

    private UUID alumnoId;
    private UUID materiaId;
    private Materia materiaObjetivo;

    @BeforeEach
    void setUp() {
        alumnoId = UUID.randomUUID();
        materiaId = UUID.randomUUID();
        materiaObjetivo = new Materia();
        materiaObjetivo.setId(materiaId);
        materiaObjetivo.setCorrelativas(new HashSet<>());
    }

    @Test
    void puedeCursar_DeberiaRetornarTrue_CuandoNoTieneCorrelativas() {
        when(materiaRepository.findById(materiaId)).thenReturn(Optional.of(materiaObjetivo));

        assertTrue(correlatividadService.puedeCursar(alumnoId, materiaId));
    }

    @Test
    void puedeCursar_DeberiaRetornarFalse_CuandoNoCumpleCorrelativaRegular() {
        Materia correlativa = new Materia();
        correlativa.setId(UUID.randomUUID());
        
        Correlatividad c = new Correlatividad();
        c.setCorrelativa(correlativa);
        c.setTipo(TipoCorrelatividad.REGULAR);
        materiaObjetivo.getCorrelativas().add(c);

        when(materiaRepository.findById(materiaId)).thenReturn(Optional.of(materiaObjetivo));
        when(inscripcionCursadoRepository.findByUsuarioId(alumnoId)).thenReturn(Collections.emptyList());
        when(inscripcionExamenRepository.findByUsuarioId(alumnoId)).thenReturn(Collections.emptyList());

        assertFalse(correlatividadService.puedeCursar(alumnoId, materiaId));
    }

    @Test
    void puedeRendir_DeberiaRetornarTrue_CuandoEsLibre() {
        materiaObjetivo.setRendirLibre(true);
        when(materiaRepository.findById(materiaId)).thenReturn(Optional.of(materiaObjetivo));

        assertTrue(correlatividadService.puedeRendir(alumnoId, materiaId));
    }

    @Test
    void puedeRendir_DeberiaRetornarFalse_CuandoNoEstaRegularizado() {
        materiaObjetivo.setRendirLibre(false);
        when(materiaRepository.findById(materiaId)).thenReturn(Optional.of(materiaObjetivo));
        when(inscripcionCursadoRepository.findByUsuarioIdAndMateriaId(alumnoId, materiaId)).thenReturn(Optional.empty());

        assertFalse(correlatividadService.puedeRendir(alumnoId, materiaId));
    }
}
