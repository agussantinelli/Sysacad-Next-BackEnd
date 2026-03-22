package com.sysacad.backend.service;

import com.sysacad.backend.dto.admin.AdminInscripcionRequest;
import com.sysacad.backend.dto.inscripcion_cursado.InscripcionCursadoRequest;
import com.sysacad.backend.dto.inscripcion_examen.InscripcionExamenRequest;
import com.sysacad.backend.exception.BusinessLogicException;
import com.sysacad.backend.exception.ResourceNotFoundException;
import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.enums.EstadoCursada;
import com.sysacad.backend.repository.*;
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
public class AdminInscripcionServiceTest {

    @Mock private UsuarioRepository usuarioRepository;
    @Mock private MatriculacionRepository matriculacionRepository;
    @Mock private MateriaRepository materiaRepository;
    @Mock private InscripcionCursadoRepository inscripcionCursadoRepository;
    @Mock private InscripcionExamenRepository inscripcionExamenRepository;
    @Mock private MesaExamenRepository mesaExamenRepository;
    @Mock private DetalleMesaExamenRepository detalleMesaExamenRepository;
    @Mock private CorrelatividadService correlatividadService;
    @Mock private InscripcionCursadoService inscripcionCursadoService;
    @Mock private InscripcionExamenService inscripcionExamenService;
    @Mock private MesaExamenService mesaExamenService;

    @InjectMocks
    private AdminInscripcionService adminInscripcionService;

    private UUID alumnoId;
    private Usuario alumno;
    private Materia materia;

    @BeforeEach
    void setUp() {
        alumnoId = UUID.randomUUID();
        alumno = new Usuario();
        alumno.setId(alumnoId);
        
        materia = new Materia();
        materia.setId(UUID.randomUUID());
        materia.setNombre("Materia Test");
    }

    @Test
    void obtenerMateriasParaCursado_DeberiaFiltrarMaterias_CuandoAlumnoYaEstaInscripto() {
        when(usuarioRepository.findById(alumnoId)).thenReturn(Optional.of(alumno));
        
        InscripcionCursado inscripcion = new InscripcionCursado();
        inscripcion.setMateria(materia);
        inscripcion.setEstado(EstadoCursada.CURSANDO);
        when(inscripcionCursadoRepository.findByUsuarioId(alumnoId)).thenReturn(List.of(inscripcion));

        Matriculacion matriculacion = new Matriculacion();
        PlanDeEstudio plan = new PlanDeEstudio();
        PlanMateria pm = new PlanMateria();
        pm.setMateria(materia);
        plan.setPlanMaterias(Collections.singleton(pm));
        matriculacion.setPlan(plan);
        when(matriculacionRepository.findByUsuario_Id(alumnoId)).thenReturn(List.of(matriculacion));

        var resultado = adminInscripcionService.obtenerMateriasParaCursado(alumnoId);

        assertTrue(resultado.isEmpty());
    }

    @Test
    void inscribir_DeberiaLanzarExcepcion_CuandoAlumnoYaTieneCursadaActiva() {
        AdminInscripcionRequest request = new AdminInscripcionRequest();
        request.setTipo("CURSADA");
        request.setIdAlumno(alumnoId);
        request.setIdMateria(materia.getId());

        InscripcionCursado existente = new InscripcionCursado();
        existente.setEstado(EstadoCursada.REGULAR);
        when(inscripcionCursadoRepository.findByUsuarioIdAndMateriaId(alumnoId, materia.getId())).thenReturn(Optional.of(existente));

        assertThrows(BusinessLogicException.class, () -> adminInscripcionService.inscribir(request));
    }

    @Test
    void inscribir_DeberiaDelegarAInscripcionCursadoService_CuandoEsCursadaValida() {
        AdminInscripcionRequest request = new AdminInscripcionRequest();
        request.setTipo("CURSADA");
        request.setIdAlumno(alumnoId);
        request.setIdMateria(materia.getId());
        request.setIdReferencia(UUID.randomUUID());

        when(inscripcionCursadoRepository.findByUsuarioIdAndMateriaId(alumnoId, materia.getId())).thenReturn(Optional.empty());

        adminInscripcionService.inscribir(request);

        verify(inscripcionCursadoService, times(1)).inscribir(any(InscripcionCursadoRequest.class));
    }

    @Test
    void inscribir_DeberiaDelegarAInscripcionExamenService_CuandoEsExamenValido() {
        AdminInscripcionRequest request = new AdminInscripcionRequest();
        request.setTipo("EXAMEN");
        request.setIdAlumno(alumnoId);
        request.setIdReferencia(UUID.randomUUID());
        request.setNroDetalle(1);

        adminInscripcionService.inscribir(request);

        verify(inscripcionExamenService, times(1)).inscribirAlumno(any(InscripcionExamenRequest.class));
    }
}
