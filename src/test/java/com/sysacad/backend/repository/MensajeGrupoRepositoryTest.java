package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class MensajeGrupoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MensajeGrupoRepository mensajeGrupoRepository;

    private Grupo grupo;

    @BeforeEach
    void setUp() {
        grupo = new Grupo();
        grupo.setNombre("Chat Group");
        grupo.setIdComision(UUID.randomUUID());
        grupo.setIdMateria(UUID.randomUUID());
        entityManager.persist(grupo);
    }

    @Test
    @DisplayName("Debe contar mensajes nuevos después de una fecha")
    void countByGrupoIdAndFechaEnvioAfter_Success() {
        MensajeGrupo m = new MensajeGrupo();
        m.setGrupo(grupo);
        m.setContenido("Hola");
        m.setFechaEnvio(LocalDateTime.now().plusMinutes(5));
        entityManager.persist(m);
        entityManager.flush();

        long count = mensajeGrupoRepository.countByGrupoIdAndFechaEnvioAfter(grupo.getId(), LocalDateTime.now());
        assertEquals(1, count);
    }
}
