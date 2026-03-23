package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.EstadoUsuario;
import com.sysacad.backend.modelo.enums.Genero;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.modelo.enums.TipoDocumento;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UsuarioRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    @DisplayName("Debe encontrar usuario por legajo o mail")
    void findByLegajoOrMail_Success() {
        Usuario usuario = createUsuario("TEST001", "test@example.com");
        entityManager.persist(usuario);
        entityManager.flush();

        Optional<Usuario> result = usuarioRepository.findByLegajoOrMail("TEST001", "other@mail.com");
        assertTrue(result.isPresent());
        assertEquals("TEST001", result.get().getLegajo());

        result = usuarioRepository.findByLegajoOrMail("OTHER", "test@example.com");
        assertTrue(result.isPresent());
        assertEquals("test@example.com", result.get().getMail());
    }

    @Test
    @DisplayName("Debe listar usuarios por rol")
    void findByRol_Success() {
        entityManager.persist(createUsuario("ALU001", RolUsuario.ESTUDIANTE));
        entityManager.persist(createUsuario("ALU002", RolUsuario.ESTUDIANTE));
        entityManager.persist(createUsuario("PROF001", RolUsuario.PROFESOR));
        entityManager.flush();

        List<Usuario> alumnos = usuarioRepository.findByRol(RolUsuario.ESTUDIANTE);
        assertEquals(2, alumnos.size());

        List<Usuario> profesores = usuarioRepository.findByRol(RolUsuario.PROFESOR);
        assertEquals(1, profesores.size());
    }

    @Test
    @DisplayName("Debe verificar existencia por mail")
    void existsByMail_Success() {
        entityManager.persist(createUsuario("TEST", "exists@test.com"));
        entityManager.flush();

        assertTrue(usuarioRepository.existsByMail("exists@test.com"));
        assertFalse(usuarioRepository.existsByMail("not@exists.com"));
    }

    private Usuario createUsuario(String legajo, String mail) {
        return createUsuario(legajo, mail, RolUsuario.ESTUDIANTE);
    }

    private Usuario createUsuario(String legajo, RolUsuario rol) {
        return createUsuario(legajo, legajo + "@test.com", rol);
    }

    private Usuario createUsuario(String legajo, String mail, RolUsuario rol) {
        Usuario usuario = new Usuario();
        usuario.setLegajo(legajo);
        usuario.setPassword("password");
        usuario.setTipoDocumento(TipoDocumento.DNI);
        usuario.setDni(UUID.randomUUID().toString().substring(0, 8));
        usuario.setNombre("Nombre");
        usuario.setApellido("Apellido");
        usuario.setMail(mail);
        usuario.setFechaNacimiento(LocalDate.of(1990, 1, 1));
        usuario.setGenero(Genero.M);
        usuario.setFechaIngreso(LocalDate.now());
        usuario.setRol(rol);
        usuario.setEstado(EstadoUsuario.ACTIVO);
        return usuario;
    }
}
