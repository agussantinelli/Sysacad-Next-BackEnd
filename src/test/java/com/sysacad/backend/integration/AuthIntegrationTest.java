package com.sysacad.backend.integration;

import com.sysacad.backend.dto.auth.LoginRequest;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.UUID;
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
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 4);
        String legajo = "ADMIN_" + uniqueSuffix;
        String dni = "1111" + uniqueSuffix;

        // Preparar usuario en DB con todos los campos requeridos
        Usuario usuario = new Usuario();
        usuario.setLegajo(legajo);
        usuario.setPassword(passwordEncoder.encode("password"));
        usuario.setRol(RolUsuario.ADMIN);
        usuario.setMail("admin_" + uniqueSuffix + "@test.com");
        usuario.setNombre("Admin");
        usuario.setApellido("Test");
        usuario.setDni(dni);
        usuario.setTipoDocumento(com.sysacad.backend.modelo.enums.TipoDocumento.DNI);
        usuario.setGenero(com.sysacad.backend.modelo.enums.Genero.M);
        usuario.setEstado(com.sysacad.backend.modelo.enums.EstadoUsuario.ACTIVO);
        usuario.setFechaNacimiento(java.time.LocalDate.of(1990, 1, 1));
        usuario.setFechaIngreso(java.time.LocalDate.now());
        usuarioRepository.save(usuario);

        LoginRequest request = new LoginRequest();
        request.setIdentificador(legajo);
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
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 4);
        String legajo = "USER_" + uniqueSuffix;
        String dni = "2222" + uniqueSuffix;

        Usuario usuario = new Usuario();
        usuario.setLegajo(legajo);
        usuario.setPassword(passwordEncoder.encode("password"));
        usuario.setRol(RolUsuario.ESTUDIANTE);
        usuario.setMail("user_" + uniqueSuffix + "@test.com");
        usuario.setNombre("User");
        usuario.setApellido("Test");
        usuario.setDni(dni);
        usuario.setTipoDocumento(com.sysacad.backend.modelo.enums.TipoDocumento.DNI);
        usuario.setGenero(com.sysacad.backend.modelo.enums.Genero.M);
        usuario.setEstado(com.sysacad.backend.modelo.enums.EstadoUsuario.ACTIVO);
        usuario.setFechaNacimiento(java.time.LocalDate.of(2000, 1, 1));
        usuario.setFechaIngreso(java.time.LocalDate.now());
        usuarioRepository.save(usuario);

        LoginRequest request = new LoginRequest();
        request.setIdentificador(legajo);
        request.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
