package com.sysacad.backend.service;

import com.sysacad.backend.dto.aviso.AvisoResponse;
import com.sysacad.backend.mapper.AvisoMapper;
import com.sysacad.backend.modelo.Aviso;
import com.sysacad.backend.modelo.AvisoPersona;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.EstadoAviso;
import com.sysacad.backend.modelo.enums.EstadoAvisoPersona;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.repository.AvisoPersonaRepository;
import com.sysacad.backend.repository.AvisoRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AvisoServiceTest {

    @Mock private AvisoRepository avisoRepository;
    @Mock private AvisoPersonaRepository avisoPersonaRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private AvisoMapper avisoMapper;
    @Mock private IEmailService emailService;

    @InjectMocks
    private AvisoService avisoService;

    private UUID avisoId;
    private UUID usuarioId;
    private Aviso aviso;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        avisoId = UUID.randomUUID();
        usuarioId = UUID.randomUUID();
        
        aviso = new Aviso();
        aviso.setId(avisoId);
        aviso.setTitulo("Test Aviso");
        aviso.setDescripcion("Contenido");

        usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setMail("estudiante@test.com");
        usuario.setNombre("Juan");
        usuario.setRol(RolUsuario.ESTUDIANTE);
    }

    @Test
    void publicarAviso_DeberiaGuardarYNotificarAEstudiantes() {
        when(avisoRepository.save(any())).thenReturn(aviso);
        when(usuarioRepository.findByRol(RolUsuario.ESTUDIANTE)).thenReturn(List.of(usuario));

        avisoService.publicarAviso(aviso);

        verify(avisoRepository).save(aviso);
        verify(emailService, times(1)).sendEmail(eq("estudiante@test.com"), anyString(), anyString());
        assertEquals(EstadoAviso.ACTIVO, aviso.getEstado());
        assertNotNull(aviso.getFechaEmision());
    }

    @Test
    void obtenerUltimosAvisosParaUsuario_DeberiaMarcarVistoComoTrue_ParaAdmin() {
        usuario.setRol(RolUsuario.ADMIN);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(avisoRepository.findAllByOrderByFechaEmisionDesc()).thenReturn(List.of(aviso));
        
        AvisoResponse response = new AvisoResponse();
        response.setId(avisoId);
        when(avisoMapper.toDTOs(anyList())).thenReturn(List.of(response));

        List<AvisoResponse> resultado = avisoService.obtenerUltimosAvisosParaUsuario(usuarioId);

        assertTrue(resultado.get(0).isVisto());
    }

    @Test
    void marcarComoLeido_DeberiaCrearAvisoPersona_CuandoNoExiste() {
        when(avisoRepository.findById(avisoId)).thenReturn(Optional.of(aviso));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(avisoPersonaRepository.findById(any())).thenReturn(Optional.empty());

        avisoService.marcarComoLeido(avisoId, usuarioId);

        verify(avisoPersonaRepository).save(any(AvisoPersona.class));
    }
}
