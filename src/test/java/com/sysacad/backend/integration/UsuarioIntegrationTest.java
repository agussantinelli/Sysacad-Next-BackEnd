package com.sysacad.backend.integration;

import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "ALU001", roles = "ESTUDIANTE")
class UsuarioIntegrationTest extends IntegrationTestBase {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Usuario puede obtener su perfil")
    void obtenerPerfil_Success() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setLegajo("ALU001");
        usuario.setNombre("Agus");
        usuario.setRol(RolUsuario.ESTUDIANTE);
        usuarioRepository.save(usuario);

        mockMvc.perform(get("/api/usuarios/perfil"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Agus"));
    }
}
