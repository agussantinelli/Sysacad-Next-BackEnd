package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.FacultadRegional;
import com.sysacad.backend.modelo.Salon;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class SalonRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private SalonRepository salonRepository;

    private FacultadRegional facultad;

    @BeforeEach
    void setUp() {
        facultad = new FacultadRegional();
        facultad.setNombre("FRC");
        facultad.setCiudad("Cba");
        facultad.setProvincia("Cba");
        entityManager.persist(facultad);
    }

    @Test
    @DisplayName("Debe encontrar salones por facultad")
    void findByFacultadId_Success() {
        Salon s = new Salon();
        s.setNombre("Aula 101");
        s.setCapacidad(50);
        s.setFacultad(facultad);
        entityManager.persist(s);
        entityManager.flush();

        List<Salon> result = salonRepository.findByFacultadId(facultad.getId());
        assertEquals(1, result.size());
        assertEquals("Aula 101", result.get(0).getNombre());
    }
}
