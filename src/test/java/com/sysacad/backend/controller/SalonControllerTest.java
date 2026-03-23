package com.sysacad.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysacad.backend.dto.salon.SalonRequest;
import com.sysacad.backend.dto.salon.SalonResponse;
import com.sysacad.backend.mapper.SalonMapper;
import com.sysacad.backend.modelo.FacultadRegional;
import com.sysacad.backend.modelo.Salon;
import com.sysacad.backend.service.FacultadService;
import com.sysacad.backend.service.SalonService;
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

@WebMvcTest(SalonController.class)
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.sysacad.backend.config.TestSecurityConfig.class)
class SalonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SalonService salonService;

    @MockBean
    private FacultadService facultadService;

    @MockBean
    private SalonMapper salonMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder crear un salón")
    void crearSalon_Admin_Success() throws Exception {
        SalonRequest request = new SalonRequest();
        request.setIdFacultad(UUID.randomUUID());

        when(salonMapper.toEntity(any())).thenReturn(new Salon());
        when(facultadService.buscarPorId(any())).thenReturn(new FacultadRegional());
        when(salonService.crearSalon(any())).thenReturn(new Salon());
        when(salonMapper.toDTO(any())).thenReturn(new SalonResponse());

        mockMvc.perform(post("/api/salones")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser
    @DisplayName("Usuario debe obtener salones por facultad")
    void listarPorFacultad_Success() throws Exception {
        UUID idFacultad = UUID.randomUUID();
        when(salonService.listarPorFacultad(idFacultad)).thenReturn(List.of());
        when(salonMapper.toDTOs(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/salones/facultad/{idFacultad}", idFacultad))
                .andExpect(status().isOk());
    }
}
