package com.sysacad.backend.integration;

import com.sysacad.backend.dto.admin.MatriculacionRequest;
import com.sysacad.backend.modelo.Carrera;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.modelo.enums.TipoDocumento;
import com.sysacad.backend.modelo.enums.Genero;
import com.sysacad.backend.modelo.enums.EstadoUsuario;
import com.sysacad.backend.repository.CarreraRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(roles = "ADMIN")
class AdminMatriculacionIntegrationTest extends IntegrationTestBase {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CarreraRepository carreraRepository;

    @Test
    @DisplayName("Admin puede realizar una matriculación")
    void crearMatriculacion_Success() throws Exception {
        Usuario alumno = new Usuario();
        alumno.setLegajo("ALU999");
        alumno.setRol(RolUsuario.ESTUDIANTE);
        alumno.setNombre("Alumno");
        alumno.setApellido("Test");
        alumno.setMail("alu999@test.com");
        alumno.setDni("99999999");
        alumno.setTipoDocumento(TipoDocumento.DNI);
        alumno.setGenero(Genero.M);
        alumno.setEstado(EstadoUsuario.ACTIVO);
        alumno.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        alumno.setFechaIngreso(LocalDate.now());
        alumno.setPassword("123456");
        alumno = usuarioRepository.save(alumno);

        Carrera carrera = new Carrera();
        carrera.setNombre("Sistemas Test");
        carrera.setAlias("ISI");
        carrera = carreraRepository.save(carrera);

        MatriculacionRequest request = new MatriculacionRequest();
        request.setIdUsuario(alumno.getId());
        request.setIdCarrera(carrera.getId());
        request.setIdFacultad(UUID.randomUUID()); 
        request.setNroPlan(2023);

        mockMvc.perform(post("/api/admin/matriculacion")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
