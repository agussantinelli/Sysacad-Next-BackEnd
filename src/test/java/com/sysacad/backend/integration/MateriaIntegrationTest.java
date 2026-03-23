package com.sysacad.backend.integration;

import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.repository.MateriaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(roles = "ESTUDIANTE")
class MateriaIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MateriaRepository materiaRepository;

    @Test
    @DisplayName("Estudiante puede ver la lista de materias")
    void listarMaterias_Success() throws Exception {
        Materia materia = new Materia();
        materia.setNombre("Álgebra");
        materia.setTipoMateria(com.sysacad.backend.modelo.enums.TipoMateria.BASICA);
        materia.setDuracion(com.sysacad.backend.modelo.enums.DuracionMateria.CUATRIMESTRAL);
        materia.setHorasCursado((short) 64);
        materiaRepository.save(materia);

        mockMvc.perform(get("/api/materias"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
