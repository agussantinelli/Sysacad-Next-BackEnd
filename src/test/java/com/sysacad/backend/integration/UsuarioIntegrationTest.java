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
    @DisplayName("Debe obtener el perfil del usuario autenticado")
    void obtenerPerfil_Success() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setLegajo("AGUS001");
        usuario.setNombre("Agus");
        usuario.setMail("agus@test.com");
        usuario.setDni("98765432");
        usuario.setTipoDocumento(com.sysacad.backend.modelo.enums.TipoDocumento.DNI);
        usuario.setRol(RolUsuario.ESTUDIANTE);
        usuario.setApellido("Santi"); // Added missing field from original
        usuario.setGenero(com.sysacad.backend.modelo.enums.Genero.M); // Added missing field from original
        usuario.setEstado(com.sysacad.backend.modelo.enums.EstadoUsuario.ACTIVO); // Added missing field from original
        usuario.setFechaNacimiento(java.time.LocalDate.of(2000, 1, 1));
        usuario.setFechaIngreso(java.time.LocalDate.now());
        usuario.setPassword("password");
        usuarioRepository.save(usuario);

        mockMvc.perform(get("/api/usuarios/" + usuario.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Agus"));
    }
}
