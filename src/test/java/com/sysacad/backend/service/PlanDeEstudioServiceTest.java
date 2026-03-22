package com.sysacad.backend.service;

import com.sysacad.backend.modelo.PlanDeEstudio;
import com.sysacad.backend.modelo.PlanMateria;
import com.sysacad.backend.repository.CarreraRepository;
import com.sysacad.backend.repository.PlanDeEstudioRepository;
import com.sysacad.backend.repository.PlanMateriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PlanDeEstudioServiceTest {

    @Mock private PlanDeEstudioRepository planRepository;
    @Mock private PlanMateriaRepository planMateriaRepository;
    @Mock private CarreraRepository carreraRepository;

    @InjectMocks
    private PlanDeEstudioService planDeEstudioService;

    private PlanDeEstudio plan;
    private PlanDeEstudio.PlanId planId;

    @BeforeEach
    void setUp() {
        UUID carreraId = UUID.randomUUID();
        planId = new PlanDeEstudio.PlanId(carreraId, 2025);
        plan = new PlanDeEstudio();
        plan.setId(planId);
        plan.setNombre("Plan 2025");
    }

    @Test
    void crearPlanDeEstudio_DeberiaGuardar_CuandoCarreraExiste() {
        when(carreraRepository.existsById(planId.getIdCarrera())).thenReturn(true);
        when(planRepository.save(any())).thenReturn(plan);

        PlanDeEstudio resultado = planDeEstudioService.crearPlanDeEstudio(plan);

        assertNotNull(resultado);
        verify(planRepository).save(plan);
    }

    @Test
    void crearPlanDeEstudio_DeberiaLanzarExcepcion_CuandoCarreraNoExiste() {
        when(carreraRepository.existsById(planId.getIdCarrera())).thenReturn(false);

        assertThrows(RuntimeException.class, () -> planDeEstudioService.crearPlanDeEstudio(plan));
    }

    @Test
    void agregarMateriaAPlan_DeberiaGuardar_CuandoPlanExiste() {
        PlanMateria pm = new PlanMateria();
        pm.setId(new PlanMateria.PlanMateriaId(planId.getIdCarrera(), planId.getNroPlan(), UUID.randomUUID()));
        
        when(planRepository.existsById(planId)).thenReturn(true);

        planDeEstudioService.agregarMateriaAPlan(pm);

        verify(planMateriaRepository).save(pm);
    }

    @Test
    void agregarMateriaAPlan_DeberiaLanzarExcepcion_CuandoPlanNoExiste() {
        PlanMateria pm = new PlanMateria();
        pm.setId(new PlanMateria.PlanMateriaId(planId.getIdCarrera(), planId.getNroPlan(), UUID.randomUUID()));
        
        when(planRepository.existsById(planId)).thenReturn(false);

        assertThrows(RuntimeException.class, () -> planDeEstudioService.agregarMateriaAPlan(pm));
    }
}
