package com.sysacad.backend.integration;

import com.sysacad.backend.dto.auth.LoginRequest;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthIntegrationTest extends IntegrationTestBase {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Login exitoso con credenciales válidas")
    void login_Success() throws Exception {
        // Preparar usuario en DB
        Usuario usuario = new Usuario();
        usuario.setLegajo("ADMIN123");
        usuario.setPassword(passwordEncoder.encode("password"));
        usuario.setRol(RolUsuario.ADMIN);
        usuario.setEmail("admin@test.com");
        usuario.setNombre("Admin");
        usuario.setApellido("Test");
        usuarioRepository.save(usuario);

        LoginRequest request = new LoginRequest();
        request.setLegajo("ADMIN123");
        request.setPassword("password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.rol").value("ADMIN"));
    }

    @Test
    @DisplayName("Login fallido con contraseña incorrecta")
    void login_InvalidPassword() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setLegajo("USER456");
        usuario.setPassword(passwordEncoder.encode("password"));
        usuario.setRol(RolUsuario.ESTUDIANTE);
        usuario.setEmail("user@test.com");
        usuarioRepository.save(usuario);

        LoginRequest request = new LoginRequest();
        request.setLegajo("USER456");
        request.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
