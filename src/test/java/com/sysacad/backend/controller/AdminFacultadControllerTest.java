package com.sysacad.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysacad.backend.dto.admin.FacultadRequest;
import com.sysacad.backend.service.AdminFacultadService;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminFacultadController.class)
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.sysacad.backend.config.TestSecurityConfig.class)
class AdminFacultadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminFacultadService facultadService;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder obtener todas las facultades")
    void obtenerTodas_Success() throws Exception {
        when(facultadService.obtenerTodas()).thenReturn(List.of());
        mockMvc.perform(get("/api/admin/facultades"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder crear una facultad")
    void crearFacultad_Success() throws Exception {
        FacultadRequest request = new FacultadRequest();
        request.setCiudad("Córdoba");
        request.setProvincia("Córdoba");

        mockMvc.perform(post("/api/admin/facultades")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder eliminar una facultad")
    void eliminarFacultad_Success() throws Exception {
        UUID id = UUID.randomUUID();
        mockMvc.perform(delete("/api/admin/facultades/{id}", id)
                .with(csrf()))
                .andExpect(status().isNoContent());
    }
}
