package com.sysacad.backend.service;

import com.sysacad.backend.modelo.InscripcionCursado;
import com.sysacad.backend.modelo.enums.EstadoCursada;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.repository.CarreraRepository;
import com.sysacad.backend.repository.InscripcionCursadoRepository;
import com.sysacad.backend.repository.MateriaRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EstadisticaServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private CarreraRepository carreraRepository;
    @Mock private MateriaRepository materiaRepository;
    @Mock private InscripcionCursadoRepository inscripcionCursadoRepository;

    @InjectMocks
    private EstadisticaService estadisticaService;

    @Test
    void contarAlumnosRegularesTotal_DeberiaFiltrarPorEstado() {
        InscripcionCursado regular = new InscripcionCursado();
        regular.setEstado(EstadoCursada.REGULAR);
        
        InscripcionCursado libre = new InscripcionCursado();
        libre.setEstado(EstadoCursada.LIBRE);

        when(inscripcionCursadoRepository.findAll()).thenReturn(List.of(regular, libre));

        long resultado = estadisticaService.contarAlumnosRegularesTotal();

        assertEquals(1, resultado);
    }

    @Test
    void obtenerResumenGeneral_DeberiaLlenarTodosLosCampos() {
        when(usuarioRepository.findByRol(RolUsuario.ESTUDIANTE)).thenReturn(List.of(new com.sysacad.backend.modelo.Usuario()));
        when(usuarioRepository.findByRol(RolUsuario.PROFESOR)).thenReturn(List.of(new com.sysacad.backend.modelo.Usuario()));
        when(carreraRepository.count()).thenReturn(5L);
        when(materiaRepository.count()).thenReturn(20L);
        when(inscripcionCursadoRepository.count()).thenReturn(100L);

        Map<String, Long> resultado = estadisticaService.obtenerResumenGeneral();

        assertEquals(1L, resultado.get("total_alumnos"));
        assertEquals(1L, resultado.get("total_profesores"));
        assertEquals(5L, resultado.get("total_carreras"));
        assertEquals(20L, resultado.get("total_materias"));
        assertEquals(100L, resultado.get("total_inscripciones_cursado"));
    }
}
