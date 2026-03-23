package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class MiembroGrupoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private MiembroGrupoRepository miembroGrupoRepository;

    private Usuario usuario;
    private Grupo grupo;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setLegajo("ALU001");
        usuario.setPassword("pass");
        usuario.setTipoDocumento(TipoDocumento.DNI);
        usuario.setDni("11223344");
        usuario.setNombre("Test");
        usuario.setApellido("User");
        usuario.setMail("test@test.com");
        usuario.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        usuario.setGenero(Genero.M);
        usuario.setFechaIngreso(LocalDate.now());
        usuario.setRol(RolUsuario.ESTUDIANTE);
        usuario.setEstado(EstadoUsuario.ACTIVO);
        entityManager.persist(usuario);

        grupo = new Grupo();
        grupo.setNombre("Test Group");
        grupo.setIdComision(UUID.randomUUID());
        grupo.setIdMateria(UUID.randomUUID());
        entityManager.persist(grupo);
    }

    @Test
    @DisplayName("Debe verificar si un usuario es miembro de un grupo")
    void existsByGrupo_IdAndUsuario_Id_Success() {
        MiembroGrupo miembro = new MiembroGrupo();
        miembro.setId(new MiembroGrupo.MiembroGrupoId(grupo.getId(), usuario.getId()));
        miembro.setGrupo(grupo);
        miembro.setUsuario(usuario);
        miembro.setFechaUnion(java.time.LocalDateTime.now());
        entityManager.persist(miembro);
        entityManager.flush();

        assertTrue(miembroGrupoRepository.existsByGrupo_IdAndUsuario_Id(grupo.getId(), usuario.getId()));
    }
}
