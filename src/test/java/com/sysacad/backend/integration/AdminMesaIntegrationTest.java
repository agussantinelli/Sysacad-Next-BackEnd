package com.sysacad.backend.integration;

import com.sysacad.backend.dto.mesa_examen.MesaExamenRequest;
import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.modelo.MesaExamen;
import com.sysacad.backend.modelo.enums.TipoMesa;
import com.sysacad.backend.repository.MateriaRepository;
import com.sysacad.backend.repository.MesaExamenRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(roles = "ADMIN")
class AdminMesaIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MesaExamenRepository mesaExamenRepository;

    @Autowired
    private MateriaRepository materiaRepository;

    @Test
    @DisplayName("Admin puede crear una mesa de examen")
    void crearMesa_Success() throws Exception {
        Materia materia = new Materia();
        materia.setNombre("Matemática I");
        materia.setCodigo("MAT1");
        materia = materiaRepository.save(materia);

        MesaExamenRequest request = new MesaExamenRequest();
        request.setIdMateria(materia.getId());
        request.setFecha(LocalDateTime.now().plusDays(7));
        request.setTipo(TipoMesa.FINAL);

        mockMvc.perform(post("/api/admin/mesas")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());

        assertTrue(mesaExamenRepository.findAll().stream()
                .anyMatch(m -> m.getMateria().getNombre().equals("Matemática I")));
    }
}
