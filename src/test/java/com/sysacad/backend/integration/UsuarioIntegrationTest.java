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
        usuario.setApellido("Santi");
        usuario.setMail("alu001@test.com");
        usuario.setDni("12345678");
        usuario.setTipoDocumento(com.sysacad.backend.modelo.enums.TipoDocumento.DNI);
        usuario.setGenero(com.sysacad.backend.modelo.enums.Genero.M);
        usuario.setEstado(com.sysacad.backend.modelo.enums.EstadoUsuario.ACTIVO);
        usuario.setFechaNacimiento(java.time.LocalDate.of(2000, 1, 1));
        usuario.setFechaIngreso(java.time.LocalDate.now());
        usuario.setPassword("password");
        usuario.setRol(RolUsuario.ESTUDIANTE);
        usuarioRepository.save(usuario);

        mockMvc.perform(get("/api/usuarios/perfil"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Agus"));
    }
}
