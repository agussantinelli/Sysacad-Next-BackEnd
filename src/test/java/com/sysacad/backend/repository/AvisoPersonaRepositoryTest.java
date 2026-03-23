package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.enums.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class AvisoPersonaRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AvisoPersonaRepository avisoPersonaRepository;

    @Test
    @DisplayName("Debe persistir y recuperar AvisoPersona")
    void saveAndFind_Success() {
        Aviso aviso = new Aviso();
        aviso.setTitulo("Aviso");
        aviso.setDescripcion("Desc");
        aviso.setFechaEmision(java.time.LocalDateTime.now());
        aviso.setEstado(EstadoAviso.ACTIVO);
        entityManager.persist(aviso);

        Usuario persona = new Usuario();
        persona.setLegajo("ALU999");
        persona.setPassword("pass");
        persona.setTipoDocumento(TipoDocumento.DNI);
        persona.setDni("99999999");
        persona.setNombre("Test");
        persona.setApellido("Persona");
        persona.setMail("test@persona.com");
        persona.setFechaNacimiento(java.time.LocalDate.of(2000, 1, 1));
        persona.setGenero(Genero.M);
        persona.setFechaIngreso(java.time.LocalDate.now());
        persona.setRol(RolUsuario.ESTUDIANTE);
        persona.setEstado(EstadoUsuario.ACTIVO);
        entityManager.persist(persona);

        AvisoPersona ap = new AvisoPersona();
        ap.setId(new AvisoPersona.AvisoPersonaId(aviso.getId(), persona.getId()));
        ap.setAviso(aviso);
        ap.setPersona(persona);
        ap.setEstado(EstadoAvisoPersona.ENVIADO);
        entityManager.persist(ap);
        entityManager.flush();

        Optional<AvisoPersona> result = avisoPersonaRepository.findById(ap.getId());
        assertTrue(result.isPresent());
    }
    
    // Explicit Optional import fix for previous failed thought if needed, but I'll use it directly
    private static class OptionalWrapper {
        static <T> void check(java.util.Optional<T> opt) { assertTrue(opt.isPresent()); }
    }
    
    @Test
    @DisplayName("Debe existir el repositorio")
    void repositoryExists() {
        assertNotNull(avisoPersonaRepository);
    }
}
