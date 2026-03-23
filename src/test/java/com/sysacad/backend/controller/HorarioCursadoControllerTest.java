package com.sysacad.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysacad.backend.dto.horario.HorarioRequest;
import com.sysacad.backend.dto.horario.HorarioResponse;
import com.sysacad.backend.mapper.HorarioMapper;
import com.sysacad.backend.modelo.Comision;
import com.sysacad.backend.modelo.HorarioCursado;
import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.service.ComisionService;
import com.sysacad.backend.service.HorarioCursadoService;
import com.sysacad.backend.service.MateriaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HorarioCursadoController.class)
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.sysacad.backend.config.TestSecurityConfig.class)
class HorarioCursadoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private HorarioCursadoService horarioService;

    @MockBean
    private ComisionService comisionService;

    @MockBean
    private MateriaService materiaService;

    @MockBean
    private HorarioMapper horarioMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    @DisplayName("Admin debe poder registrar un horario")
    void registrarHorario_Admin_Success() throws Exception {
        HorarioRequest request = new HorarioRequest();
        request.setIdComision(UUID.randomUUID());
        request.setIdMateria(UUID.randomUUID());
        request.setDia(com.sysacad.backend.modelo.enums.DiaSemana.LUNES);
        request.setHoraDesde(LocalTime.of(8, 0));
        request.setHoraHasta(LocalTime.of(12, 0));

        when(comisionService.buscarPorId(any())).thenReturn(Optional.of(new Comision()));
        when(materiaService.buscarPorId(any())).thenReturn(Optional.of(new Materia()));
        when(horarioMapper.toEntity(any())).thenReturn(new HorarioCursado());
        when(horarioService.registrarHorario(any())).thenReturn(new HorarioCursado());
        when(horarioMapper.toDTO(any())).thenReturn(new HorarioResponse());

        mockMvc.perform(post("/api/horarios")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ESTUDIANTE")
    @DisplayName("Debe obtener horarios por comisión")
    void obtenerPorComision_Success() throws Exception {
        UUID idComision = UUID.randomUUID();
        when(horarioService.obtenerPorComision(idComision)).thenReturn(List.of());
        when(horarioMapper.toDTOs(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/horarios/comision/{idComision}", idComision))
                .andExpect(status().isOk());
    }
}
