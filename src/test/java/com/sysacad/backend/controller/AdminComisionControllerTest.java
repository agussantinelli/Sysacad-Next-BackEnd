package com.sysacad.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysacad.backend.dto.admin.AdminComisionDTO;
import com.sysacad.backend.dto.comision.ComisionRequest;
import com.sysacad.backend.service.AdminComisionService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminComisionController.class)
@ActiveProfiles("test")
class AdminComisionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AdminComisionService adminComisionService;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe listar todas las comisiones con detalle")
    void obtenerTodas_Success() throws Exception {
        when(adminComisionService.obtenerTodasConDetalle()).thenReturn(List.of(new AdminComisionDTO()));

        mockMvc.perform(get("/api/admin/comisiones"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe crear una comisión")
    void crearComision_Success() throws Exception {
        ComisionRequest request = new ComisionRequest();
        request.setNombre("1K1");
        request.setAnio(2024);
        request.setTurno("MAÑANA");

        mockMvc.perform(post("/api/admin/comisiones")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }
}
