package com.sysacad.backend.service;

import com.sysacad.backend.dto.grupo.GrupoRequest;
import com.sysacad.backend.dto.grupo.MensajeGrupoRequest;
import com.sysacad.backend.exception.BusinessLogicException;
import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.enums.RolGrupo;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.repository.*;
import com.sysacad.backend.mapper.MensajeGrupoMapper;
import com.sysacad.backend.mapper.GrupoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GrupoServiceTest {

    @Mock private GrupoRepository grupoRepository;
    @Mock private MiembroGrupoRepository miembroGrupoRepository;
    @Mock private MensajeGrupoRepository mensajeGrupoRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private ComisionRepository comisionRepository;
    @Mock private MateriaRepository materiaRepository;
    @Mock private AsignacionMateriaRepository asignacionMateriaRepository;
    @Mock private InscripcionCursadoRepository inscripcionCursadoRepository;
    @Mock private MensajeGrupoMapper mensajeGrupoMapper;
    @Mock private GrupoMapper grupoMapper;
    @Mock private IEmailService emailService;

    @InjectMocks
    private GrupoService grupoService;

    private UUID grupoId;
    private UUID usuarioId;
    private Grupo grupo;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        grupoId = UUID.randomUUID();
        usuarioId = UUID.randomUUID();
        
        grupo = new Grupo();
        grupo.setId(grupoId);
        grupo.setNombre("Test Group");
        grupo.setEsVisible(true);

        usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setRol(RolUsuario.PROFESOR);
        usuario.setMail("test@test.com");
    }

    @Test
    void crearGrupo_DeberiaGuardarCorrectamente() {
        GrupoRequest request = new GrupoRequest();
        request.setNombre("Nuevo Grupo");
        request.setTipo("ESTUDIO");

        when(grupoRepository.save(any(Grupo.class))).thenReturn(grupo);
        when(grupoRepository.findById(grupoId)).thenReturn(Optional.of(grupo));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

        Grupo resultado = grupoService.crearGrupo(request, usuarioId);

        assertNotNull(resultado);
        verify(grupoRepository).save(any(Grupo.class));
        verify(miembroGrupoRepository).save(any(MiembroGrupo.class));
    }

    @Test
    void enviarMensaje_DeberiaLanzarExcepcion_CuandoNoEsAdmin() {
        MiembroGrupo miembro = new MiembroGrupo();
        miembro.setRol(RolGrupo.MIEMBRO);

        when(grupoRepository.findById(grupoId)).thenReturn(Optional.of(grupo));
        when(miembroGrupoRepository.findById(any())).thenReturn(Optional.of(miembro));

        MensajeGrupoRequest request = new MensajeGrupoRequest();
        request.setContenido("Hola");

        assertThrows(BusinessLogicException.class, () -> grupoService.enviarMensaje(grupoId, request, usuarioId));
    }

    @Test
    void enviarMensaje_DeberiaGuardarYNotificar_CuandoEsAdmin() {
        MiembroGrupo miembro = new MiembroGrupo();
        miembro.setRol(RolGrupo.ADMIN);
        miembro.setUsuario(usuario);

        when(grupoRepository.findById(grupoId)).thenReturn(Optional.of(grupo));
        when(miembroGrupoRepository.findById(any())).thenReturn(Optional.of(miembro));
        when(usuarioRepository.getReferenceById(usuarioId)).thenReturn(usuario);
        
        MensajeGrupo msg = new MensajeGrupo();
        msg.setContenido("Hola");
        msg.setFechaEnvio(LocalDateTime.now());
        when(mensajeGrupoRepository.save(any())).thenReturn(msg);

        MensajeGrupoRequest request = new MensajeGrupoRequest();
        request.setContenido("Hola");

        grupoService.enviarMensaje(grupoId, request, usuarioId);

        verify(mensajeGrupoRepository).save(any(MensajeGrupo.class));
    }
}
