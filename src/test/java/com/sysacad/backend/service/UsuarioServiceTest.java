package com.sysacad.backend.service;

import com.sysacad.backend.exception.BusinessLogicException;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.EstadoUsuario;
import com.sysacad.backend.repository.*;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AsignacionMateriaRepository asignacionMateriaRepository;
    @Mock private InscripcionCursadoRepository inscripcionCursadoRepository;
    @Mock private DetalleMesaExamenRepository detalleMesaExamenRepository;
    @Mock private FileStorageService fileStorageService;
    @Mock private IEmailService emailService;

    @InjectMocks
    private UsuarioService usuarioService;

    private UUID usuarioId;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setLegajo("12345");
        usuario.setMail("test@test.com");
        usuario.setPassword("encodedPassword");
    }

    @Test
    void autenticar_DeberiaFuncionar_CuandoPasswordCoincide() {
        when(usuarioRepository.findByLegajoOrMail(any(), any())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("rawPassword", "encodedPassword")).thenReturn(true);

        Usuario result = usuarioService.autenticar("12345", "rawPassword");

        assertNotNull(result);
        assertEquals(usuarioId, result.getId());
    }

    @Test
    void autenticar_DeberiaLanzarExcepcion_CuandoPasswordNoCoincide() {
        when(usuarioRepository.findByLegajoOrMail(any(), any())).thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        assertThrows(BusinessLogicException.class, () -> usuarioService.autenticar("12345", "wrongPassword"));
    }

    @Test
    void registrarUsuario_DeberiaCodificarPassword_YEnviarEmail() {
        when(usuarioRepository.existsByMail(any())).thenReturn(false);
        when(usuarioRepository.existsByLegajo(any())).thenReturn(false);
        when(usuarioRepository.existsByTipoDocumentoAndDni(any(), any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(usuarioRepository.save(any())).thenReturn(usuario);

        Usuario request = new Usuario();
        request.setPassword("rawPassword");
        request.setMail("test@test.com");
        
        Usuario result = usuarioService.registrarUsuario(request);

        assertNotNull(result);
        verify(passwordEncoder).encode("rawPassword");
        verify(emailService).sendHtmlEmail(eq("test@test.com"), any(), any(), any());
    }

    @Test
    void resetPassword_DeberiaFuncionar_CuandoTokenEsValido() {
        usuario.setResetPasswordToken("validToken");
        usuario.setResetPasswordTokenExpiration(LocalDateTime.now().plusHours(1));
        
        when(usuarioRepository.findByResetPasswordToken("validToken")).thenReturn(Optional.of(usuario));
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");

        usuarioService.resetPassword("validToken", "newPassword");

        assertEquals("newEncodedPassword", usuario.getPassword());
        assertNull(usuario.getResetPasswordToken());
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void cambiarEstado_DeberiaLanzarExcepcion_CuandoTieneMesasFuturas() {
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(detalleMesaExamenRepository.existsByProfesorAndFechaAfter(any(), any())).thenReturn(true);

        assertThrows(BusinessLogicException.class, () -> usuarioService.cambiarEstado(usuarioId, EstadoUsuario.INACTIVO));
    }
}
