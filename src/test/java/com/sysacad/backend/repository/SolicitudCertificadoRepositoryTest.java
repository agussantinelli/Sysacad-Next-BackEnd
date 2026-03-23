package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.SolicitudCertificado;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class SolicitudCertificadoRepositoryTest {

    @Autowired
    private SolicitudCertificadoRepository solicitudCertificadoRepository;

    @Test
    @DisplayName("Debe existir el repositorio de certificados")
    void repositoryExists() {
        assertNotNull(solicitudCertificadoRepository);
    }
}
