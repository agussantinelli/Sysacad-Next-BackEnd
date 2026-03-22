package com.sysacad.backend.service;

import com.sysacad.backend.dto.admin.MatriculacionRequest;
import com.sysacad.backend.exception.BusinessLogicException;
import com.sysacad.backend.exception.ResourceNotFoundException;
import com.sysacad.backend.mapper.CarreraMapper;
import com.sysacad.backend.mapper.FacultadMapper;
import com.sysacad.backend.mapper.UsuarioMapper;
import com.sysacad.backend.modelo.*;
import com.sysacad.backend.repository.*;
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
public class AdminMatriculacionServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private FacultadRegionalRepository facultadRegionalRepository;
    @Mock private CarreraRepository carreraRepository;
    @Mock private PlanDeEstudioRepository planDeEstudioRepository;
    @Mock private MatriculacionRepository matriculacionRepository;
    @Mock private UsuarioMapper usuarioMapper;
    @Mock private FacultadMapper facultadMapper;
    @Mock private CarreraMapper carreraMapper;

    @InjectMocks
    private AdminMatriculacionService adminMatriculacionService;

    private MatriculacionRequest request;
    private UUID usuarioId;
    private UUID facultadId;
    private UUID carreraId;
    private Integer nroPlan;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        facultadId = UUID.randomUUID();
        carreraId = UUID.randomUUID();
        nroPlan = 2023;

        request = new MatriculacionRequest();
        request.setIdUsuario(usuarioId);
        request.setIdFacultad(facultadId);
        request.setIdCarrera(carreraId);
        request.setNroPlan(nroPlan);
    }

    @Test
    void crearMatriculacion_DeberiaLanzarExcepcion_CuandoYaTieneMatriculacionActiva() {
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(new Usuario()));
        when(facultadRegionalRepository.findById(facultadId)).thenReturn(Optional.of(new FacultadRegional()));
        
        PlanDeEstudio plan = new PlanDeEstudio();
        Carrera carrera = new Carrera();
        carrera.setNombre("Sistemas");
        plan.setCarrera(carrera);
        when(planDeEstudioRepository.findById(any())).thenReturn(Optional.of(plan));

        Matriculacion existente = new Matriculacion();
        existente.setPlan(plan);
        existente.setId(new Matriculacion.MatriculacionId(usuarioId, facultadId, carreraId, nroPlan));
        
        when(matriculacionRepository.existsById(any())).thenReturn(false);
        when(matriculacionRepository.findByIdIdUsuarioAndEstado(usuarioId, "ACTIVO")).thenReturn(List.of(existente));

        assertThrows(BusinessLogicException.class, () -> adminMatriculacionService.crearMatriculacion(request));
    }

    @Test
    void crearMatriculacion_DeberiaGuardarCorrectamente_CuandoNoHayConflictos() {
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(new Usuario()));
        when(facultadRegionalRepository.findById(facultadId)).thenReturn(Optional.of(new FacultadRegional()));
        when(planDeEstudioRepository.findById(any())).thenReturn(Optional.of(new PlanDeEstudio()));
        
        when(matriculacionRepository.existsById(any())).thenReturn(false);
        when(matriculacionRepository.findByIdIdUsuarioAndEstado(usuarioId, "ACTIVO")).thenReturn(Collections.emptyList());

        adminMatriculacionService.crearMatriculacion(request);

        verify(matriculacionRepository, times(1)).save(any(Matriculacion.class));
    }

    @Test
    void crearMatriculacion_DeberiaLanzarExcepcion_CuandoYaEstaMatriculadoEnMismaCarrera() {
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(new Usuario()));
        when(facultadRegionalRepository.findById(facultadId)).thenReturn(Optional.of(new FacultadRegional()));
        when(planDeEstudioRepository.findById(any())).thenReturn(Optional.of(new PlanDeEstudio()));
        
        when(matriculacionRepository.existsById(any())).thenReturn(true);

        assertThrows(RuntimeException.class, () -> adminMatriculacionService.crearMatriculacion(request));
    }
}
