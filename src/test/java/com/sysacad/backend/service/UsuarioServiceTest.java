package com.sysacad.backend.service;

import com.sysacad.backend.exception.BusinessLogicException;
import com.sysacad.backend.exception.ResourceNotFoundException;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.EstadoUsuario;
import com.sysacad.backend.repository.AsignacionMateriaRepository;
import com.sysacad.backend.repository.DetalleMesaExamenRepository;
import com.sysacad.backend.repository.InscripcionCursadoRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AsignacionMateriaRepository asignacionMateriaRepository;
    @Mock
    private InscripcionCursadoRepository inscripcionCursadoRepository;
    @Mock
    private DetalleMesaExamenRepository detalleMesaExamenRepository;
    @Mock
    private FileStorageService fileStorageService;
    @Mock
    private IEmailService emailService;

    @InjectMocks
    private UsuarioService usuarioService;

    private Usuario usuario;
    private UUID usuarioId;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setLegajo("55555");
        usuario.setMail("test@sysacad.com");
        usuario.setPassword("encodedPassword");
        usuario.setNombre("Test");
    }

    @Test
    void autenticar_DeberiaRetornarUsuario_CuandoCredencialesSonCorrectas() {
        when(usuarioRepository.findByLegajoOrMail("55555", "55555")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);

        Usuario resultado = usuarioService.autenticar("55555", "rawPassword");

        assertNotNull(resultado);
        assertEquals("55555", resultado.getLegajo());
    }

    @Test
    void autenticar_DeberiaLanzarExcepcion_CuandoUsuarioNoExiste() {
        when(usuarioRepository.findByLegajoOrMail("invalid", "invalid")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> 
            usuarioService.autenticar("invalid", "password")
        );
    }

    @Test
    void autenticar_DeberiaLanzarExcepcion_CuandoPasswordEsIncorrecta() {
        when(usuarioRepository.findByLegajoOrMail("55555", "55555")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrong", "encodedPassword")).thenReturn(false);

        assertThrows(BusinessLogicException.class, () -> 
            usuarioService.autenticar("55555", "wrong")
        );
    }

    @Test
    void registrarUsuario_DeberiaGuardarUsuario_CuandoDatosSonValidos() {
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setMail("new@sysacad.com");
        nuevoUsuario.setLegajo("66666");
        nuevoUsuario.setPassword("rawPassword");
        nuevoUsuario.setNombre("New User");

        when(usuarioRepository.existsByMail("new@sysacad.com")).thenReturn(false);
        when(usuarioRepository.existsByLegajo("66666")).thenReturn(false);
        when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(nuevoUsuario);

        Usuario resultado = usuarioService.registrarUsuario(nuevoUsuario);

        assertNotNull(resultado);
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
        verify(emailService, times(1)).sendHtmlEmail(anyString(), anyString(), anyString(), anyMap());
    }

    @Test
    void registrarUsuario_DeberiaLanzarExcepcion_CuandoEmailYaExiste() {
        when(usuarioRepository.existsByMail(anyString())).thenReturn(true);

        assertThrows(BusinessLogicException.class, () -> 
            usuarioService.registrarUsuario(usuario)
        );
    }

    @Test
    void resetPassword_DeberiaCambiarPassword_CuandoTokenEsValido() {
        usuario.setResetPasswordToken("validToken");
        usuario.setResetPasswordTokenExpiration(LocalDateTime.now().plusHours(1));

        when(usuarioRepository.findByResetPasswordToken("validToken")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("newPass")).thenReturn("newEncodedPass");

        usuarioService.resetPassword("validToken", "newPass");

        assertEquals("newEncodedPass", usuario.getPassword());
        assertNull(usuario.getResetPasswordToken());
        verify(usuarioRepository, times(1)).save(usuario);
    }

    @Test
    void cambiarEstado_DeberiaActualizarEstado_CuandoEsValido() {
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuario);

        Usuario resultado = usuarioService.cambiarEstado(usuarioId, EstadoUsuario.INACTIVO);

        assertEquals(EstadoUsuario.INACTIVO, resultado.getEstado());
    }

    @Test
    void cambiarEstado_DeberiaLanzarExcepcion_CuandoProfesorTieneMesasFuturas() {
        usuario.setEstado(EstadoUsuario.ACTIVO);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(detalleMesaExamenRepository.existsByProfesorAndFechaAfter(eq(usuarioId), any())).thenReturn(true);

        assertThrows(BusinessLogicException.class, () -> 
            usuarioService.cambiarEstado(usuarioId, EstadoUsuario.INACTIVO)
        );
    }
}
