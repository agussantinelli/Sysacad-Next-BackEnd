package com.sysacad.backend.service;

import com.sysacad.backend.modelo.AsignacionMateria;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.repository.AsignacionMateriaRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AsignacionDocenteServiceTest {

    @Mock
    private AsignacionMateriaRepository asignacionRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private AsignacionDocenteService asignacionDocenteService;

    private AsignacionMateria asignacion;
    private Usuario profesor;
    private UUID usuarioId;
    private UUID materiaId;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        materiaId = UUID.randomUUID();
        
        profesor = new Usuario();
        profesor.setId(usuarioId);
        profesor.setRol(RolUsuario.PROFESOR);

        AsignacionMateria.AsignacionMateriaId id = new AsignacionMateria.AsignacionMateriaId(usuarioId, materiaId);
        asignacion = new AsignacionMateria();
        asignacion.setId(id);
    }

    @Test
    void asignarProfesorAMateria_DeberiaGuardarCorrectamente_CuandoUsuarioEsProfesor() {
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(profesor));
        when(asignacionRepository.save(any())).thenReturn(asignacion);

        AsignacionMateria resultado = asignacionDocenteService.asignarProfesorAMateria(asignacion);

        assertNotNull(resultado);
        verify(asignacionRepository, times(1)).save(asignacion);
    }

    @Test
    void asignarProfesorAMateria_DeberiaLanzarExcepcion_CuandoUsuarioNoEsProfesor() {
        Usuario alumno = new Usuario();
        alumno.setId(usuarioId);
        alumno.setRol(RolUsuario.ESTUDIANTE);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(alumno));

        assertThrows(RuntimeException.class, () -> asignacionDocenteService.asignarProfesorAMateria(asignacion));
    }

    @Test
    void listarMateriasDeProfesor_DeberiaLlamarAlRepositorio() {
        when(asignacionRepository.findByIdIdUsuario(usuarioId)).thenReturn(List.of(asignacion));

        List<AsignacionMateria> resultado = asignacionDocenteService.listarMateriasDeProfesor(usuarioId);

        assertEquals(1, resultado.size());
        verify(asignacionRepository).findByIdIdUsuario(usuarioId);
    }
}
