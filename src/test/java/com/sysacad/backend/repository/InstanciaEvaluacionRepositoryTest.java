package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.InstanciaEvaluacion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class InstanciaEvaluacionRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private InstanciaEvaluacionRepository instanciaEvaluacionRepository;

    @Test
    @DisplayName("Debe encontrar instancia por nombre")
    void findByNombre_Success() {
        InstanciaEvaluacion inst = new InstanciaEvaluacion();
        inst.setNombre("Final");
        entityManager.persist(inst);
        entityManager.flush();

        Optional<InstanciaEvaluacion> result = instanciaEvaluacionRepository.findByNombre("Final");
        assertTrue(result.isPresent());
    }
}
