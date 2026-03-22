package com.sysacad.backend.service;

import com.sysacad.backend.exception.BusinessLogicException;
import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.enums.RolCargo;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.repository.AsignacionMateriaRepository;
import com.sysacad.backend.repository.ComisionRepository;
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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ComisionServiceTest {

    @Mock private ComisionRepository comisionRepository;
    @Mock private AsignacionMateriaRepository asignacionMateriaRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private SecurityContext securityContext;
    @Mock private Authentication authentication;

    @InjectMocks
    private ComisionService comisionService;

    private UUID comisionId;
    private Comision comision;
    private Usuario profesor;

    @BeforeEach
    void setUp() {
        comisionId = UUID.randomUUID();
        comision = new Comision();
        comision.setId(comisionId);
        comision.setMaterias(new ArrayList<>());
        comision.setProfesores(new ArrayList<>());

        profesor = new Usuario();
        profesor.setId(UUID.randomUUID());
        profesor.setRol(RolUsuario.PROFESOR);

        SecurityContextHolder.setContext(securityContext);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void asignarProfesor_DeberiaLanzarExcepcion_CuandoUsuarioNoEsProfesor() {
        Usuario noProfe = new Usuario();
        noProfe.setRol(RolUsuario.ESTUDIANTE);

        assertThrows(BusinessLogicException.class, () -> comisionService.asignarProfesor(comisionId, noProfe));
    }

    @Test
    void asignarProfesor_DeberiaFuncionar_CuandoEsAdmin() {
        when(comisionRepository.findById(comisionId)).thenReturn(Optional.of(comision));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .when(authentication).getAuthorities();

        comisionService.asignarProfesor(comisionId, profesor);

        verify(comisionRepository).save(comision);
        assertTrue(comision.getProfesores().contains(profesor));
    }

    @Test
    void asignarProfesor_DeberiaFuncionar_CuandoEsJefeDeCatedraDeLaMateria() {
        Materia materia = new Materia();
        materia.setId(UUID.randomUUID());
        comision.getMaterias().add(materia);

        Usuario jefe = new Usuario();
        jefe.setId(UUID.randomUUID());
        jefe.setLegajo("JEFE123");

        when(comisionRepository.findById(comisionId)).thenReturn(Optional.of(comision));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(Collections.singletonList(new SimpleGrantedAuthority("ROLE_PROFESOR")))
                .when(authentication).getAuthorities();
        when(authentication.getName()).thenReturn("JEFE123");
        when(usuarioRepository.findByLegajo("JEFE123")).thenReturn(Optional.of(jefe));

        AsignacionMateria jefatura = new AsignacionMateria();
        jefatura.setMateria(materia);
        when(asignacionMateriaRepository.findByIdIdUsuarioAndCargo(jefe.getId(), RolCargo.JEFE_CATEDRA))
                .thenReturn(List.of(jefatura));

        comisionService.asignarProfesor(comisionId, profesor);

        verify(comisionRepository).save(comision);
    }

    @Test
    void asignarProfesor_DeberiaLanzarExcepcion_CuandoNoTienePermiso() {
        when(comisionRepository.findById(comisionId)).thenReturn(Optional.of(comision));
        when(securityContext.getAuthentication()).thenReturn(authentication);
        doReturn(Collections.emptyList()).when(authentication).getAuthorities();
        when(authentication.getName()).thenReturn("USER");
        when(usuarioRepository.findByLegajo("USER")).thenReturn(Optional.of(new Usuario()));

        assertThrows(BusinessLogicException.class, () -> comisionService.asignarProfesor(comisionId, profesor));
    }
}
