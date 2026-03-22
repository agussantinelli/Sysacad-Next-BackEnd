package com.sysacad.backend.service;

import com.sysacad.backend.dto.admin.AdminEstadisticasDTO;
import com.sysacad.backend.dto.admin.AdminInscripcionDTO;
import com.sysacad.backend.modelo.InscripcionCursado;
import com.sysacad.backend.modelo.InscripcionExamen;
import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.EstadoCursada;
import com.sysacad.backend.modelo.enums.EstadoExamen;
import com.sysacad.backend.repository.InscripcionCursadoRepository;
import com.sysacad.backend.repository.InscripcionExamenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

    @Mock private InscripcionCursadoRepository inscripcionCursadoRepository;
    @Mock private InscripcionExamenRepository inscripcionExamenRepository;

    @InjectMocks
    private AdminService adminService;

    private Usuario usuario;
    private Materia materia;

    @BeforeEach
    void setUp() {
        usuario = new Usuario();
        usuario.setId(UUID.randomUUID());
        usuario.setNombre("Test");
        usuario.setApellido("User");
        usuario.setLegajo("12345");

        materia = new Materia();
        materia.setNombre("Sistemas");
    }

    @Test
    void obtenerTodasInscripciones_DeberiaRetornarListaCombinadaYSordernada() {
        InscripcionCursado cursada = new InscripcionCursado();
        cursada.setUsuario(usuario);
        cursada.setMateria(materia);
        cursada.setFechaInscripcion(java.time.LocalDateTime.of(2025, 1, 1, 0, 0));
        cursada.setEstado(EstadoCursada.CURSANDO);

        InscripcionExamen examen = new InscripcionExamen();
        examen.setUsuario(usuario);
        com.sysacad.backend.modelo.DetalleMesaExamen detalle = new com.sysacad.backend.modelo.DetalleMesaExamen();
        detalle.setMateria(materia);
        examen.setDetalleMesaExamen(detalle);
        examen.setFechaInscripcion(java.time.LocalDateTime.of(2025, 2, 1, 0, 0));
        examen.setEstado(EstadoExamen.APROBADO);

        when(inscripcionCursadoRepository.findAll()).thenReturn(List.of(cursada));
        when(inscripcionExamenRepository.findAll()).thenReturn(List.of(examen));

        List<AdminInscripcionDTO> resultado = adminService.obtenerTodasInscripciones();

        assertEquals(2, resultado.size());
        assertEquals("EXAMEN", resultado.get(0).getTipo()); // Orden descendente por fecha
        assertEquals("CURSADA", resultado.get(1).getTipo());
    }

    @Test
    void obtenerEstadisticas_DeberiaLlamarARepositorios() {
        UUID facultadId = UUID.randomUUID();
        when(inscripcionCursadoRepository.countAlumnosAdmin(any(), any(), any(), any())).thenReturn(100L);
        when(inscripcionExamenRepository.countExamenesAdmin(any(), any(), any(), any())).thenReturn(50L);

        AdminEstadisticasDTO resultado = adminService.obtenerEstadisticas(2025, facultadId, null);

        assertNotNull(resultado);
        assertEquals(100L, resultado.getCantidadTotalAlumnos());
        assertEquals(50L, resultado.getCantidadTotalInscriptosExamen());
    }

    @Test
    void eliminarInscripcion_DeberiaLlamarAlRepositorioCorrecto() {
        UUID id = UUID.randomUUID();
        
        adminService.eliminarInscripcion(id, "CURSADA");
        verify(inscripcionCursadoRepository).deleteById(id);

        adminService.eliminarInscripcion(id, "EXAMEN");
        verify(inscripcionExamenRepository).deleteById(id);
    }

    @Test
    void eliminarInscripcion_DeberiaLanzarExcepcion_CuandoTipoEsInvalido() {
        assertThrows(IllegalArgumentException.class, () -> 
            adminService.eliminarInscripcion(UUID.randomUUID(), "INVALID")
        );
    }
}
