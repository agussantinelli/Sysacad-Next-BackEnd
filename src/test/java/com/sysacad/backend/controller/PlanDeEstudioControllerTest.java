package com.sysacad.backend.controller;

import com.sysacad.backend.mapper.PlanDeEstudioMapper;
import com.sysacad.backend.service.PlanDeEstudioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PlanDeEstudioController.class)
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.sysacad.backend.config.TestSecurityConfig.class)
class PlanDeEstudioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PlanDeEstudioService planService;

    @MockBean
    private PlanDeEstudioMapper planMapper;

    @Test
    @WithMockUser
    @DisplayName("Debe listar planes vigentes por carrera")
    void listarPlanesVigentes_Success() throws Exception {
        UUID idCarrera = UUID.randomUUID();
        when(planService.listarPlanesVigentes(idCarrera)).thenReturn(List.of());

        mockMvc.perform(get("/api/planes/vigentes/{idCarrera}", idCarrera))
                .andExpect(status().isOk());
    }
}
