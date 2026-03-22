package com.sysacad.backend.service;

import com.sysacad.backend.dto.comision.CargaNotasCursadaDTO;
import com.sysacad.backend.dto.comision.NotaCursadaItemDTO;
import com.sysacad.backend.dto.materia.MateriaProfesorDTO;
import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.enums.*;
import com.sysacad.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProfesorServiceTest {

    @Mock private AsignacionMateriaRepository asignacionMateriaRepository;
    @Mock private ComisionRepository comisionRepository;
    @Mock private HorarioCursadoRepository horarioCursadoRepository;
    @Mock private PlanMateriaRepository planMateriaRepository;
    @Mock private InscripcionCursadoRepository inscripcionCursadoRepository;
    @Mock private DetalleMesaExamenRepository detalleMesaExamenRepository;
    @Mock private InscripcionExamenRepository inscripcionExamenRepository;
    @Mock private CalificacionCursadaRepository calificacionCursadaRepository;
    @Mock private InstanciaEvaluacionRepository instanciaEvaluacionRepository;
    @Mock private GrupoService grupoService;
    @Mock private IEmailService emailService;

    @InjectMocks
    private ProfesorService profesorService;

    private UUID profesorId;
    private UUID materiaId;
    private Usuario profesor;
    private Materia materia;

    @BeforeEach
    void setUp() {
        profesorId = UUID.randomUUID();
        materiaId = UUID.randomUUID();
        profesor = new Usuario();
        profesor.setId(profesorId);
        profesor.setNombre("Prof");
        profesor.setApellido("Test");

        materia = new Materia();
        materia.setId(materiaId);
        materia.setNombre("Materia Test");
    }

    @Test
    void obtenerMateriasAsignadas_DeberiaMapearCorrectamente() {
        AsignacionMateria asignacion = new AsignacionMateria();
        asignacion.setMateria(materia);
        asignacion.setCargo(RolCargo.JTP);
        asignacion.setProfesor(profesor);

        when(asignacionMateriaRepository.findByIdIdUsuario(profesorId)).thenReturn(List.of(asignacion));
        when(planMateriaRepository.findByIdIdMateria(materiaId)).thenReturn(new ArrayList<>());

        List<MateriaProfesorDTO> resultado = profesorService.obtenerMateriasAsignadas(profesorId);

        assertFalse(resultado.isEmpty());
        assertEquals("Materia Test", resultado.get(0).getNombreMateria());
        assertEquals(RolCargo.JTP, resultado.get(0).getCargo());
    }

    @Test
    void obtenerComisionesDeMateria_DeberiaFiltrarPorMateria() {
        AsignacionMateria asignacion = new AsignacionMateria();
        asignacion.setMateria(materia);
        asignacion.setCargo(RolCargo.PROFESOR_TITULAR);
        
        when(asignacionMateriaRepository.findByIdIdUsuario(profesorId)).thenReturn(List.of(asignacion));
        
        Comision comision = new Comision();
        comision.setId(UUID.randomUUID());
        comision.setNombre("1K1");
        comision.setSalon(new Salon());
        comision.setProfesores(List.of(profesor));
        
        when(comisionRepository.findByMateriasIdAndProfesoresId(materiaId, profesorId)).thenReturn(List.of(comision));

        var resultado = profesorService.obtenerComisionesDeMateria(profesorId, materiaId);

        assertFalse(resultado.isEmpty());
        assertEquals("1K1", resultado.get(0).getNombreComision());
    }

    @Test
    void cargarNotasCursada_DeberiaGuardarFinal_CuandoEsNotaFinal() {
        CargaNotasCursadaDTO dto = new CargaNotasCursadaDTO();
        dto.setEsNotaFinal(true);
        
        NotaCursadaItemDTO item = new NotaCursadaItemDTO();
        item.setIdInscripcion(UUID.randomUUID());
        item.setNota(new BigDecimal("8.00"));
        item.setEstado("REGULAR");
        dto.setNotas(List.of(item));

        UUID comisionId = UUID.randomUUID();
        Comision comision = new Comision();
        comision.setId(comisionId);
        comision.setProfesores(List.of(profesor));
        when(comisionRepository.findById(comisionId)).thenReturn(Optional.of(comision));

        InscripcionCursado inscripcion = new InscripcionCursado();
        inscripcion.setComision(comision);
        inscripcion.setMateria(materia);
        when(inscripcionCursadoRepository.findById(any())).thenReturn(Optional.of(inscripcion));

        profesorService.cargarNotasCursada(profesorId, comisionId, materiaId, dto);

        assertEquals(new BigDecimal("8.00"), inscripcion.getNotaFinal());
        assertEquals(EstadoCursada.REGULAR, inscripcion.getEstado());
        verify(inscripcionCursadoRepository).save(inscripcion);
    }
}
