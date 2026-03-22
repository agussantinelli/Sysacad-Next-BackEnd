package com.sysacad.backend.service;

import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
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
    @Mock private com.sysacad.backend.repository.InscripcionCursadoRepository inscripcionCursadoRepository;
    @Mock private com.sysacad.backend.repository.InscripcionExamenRepository inscripcionExamenRepository;
    @Mock private EquivalenciaService equivalenciaService;
    @Mock private com.sysacad.backend.repository.MateriaRepository materiaRepository;

    @InjectMocks
    private MatriculacionService matriculacionService;

    private UUID alumnoId;
    private UUID facultadId;
    private UUID carreraId;
    private PlanDeEstudio.PlanId planId;
    private Usuario alumno;
    private FacultadRegional facultad;
    private Carrera carrera;
    private PlanDeEstudio plan;

    @BeforeEach
    void setUp() {
        alumnoId = UUID.randomUUID();
        facultadId = UUID.randomUUID();
        carreraId = UUID.randomUUID();
        planId = new PlanDeEstudio.PlanId(carreraId, 2025);

        alumno = new Usuario();
        alumno.setId(alumnoId);
        alumno.setRol(RolUsuario.ESTUDIANTE);

        facultad = new FacultadRegional();
        facultad.setId(facultadId);
        facultad.setCiudad("Rosario");
        facultad.setProvincia("Santa Fe");

        carrera = new Carrera();
        carrera.setId(carreraId);
        carrera.setNombre("Ingenieria en Sistemas");

        plan = new PlanDeEstudio();
        plan.setId(planId);
        plan.setNombre("Plan 2025");
        plan.setCarrera(carrera);
    }

    @Test
    void matricularAlumno_DeberiaGuardarCorrectamente() {
        Matriculacion matriculacion = new Matriculacion();
        matriculacion.setId(new Matriculacion.MatriculacionId(alumnoId, facultadId, carreraId, 2025));
        
        when(usuarioRepository.findById(alumnoId)).thenReturn(Optional.of(alumno));
        when(matriculacionRepository.save(any())).thenReturn(matriculacion);

        Matriculacion resultado = matriculacionService.matricularAlumno(matriculacion);

        assertNotNull(resultado);
        assertEquals("ACTIVO", resultado.getEstado());
        verify(matriculacionRepository).save(matriculacion);
    }

    @Test
    void obtenerHistorialMateria_DeberiaFiltrarPorMateria() {
        UUID materiaId = UUID.randomUUID();
        Materia materia = new Materia();
        materia.setId(materiaId);
        materia.setNombre("Test");

        when(usuarioRepository.findByLegajo("123")).thenReturn(Optional.of(alumno));
        when(materiaRepository.findById(materiaId)).thenReturn(Optional.of(materia));
        when(inscripcionCursadoRepository.findByUsuarioId(alumnoId)).thenReturn(new ArrayList<>());
        when(inscripcionExamenRepository.findByUsuarioId(alumnoId)).thenReturn(new ArrayList<>());

        var history = matriculacionService.obtenerHistorialMateria("123", materiaId);

        assertNotNull(history);
        assertEquals("Test", history.getNombreMateria());
    }

    @Test
    void obtenerCarrerasPorAlumno_DeberiaLlamarAlRepositorio() {
        when(matriculacionRepository.findByIdIdUsuario(alumnoId)).thenReturn(new ArrayList<>());

        List<Matriculacion> resultado = matriculacionService.obtenerCarrerasPorAlumno(alumnoId);

        assertNotNull(resultado);
        verify(matriculacionRepository).findByIdIdUsuario(alumnoId);
    }
}
