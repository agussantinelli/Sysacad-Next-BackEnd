package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.Carrera;
import com.sysacad.backend.modelo.FacultadRegional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CarreraRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CarreraRepository carreraRepository;

    @Test
    @DisplayName("Debe encontrar carreras por ID de facultad")
    void findByFacultades_Id_Success() {
        FacultadRegional facultad = new FacultadRegional();
        facultad.setCiudad("Córdoba");
        facultad.setProvincia("Córdoba");
        entityManager.persist(facultad);

        Carrera carrera = new Carrera();
        carrera.setNombre("Sistemas");
        carrera.setAlias("ISI");
        carrera.setFacultades(java.util.Set.of(facultad));
        entityManager.persist(carrera);
        entityManager.flush();

        List<Carrera> result = carreraRepository.findByFacultades_Id(facultad.getId());
        assertFalse(result.isEmpty());
        assertEquals("Sistemas", result.get(0).getNombre());
    }

    @Test
    @DisplayName("Debe verificar existencia por alias")
    void existsByAlias_Success() {
        Carrera c = new Carrera();
        c.setNombre("Mecánica");
        c.setAlias("IEM");
        entityManager.persist(c);
        entityManager.flush();

        assertTrue(carreraRepository.existsByAlias("IEM"));
        assertFalse(carreraRepository.existsByAlias("LAR"));
    }
}
