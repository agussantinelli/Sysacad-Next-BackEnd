package com.sysacad.backend.integration;

import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.repository.UsuarioRepository;
import com.sysacad.backend.repository.MatriculacionRepository;
import com.sysacad.backend.modelo.Matriculacion;
import com.sysacad.backend.modelo.Carrera;
import com.sysacad.backend.modelo.FacultadRegional;
import com.sysacad.backend.repository.CarreraRepository;
import com.sysacad.backend.repository.FacultadRegionalRepository;
import com.sysacad.backend.modelo.PlanDeEstudio;
import com.sysacad.backend.repository.PlanDeEstudioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import java.util.UUID;
import java.time.LocalDate;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "ALU001", roles = "ESTUDIANTE")
class CertificadoIntegrationTest extends IntegrationTestBase {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private MatriculacionRepository matriculacionRepository;

    @Autowired
    private CarreraRepository carreraRepository;

    @Autowired
    private FacultadRegionalRepository facultadRegionalRepository;

    @Autowired
    private PlanDeEstudioRepository planDeEstudioRepository;

    @Test
    @DisplayName("Estudiante puede solicitar certificado de alumno regular")
    void solicitarCertificado_Success() throws Exception {
        String uniqueSuffix = UUID.randomUUID().toString().substring(0, 4);
        Usuario alumno = new Usuario();
        alumno.setLegajo("ALU_" + uniqueSuffix);
        alumno.setNombre("Agus");
        alumno.setApellido("Santi");
        alumno.setMail("alu_" + uniqueSuffix + "@test.com");
        alumno.setDni("5555" + uniqueSuffix);
        alumno.setTipoDocumento(com.sysacad.backend.modelo.enums.TipoDocumento.DNI);
        alumno.setGenero(com.sysacad.backend.modelo.enums.Genero.M);
        alumno.setEstado(com.sysacad.backend.modelo.enums.EstadoUsuario.ACTIVO);
        alumno.setFechaNacimiento(java.time.LocalDate.of(2000, 1, 1));
        alumno.setFechaIngreso(java.time.LocalDate.now());
        alumno.setPassword("password");
        alumno.setRol(com.sysacad.backend.modelo.enums.RolUsuario.ESTUDIANTE);
        alumno = usuarioRepository.save(alumno);

        FacultadRegional facultad = new FacultadRegional();
        facultad.setCiudad("Tucumán_" + uniqueSuffix);
        facultad.setProvincia("Tucumán");
        facultad = facultadRegionalRepository.save(facultad);

        Carrera carrera = new Carrera();
        carrera.setNombre("Sistemas Cert " + uniqueSuffix);
        carrera.setAlias("ISC" + uniqueSuffix);
        carrera = carreraRepository.save(carrera);

        PlanDeEstudio plan = new PlanDeEstudio();
        plan.setId(new PlanDeEstudio.PlanId(carrera.getId(), 2023));
        plan.setNombre("Plan 2023 " + uniqueSuffix);
        plan.setFechaInicio(LocalDate.of(2023, 1, 1));
        plan.setEsVigente(true);
        plan = planDeEstudioRepository.save(plan);

        Matriculacion matriculacion = new Matriculacion();
        matriculacion.setId(new Matriculacion.MatriculacionId(alumno.getId(), facultad.getId(), carrera.getId(), 2023));
        matriculacion.setUsuario(alumno);
        matriculacion.setFacultad(facultad);
        matriculacion.setPlan(plan);
        matriculacion.setFechaInscripcion(java.time.LocalDate.now());
        matriculacion.setEstado("ACTIVA");
        matriculacionRepository.save(matriculacion);

        mockMvc.perform(get("/api/alumnos/certificado-regular"))
                .andExpect(status().isOk());
    }
}
