package com.sysacad.backend.controller;

import com.sysacad.backend.service.AdminGeneralService;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminGeneralController.class)
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.sysacad.backend.config.TestSecurityConfig.class)
class AdminGeneralControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AdminGeneralService adminGeneralService;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe obtener todas las carreras")
    void obtenerTodasLasCarreras_Success() throws Exception {
        when(adminGeneralService.obtenerTodasLasCarreras()).thenReturn(List.of());
        mockMvc.perform(get("/api/admin/general/carreras"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder buscar materias por carrera")
    void buscarMateriasPorCarrera_Success() throws Exception {
        UUID idCarrera = UUID.randomUUID();
        when(adminGeneralService.buscarMateriasPorCarreraYNombre(any(), anyString())).thenReturn(List.of());
        
        mockMvc.perform(get("/api/admin/general/materias/buscar")
                .param("idCarrera", idCarrera.toString())
                .param("query", "matematica"))
                .andExpect(status().isOk());
    }
}
