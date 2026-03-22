package com.sysacad.backend.service;

import com.sysacad.backend.dto.alumno.AlumnoCertificadoDTO;
import com.sysacad.backend.dto.examen.ProfesorCertificadoDTO;
import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.modelo.enums.TipoCertificado;
import com.sysacad.backend.repository.MatriculacionRepository;
import com.sysacad.backend.repository.SolicitudCertificadoRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import com.sysacad.backend.service.pdf.IPdfGenerator;
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
public class CertificadoServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private MatriculacionRepository matriculacionRepository;
    @Mock private IPdfGenerator pdfGenerator;
    @Mock private SolicitudCertificadoRepository solicitudCertificadoRepository;

    @InjectMocks
    private CertificadoService certificadoService;

    private UUID usuarioId;
    private Usuario usuario;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        usuario = new Usuario();
        usuario.setId(usuarioId);
        usuario.setNombre("Test");
        usuario.setApellido("User");
        usuario.setDni("12345678");
        usuario.setLegajo("12345");
        usuario.setRol(RolUsuario.ESTUDIANTE);
    }

    @Test
    void generarCertificadoRegular_DeberiaLanzarExcepcion_CuandoNoEstaMatriculado() {
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(matriculacionRepository.findByIdIdUsuario(usuarioId)).thenReturn(Collections.emptyList());

        assertThrows(RuntimeException.class, () -> certificadoService.generarCertificadoRegular(usuarioId));
    }

    @Test
    void generarCertificadoRegular_DeberiaGenerarPDF_CuandoEstaMatriculado() {
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        
        Matriculacion matricula = new Matriculacion();
        PlanDeEstudio plan = new PlanDeEstudio();
        Carrera carrera = new Carrera();
        carrera.setNombre("Ingeniería en Sistemas");
        carrera.setFacultades(new java.util.HashSet<>());
        plan.setCarrera(carrera);
        matricula.setPlan(plan);
        when(matriculacionRepository.findByIdIdUsuario(usuarioId)).thenReturn(List.of(matricula));
        
        byte[] pdfContent = new byte[]{1, 2, 3};
        when(pdfGenerator.generarCertificado(any(AlumnoCertificadoDTO.class))).thenReturn(pdfContent);

        byte[] resultado = certificadoService.generarCertificadoRegular(usuarioId);

        assertArrayEquals(pdfContent, resultado);
        verify(solicitudCertificadoRepository, times(1)).save(any(SolicitudCertificado.class));
    }

    @Test
    void generarCertificadoDocente_DeberiaGenerarPDF_ParaProfesor() {
        usuario.setRol(RolUsuario.PROFESOR);
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        
        byte[] pdfContent = new byte[]{4, 5, 6};
        when(pdfGenerator.generarCertificadoProfesor(any(ProfesorCertificadoDTO.class))).thenReturn(pdfContent);

        byte[] resultado = certificadoService.generarCertificadoDocente(usuarioId);

        assertArrayEquals(pdfContent, resultado);
        verify(solicitudCertificadoRepository, times(1)).save(any(SolicitudCertificado.class));
    }
}
