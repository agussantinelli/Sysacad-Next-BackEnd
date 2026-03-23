package com.sysacad.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sysacad.backend.config.security.JwtService;
import com.sysacad.backend.dto.auth.AuthResponse;
import com.sysacad.backend.dto.auth.ForgotPasswordRequest;
import com.sysacad.backend.dto.auth.LoginRequest;
import com.sysacad.backend.dto.auth.ResetPasswordRequest;
import com.sysacad.backend.dto.usuario.UsuarioResponse;
import com.sysacad.backend.mapper.UsuarioMapper;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.service.MatriculacionService;
import com.sysacad.backend.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.sysacad.backend.config.TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    @Autowired
    private JwtService jwtService;

    @MockBean
    private MatriculacionService matriculacionService;

    @MockBean
    private UsuarioMapper usuarioMapper;



    @Test
    @DisplayName("Debe loguear exitosamente y retornar token")
    void login_Success() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setIdentificador("testuser");
        request.setPassword("password");
        request.setTipoIdentificador("LEGAJO");

        Usuario usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setRol(RolUsuario.ADMIN);

        UsuarioResponse usuarioResponse = new UsuarioResponse();
        usuarioResponse.setId(usuario.getId());

        when(usuarioService.autenticar(anyString(), anyString())).thenReturn(usuario);
        when(jwtService.generateToken(any())).thenReturn("mock-jwt-token");
        when(usuarioMapper.toDTO(usuario)).thenReturn(usuarioResponse);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock-jwt-token"))
                .andExpect(jsonPath("$.usuario.id").value(usuario.getId().toString()));
    }

    @Test
    @DisplayName("Debe procesar solicitud de olvido de contraseña")
    void forgotPassword_Success() throws Exception {
        ForgotPasswordRequest request = new ForgotPasswordRequest();
        request.setIdentificador("test@test.com");

        doNothing().when(usuarioService).solicitarRecuperacionPassword(anyString());

        mockMvc.perform(post("/api/auth/forgot-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").exists());

        verify(usuarioService).solicitarRecuperacionPassword("test@test.com");
    }

    @Test
    @DisplayName("Debe resetear la contraseña exitosamente")
    void resetPassword_Success() throws Exception {
        ResetPasswordRequest request = new ResetPasswordRequest();
        request.setToken("valid-token");
        request.setNewPassword("new-password");

        doNothing().when(usuarioService).resetPassword(anyString(), anyString());

        mockMvc.perform(post("/api/auth/reset-password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("La contraseña ha sido restablecida exitosamente."));

        verify(usuarioService).resetPassword("valid-token", "new-password");
    }
}
