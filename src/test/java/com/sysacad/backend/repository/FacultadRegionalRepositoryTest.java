package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.FacultadRegional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class FacultadRegionalRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private FacultadRegionalRepository facultadRegionalRepository;

    @Test
    @DisplayName("Debe verificar existencia por ciudad y provincia")
    void existsByCiudadAndProvincia_Success() {
        FacultadRegional f = new FacultadRegional();
        f.setNombre("FRC");
        f.setCiudad("Córdoba");
        f.setProvincia("Córdoba");
        entityManager.persist(f);
        entityManager.flush();

        assertTrue(facultadRegionalRepository.existsByCiudadAndProvincia("Córdoba", "Córdoba"));
        assertFalse(facultadRegionalRepository.existsByCiudadAndProvincia("Rosario", "Santa Fe"));
    }
}
