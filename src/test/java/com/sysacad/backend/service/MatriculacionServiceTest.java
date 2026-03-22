package com.sysacad.backend.service;

import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.repository.InscripcionCursadoRepository;
import com.sysacad.backend.repository.InscripcionExamenRepository;
import com.sysacad.backend.repository.MatriculacionRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MatriculacionServiceTest {

    @Mock private MatriculacionRepository matriculacionRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private InscripcionCursadoRepository inscripcionCursadoRepository;
    @Mock private InscripcionExamenRepository inscripcionExamenRepository;
    @Mock private EquivalenciaService equivalenciaService;
    @Mock private com.sysacad.backend.repository.MateriaRepository materiaRepository;

    @InjectMocks
    private MatriculacionService matriculacionService;

    private UUID usuarioId;
    private Usuario alumno;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        alumno = new Usuario();
        alumno.setId(usuarioId);
        alumno.setRol(RolUsuario.ESTUDIANTE);
    }

    @Test
    void matricularAlumno_DeberiaGuardarCorrectamente() {
        Matriculacion m = new Matriculacion();
        Matriculacion.MatriculacionId id = new Matriculacion.MatriculacionId(usuarioId, UUID.randomUUID(), UUID.randomUUID());
        m.setId(id);

        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(alumno));
        when(matriculacionRepository.save(any())).thenReturn(m);

        Matriculacion resultado = matriculacionService.matricularAlumno(m);

        assertNotNull(resultado);
        verify(matriculacionRepository).save(m);
        verify(equivalenciaService).procesarEquivalencias(any(), any());
    }

    @Test
    void obtenerHistorialMateria_DeberiaFiltrarPorMateria() {
        UUID materiaId = UUID.randomUUID();
        Materia materia = new Materia();
        materia.setId(materiaId);
        materia.setNombre("Test");

        when(usuarioRepository.findByLegajo("123")).thenReturn(Optional.of(alumno));
        when(materiaRepository.findById(materiaId)).thenReturn(Optional.of(materia));
        when(inscripcionCursadoRepository.findByUsuarioId(usuarioId)).thenReturn(new ArrayList<>());
        when(inscripcionExamenRepository.findByUsuarioId(usuarioId)).thenReturn(new ArrayList<>());

        var history = matriculacionService.obtenerHistorialMateria("123", materiaId);

        assertNotNull(history);
        assertEquals("Test", history.getMateriaNombre());
    }
}
