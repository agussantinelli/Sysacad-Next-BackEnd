package com.sysacad.backend.service;

import com.sysacad.backend.dto.admin.CarreraAdminDTO;
import com.sysacad.backend.dto.admin.PlanDetalleDTO;
import com.sysacad.backend.dto.materia.MateriaDetalleDTO;
import com.sysacad.backend.dto.plan.PlanDeEstudioResponse;
import com.sysacad.backend.dto.carrera.CarreraResponse;
import com.sysacad.backend.exception.ResourceNotFoundException;
import com.sysacad.backend.mapper.CarreraMapper;
import com.sysacad.backend.dto.admin.CarreraAdminDTO;
import com.sysacad.backend.dto.admin.PlanDetalleDTO;
import com.sysacad.backend.dto.carrera.CarreraRequest;
import com.sysacad.backend.dto.carrera.CarreraResponse;
import com.sysacad.backend.dto.materia.MateriaDetalleDTO;
import com.sysacad.backend.dto.plan.PlanDeEstudioResponse;
import com.sysacad.backend.exception.ResourceNotFoundException;
import com.sysacad.backend.mapper.CarreraMapper;
import com.sysacad.backend.modelo.*;
import com.sysacad.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminCarreraService {

    private final CarreraRepository carreraRepository;
    private final MatriculacionRepository matriculacionRepository;
    private final PlanDeEstudioRepository planDeEstudioRepository;
    private final PlanMateriaRepository planMateriaRepository;
    private final FacultadRegionalRepository facultadRegionalRepository;
    private final CarreraMapper carreraMapper;

    @Autowired
    public AdminCarreraService(CarreraRepository carreraRepository,
                               MatriculacionRepository matriculacionRepository,
                               PlanDeEstudioRepository planDeEstudioRepository,
                               PlanMateriaRepository planMateriaRepository,
                               FacultadRegionalRepository facultadRegionalRepository,
                               CarreraMapper carreraMapper) {
        this.carreraRepository = carreraRepository;
        this.matriculacionRepository = matriculacionRepository;
        this.planDeEstudioRepository = planDeEstudioRepository;
        this.planMateriaRepository = planMateriaRepository;
        this.facultadRegionalRepository = facultadRegionalRepository;
        this.carreraMapper = carreraMapper;
    }

    @Transactional(readOnly = true)
    public List<CarreraAdminDTO> obtenerTodasConEstadisticas() {
        return carreraRepository.findAll().stream()
                .map(carrera -> {
                    long matriculados = matriculacionRepository.countByIdIdCarrera(carrera.getId());
                    List<PlanDeEstudioResponse> planes = planDeEstudioRepository.findByIdIdCarrera(carrera.getId()).stream()
                            .map(PlanDeEstudioResponse::new)
                            .collect(Collectors.toList());
                    
                    return new CarreraAdminDTO(
                            carrera.getId(),
                            carrera.getNombre(),
                            carrera.getAlias(),
                            matriculados,
                            planes
                    );
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PlanDetalleDTO obtenerDetallePlan(UUID carreraId, Integer anio) {
        PlanDeEstudio.PlanId planId = new PlanDeEstudio.PlanId(carreraId, anio);
        PlanDeEstudio plan = planDeEstudioRepository.findById(planId)
                .orElseThrow(() -> new RuntimeException("Plan de estudio no encontrado"));

        return mapToPlanDetalleDTO(plan);
    }

    @Transactional(readOnly = true)
    public List<PlanDetalleDTO> obtenerPlanesDetallados(UUID carreraId) {
        return planDeEstudioRepository.findByIdIdCarrera(carreraId).stream()
                .map(this::mapToPlanDetalleDTO)
                .collect(Collectors.toList());
    }

    private PlanDetalleDTO mapToPlanDetalleDTO(PlanDeEstudio plan) {
        UUID carreraId = plan.getId().getIdCarrera();
        Integer anio = plan.getId().getNroPlan();
        PlanDeEstudio.PlanId planId = plan.getId();

        List<PlanMateria> planMaterias = planMateriaRepository.findByIdIdCarreraAndIdNroPlan(carreraId, anio);

        List<MateriaDetalleDTO> materiasDTO = planMaterias.stream()
                .map(pm -> {
                    Materia m = pm.getMateria();
                    List<String> correlativas = m.getCorrelativas().stream()
                            .filter(c -> c.getPlan().getId().equals(planId))
                            .map(c -> c.getCorrelativa().getNombre() + " (" + c.getTipo() + ")")
                            .collect(Collectors.toList());

                    return new MateriaDetalleDTO(
                            m.getId(),
                            m.getNombre(),
                            pm.getCodigoMateria(),
                            (int) pm.getNivel(),
                            m.getHorasCursado(),
                            m.getTipoMateria().name(),
                            correlativas
                    );
                })
                .collect(Collectors.toList());

        return new PlanDetalleDTO(
                carreraId,
                anio,
                plan.getNombre(),
                plan.getEsVigente(),
                materiasDTO
        );
    }
    @Transactional(readOnly = true)
    public List<CarreraResponse> obtenerTodas() {
        return carreraMapper.toDTOs(carreraRepository.findAll());
    }

    @Transactional
    public void asociarCarreraFacultad(UUID idCarrera, UUID idFacultad) {
        Carrera carrera = carreraRepository.findById(idCarrera)
                .orElseThrow(() -> new ResourceNotFoundException("Carrera no encontrada"));

        FacultadRegional facultad = facultadRegionalRepository.findById(idFacultad)
                .orElseThrow(() -> new ResourceNotFoundException("Facultad no encontrada"));

        carrera.getFacultades().add(facultad);
        carreraRepository.save(carrera);
    }

    @Transactional
    public CarreraResponse registrarCarrera(CarreraRequest request) {
        if (carreraRepository.existsByNombre(request.getNombre())) {
            throw new RuntimeException("Ya existe una carrera con el nombre: " + request.getNombre());
        }
        
        String aliasUpper = request.getAlias().toUpperCase();
        if (carreraRepository.existsByAlias(aliasUpper)) {
            throw new RuntimeException("Ya existe una carrera con el alias: " + aliasUpper);
        }

        Carrera carrera = carreraMapper.toEntity(request);
        carrera.setAlias(aliasUpper);

        if (request.getIdFacultad() != null) {
            FacultadRegional facultad = facultadRegionalRepository.findById(request.getIdFacultad())
                    .orElseThrow(() -> new ResourceNotFoundException("Facultad no encontrada"));
            java.util.Set<FacultadRegional> facultades = new java.util.HashSet<>();
            facultades.add(facultad);
            carrera.setFacultades(facultades);
        }

        Carrera guardada = carreraRepository.save(carrera);
        return carreraMapper.toDTO(guardada);
    }
}
