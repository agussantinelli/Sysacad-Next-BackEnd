package com.sysacad.backend.service;

import com.sysacad.backend.dto.admin.AdminComisionDTO;
import com.sysacad.backend.dto.admin.AsignarMateriaComisionRequest;
import com.sysacad.backend.dto.comision.ComisionRequest;
import com.sysacad.backend.exception.BusinessLogicException;
import com.sysacad.backend.exception.ResourceNotFoundException;
import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.enums.DiaSemana;
import com.sysacad.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminComisionServiceTest {

    @Mock private ComisionRepository comisionRepository;
    @Mock private MateriaRepository materiaRepository;
    @Mock private SalonRepository salonRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private AsignacionMateriaRepository asignacionMateriaRepository;
    @Mock private InscripcionCursadoRepository inscripcionCursadoRepository;
    @Mock private CarreraRepository carreraRepository;
    @Mock private HorarioCursadoRepository horarioCursadoRepository;
    @Mock private PlanMateriaRepository planMateriaRepository;
    @Mock private GrupoService grupoService;

    @InjectMocks
    private AdminComisionService adminComisionService;

    private Comision comision;
    private UUID comisionId;
    private Carrera carrera;
    private UUID carreraId;

    @BeforeEach
    void setUp() {
        comisionId = UUID.randomUUID();
        carreraId = UUID.randomUUID();
        
        carrera = new Carrera();
        carrera.setId(carreraId);
        carrera.setNombre("Sistemas");

        comision = new Comision();
        comision.setId(comisionId);
        comision.setNombre("1K1");
        comision.setCarrera(carrera);
        comision.setMaterias(new HashSet<>());
        comision.setProfesores(new HashSet<>());
    }

    @Test
    void crearComision_DeberiaGuardarCorrectamente_CuandoDatosSonValidos() {
        ComisionRequest request = new ComisionRequest();
        request.setNombre("1k1");
        request.setTurno("MAÑANA");
        request.setAnio(2025);
        request.setNivel(1);
        request.setIdCarrera(carreraId);

        when(comisionRepository.existsByNombre("1K1")).thenReturn(false);
        when(carreraRepository.findById(carreraId)).thenReturn(Optional.of(carrera));

        adminComisionService.crearComision(request);

        verify(comisionRepository, times(1)).save(any(Comision.class));
        verify(grupoService, times(1)).crearGruposParaComision(any(Comision.class));
    }

    @Test
    void crearComision_DeberiaLanzarExcepcion_CuandoNombreYaExiste() {
        ComisionRequest request = new ComisionRequest();
        request.setNombre("1K1");
        request.setTurno("MAÑANA");
        request.setAnio(2025);
        request.setNivel(1);
        request.setIdCarrera(carreraId);

        when(comisionRepository.existsByNombre("1K1")).thenReturn(true);

        assertThrows(BusinessLogicException.class, () -> adminComisionService.crearComision(request));
    }

    @Test
    void asignarMateria_DeberiaLanzarExcepcion_CuandoHorasNoCoinciden() {
        Materia materia = new Materia();
        materia.setId(UUID.randomUUID());
        materia.setNombre("Sintaxis");
        materia.setHorasCursado(4);

        AsignarMateriaComisionRequest request = new AsignarMateriaComisionRequest();
        request.setIdMateria(materia.getId());
        
        AsignarMateriaComisionRequest.HorarioRequestDTO hDTO = new AsignarMateriaComisionRequest.HorarioRequestDTO();
        hDTO.setDia(com.sysacad.backend.dto.enums.DiaSemanaDTO.LUNES);
        hDTO.setHoraDesde(LocalTime.of(8, 0));
        hDTO.setHoraHasta(LocalTime.of(10, 0)); // Solo 2 horas, faltan 2
        request.setHorarios(List.of(hDTO));

        when(comisionRepository.findById(comisionId)).thenReturn(Optional.of(comision));
        when(materiaRepository.findById(materia.getId())).thenReturn(Optional.of(materia));
        
        PlanMateria.PlanMateriaId pmId = new PlanMateria.PlanMateriaId(carreraId, 2023, materia.getId());
        PlanMateria pm = new PlanMateria();
        pm.setId(pmId);
        when(planMateriaRepository.findByIdIdCarreraAndNivel(any(), anyShort())).thenReturn(List.of(pm));

        assertThrows(BusinessLogicException.class, () -> adminComisionService.asignarMateria(comisionId, request));
    }

    @Test
    void asignarMateria_DeberiaGuardar_CuandoTodoEsCorrecto() {
        Materia materia = new Materia();
        materia.setId(UUID.randomUUID());
        materia.setNombre("Sintaxis");
        materia.setHorasCursado(2);

        AsignarMateriaComisionRequest request = new AsignarMateriaComisionRequest();
        request.setIdMateria(materia.getId());
        request.setIdsProfesores(new ArrayList<>());
        
        AsignarMateriaComisionRequest.HorarioRequestDTO hDTO = new AsignarMateriaComisionRequest.HorarioRequestDTO();
        hDTO.setDia(com.sysacad.backend.dto.enums.DiaSemanaDTO.LUNES);
        hDTO.setHoraDesde(LocalTime.of(8, 0));
        hDTO.setHoraHasta(LocalTime.of(10, 0));
        request.setHorarios(List.of(hDTO));

        when(comisionRepository.findById(comisionId)).thenReturn(Optional.of(comision));
        when(materiaRepository.findById(materia.getId())).thenReturn(Optional.of(materia));
        
        PlanMateria.PlanMateriaId pmId = new PlanMateria.PlanMateriaId(carreraId, 2023, materia.getId());
        PlanMateria pm = new PlanMateria();
        pm.setId(pmId);
        pm.setNivel((short) 1);
        when(planMateriaRepository.findByIdIdCarreraAndNivel(any(), anyShort())).thenReturn(List.of(pm));

        adminComisionService.asignarMateria(comisionId, request);

        verify(comisionRepository).save(comision);
        verify(horarioCursadoRepository).save(any(HorarioCursado.class));
    }
}
