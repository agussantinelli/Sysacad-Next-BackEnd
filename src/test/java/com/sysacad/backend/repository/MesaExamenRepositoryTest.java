package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.MesaExamen;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class MesaExamenRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MesaExamenRepository mesaExamenRepository;

    @Test
    @DisplayName("Debe detectar solapamiento de fechas de mesa de examen")
    void existsByFechaFinAfterAndFechaInicioBefore_Success() {
        MesaExamen m = new MesaExamen();
        m.setNombre("Finales Dic");
        m.setFechaInicio(LocalDate.of(2024, 12, 1));
        m.setFechaFin(LocalDate.of(2024, 12, 15));
        entityManager.persist(m);
        entityManager.flush();

        // Solapa porque empieza el 10 y termina el 20 (el 10 y 15 están dentro)
        boolean exists = mesaExamenRepository.existsByFechaFinAfterAndFechaInicioBefore(
                LocalDate.of(2024, 12, 10), LocalDate.of(2024, 12, 20));
        
        assertTrue(exists);
    }
}
