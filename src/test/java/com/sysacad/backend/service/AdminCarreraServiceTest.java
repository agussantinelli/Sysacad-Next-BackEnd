package com.sysacad.backend.service;

import com.sysacad.backend.dto.admin.CarreraAdminDTO;
import com.sysacad.backend.dto.admin.PlanDetalleDTO;
import com.sysacad.backend.dto.carrera.CarreraRequest;
import com.sysacad.backend.dto.carrera.CarreraResponse;
import com.sysacad.backend.exception.ResourceNotFoundException;
import com.sysacad.backend.mapper.CarreraMapper;
import com.sysacad.backend.modelo.Carrera;
import com.sysacad.backend.modelo.FacultadRegional;
import com.sysacad.backend.modelo.PlanDeEstudio;
import com.sysacad.backend.repository.CarreraRepository;
import com.sysacad.backend.repository.FacultadRegionalRepository;
import com.sysacad.backend.repository.MatriculacionRepository;
import com.sysacad.backend.repository.PlanDeEstudioRepository;
import com.sysacad.backend.repository.PlanMateriaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AdminCarreraServiceTest {

    @Mock
    private CarreraRepository carreraRepository;
    @Mock
    private MatriculacionRepository matriculacionRepository;
    @Mock
    private PlanDeEstudioRepository planDeEstudioRepository;
    @Mock
    private PlanMateriaRepository planMateriaRepository;
    @Mock
    private FacultadRegionalRepository facultadRegionalRepository;
    @Mock
    private CarreraMapper carreraMapper;

    @InjectMocks
    private AdminCarreraService adminCarreraService;

    private Carrera carrera;
    private UUID carreraId;

    @BeforeEach
    void setUp() {
        carreraId = UUID.randomUUID();
        carrera = new Carrera();
        carrera.setId(carreraId);
        carrera.setNombre("Ingeniería en Sistemas");
        carrera.setAlias("ISI");
        carrera.setFacultades(new HashSet<>());
    }

    @Test
    void obtenerTodasConEstadisticas_DeberiaRetornarDTOsConDatos() {
        when(carreraRepository.findAll()).thenReturn(List.of(carrera));
        when(matriculacionRepository.countByIdIdCarrera(carreraId)).thenReturn(100L);
        when(planDeEstudioRepository.findByIdIdCarrera(carreraId)).thenReturn(new ArrayList<>());

        List<CarreraAdminDTO> resultado = adminCarreraService.obtenerTodasConEstadisticas();

        assertFalse(resultado.isEmpty());
        assertEquals("ISI", resultado.getFirst().getAlias());
        assertEquals(100, resultado.getFirst().getCantidadMatriculados());
    }

    @Test
    void obtenerDetallePlan_DeberiaLanzarExcepcion_CuandoNoHayPlanVigente() {
        when(planDeEstudioRepository.findByIdIdCarreraAndEsVigenteTrue(carreraId)).thenReturn(new ArrayList<>());

        assertThrows(RuntimeException.class, () -> 
            adminCarreraService.obtenerDetallePlan(carreraId, 2025)
        );
    }

    @Test
    void asociarCarreraFacultad_DeberiaGuardarAsociacion_CuandoEsValida() {
        UUID facultadId = UUID.randomUUID();
        FacultadRegional facultad = new FacultadRegional();
        facultad.setId(facultadId);

        when(carreraRepository.findById(carreraId)).thenReturn(Optional.of(carrera));
        when(facultadRegionalRepository.findById(facultadId)).thenReturn(Optional.of(facultad));

        adminCarreraService.asociarCarreraFacultad(carreraId, facultadId);

        assertTrue(carrera.getFacultades().contains(facultad));
        verify(carreraRepository, times(1)).save(carrera);
    }

    @Test
    void registrarCarrera_DeberiaLanzarExcepcion_CuandoNombreYaExiste() {
        CarreraRequest request = new CarreraRequest();
        request.setNombre("Ingeniería en Sistemas");

        when(carreraRepository.existsByNombre("Ingeniería en Sistemas")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> 
            adminCarreraService.registrarCarrera(request)
        );
    }

    @Test
    void registrarCarrera_DeberiaGuardarCarrera_CuandoDatosSonValidos() {
        CarreraRequest request = new CarreraRequest();
        request.setNombre("Nueva Carrera");
        request.setAlias("NC");

        Carrera carreraNueva = new Carrera();
        carreraNueva.setAlias("NC");

        when(carreraRepository.existsByNombre("Nueva Carrera")).thenReturn(false);
        when(carreraRepository.existsByAlias("NC")).thenReturn(false);
        when(carreraMapper.toEntity(request)).thenReturn(carreraNueva);
        when(carreraRepository.save(any(Carrera.class))).thenReturn(carreraNueva);
        when(carreraMapper.toDTO(any())).thenReturn(new CarreraResponse());

        adminCarreraService.registrarCarrera(request);

        verify(carreraRepository, times(1)).save(any(Carrera.class));
    }
}
