package com.sysacad.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysacad.backend.dto.plan.PlanDeEstudioRequest;
import com.sysacad.backend.dto.plan.PlanDeEstudioResponse;
import com.sysacad.backend.mapper.CarreraMapper;
import com.sysacad.backend.mapper.PlanDeEstudioMapper;
import com.sysacad.backend.modelo.PlanDeEstudio;
import com.sysacad.backend.repository.FacultadRegionalRepository;
import com.sysacad.backend.service.CarreraService;
import com.sysacad.backend.service.PlanDeEstudioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CarreraController.class)
@ActiveProfiles("test")
class CarreraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CarreraService carreraService;

    @MockBean
    private PlanDeEstudioService planService;

    @MockBean
    private FacultadRegionalRepository facultadRepository;

    @MockBean
    private CarreraMapper carreraMapper;

    @MockBean
    private PlanDeEstudioMapper planDeEstudioMapper;

    @Test
    @WithMockUser
    @DisplayName("Debe listar carreras por facultad")
    void listarPorFacultad_Success() throws Exception {
        UUID idFacultad = UUID.randomUUID();
        when(carreraService.listarCarrerasPorFacultad(idFacultad)).thenReturn(List.of());

        mockMvc.perform(get("/api/carreras/facultad/{idFacultad}", idFacultad))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder crear un plan de estudio")
    void crearPlan_Admin_Success() throws Exception {
        PlanDeEstudioRequest request = new PlanDeEstudioRequest();
        request.setIdCarrera(UUID.randomUUID());
        request.setNroPlan(2023);
        
        PlanDeEstudio plan = new PlanDeEstudio();
        plan.setId(new PlanDeEstudio.PlanId(request.getIdCarrera(), request.getNroPlan()));

        when(planDeEstudioMapper.toEntity(any())).thenReturn(plan);
        when(planService.crearPlanDeEstudio(any())).thenReturn(plan);
        when(planDeEstudioMapper.toDTO(any())).thenReturn(new PlanDeEstudioResponse());

        mockMvc.perform(post("/api/carreras/planes")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
