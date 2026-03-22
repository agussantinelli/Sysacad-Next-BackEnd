package com.sysacad.backend.service;

import com.sysacad.backend.exception.BusinessLogicException;
import com.sysacad.backend.modelo.AsignacionMateria;
import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolCargo;
import com.sysacad.backend.repository.AsignacionMateriaRepository;
import com.sysacad.backend.repository.MateriaRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MateriaServiceTest {

    @Mock private MateriaRepository materiaRepository;
    @Mock private AsignacionMateriaRepository asignacionMateriaRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private MateriaService materiaService;

    private UUID materiaId;
    private Materia materia;

    @BeforeEach
    void setUp() {
        materiaId = UUID.randomUUID();
        materia = new Materia();
        materia.setId(materiaId);
        materia.setNombre("Sistemas");

        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void crearMateria_DeberiaLanzarExcepcion_CuandoNombreYaExiste() {
        when(materiaRepository.findByNombre("Sistemas")).thenReturn(Optional.of(materia));

        assertThrows(BusinessLogicException.class, () -> materiaService.crearMateria(materia));
    }

    @Test
    void actualizarMateria_DeberiaFuncionar_CuandoEsAdmin() {
        when(materiaRepository.findById(materiaId)).thenReturn(Optional.of(materia));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .when(authentication).getAuthorities();
        when(materiaRepository.save(any())).thenReturn(materia);

        materia.setNombre("Sistemas II");
        Materia resultado = materiaService.actualizarMateria(materiaId, materia);

        assertNotNull(resultado);
        verify(materiaRepository).save(any());
    }

    @Test
    void actualizarMateria_DeberiaFuncionar_CuandoEsJefeDeCatedra() {
        when(materiaRepository.findById(materiaId)).thenReturn(Optional.of(materia));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_PROFESOR")))
                .when(authentication).getAuthorities();
        when(authentication.getName()).thenReturn("LEGAJO123");
        
        Usuario jefe = new Usuario();
        jefe.setId(UUID.randomUUID());
        when(usuarioRepository.findByLegajo("LEGAJO123")).thenReturn(Optional.of(jefe));
        
        AsignacionMateria asignacion = new AsignacionMateria();
        asignacion.setMateria(materia);
        when(asignacionMateriaRepository.findByIdIdUsuarioAndCargo(jefe.getId(), RolCargo.JEFE_CATEDRA))
                .thenReturn(List.of(asignacion));
        
        when(materiaRepository.save(any())).thenReturn(materia);

        materiaService.actualizarMateria(materiaId, materia);

        verify(materiaRepository).save(materia);
    }

    @Test
    void actualizarMateria_DeberiaLanzarExcepcion_CuandoNoTienePermiso() {
        when(materiaRepository.findById(materiaId)).thenReturn(Optional.of(materia));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(Collections.emptyList()).when(authentication).getAuthorities();
        when(authentication.getName()).thenReturn("USER");
        when(usuarioRepository.findByLegajo("USER")).thenReturn(Optional.of(new Usuario()));

        assertThrows(BusinessLogicException.class, () -> materiaService.actualizarMateria(materiaId, materia));
    }
}
