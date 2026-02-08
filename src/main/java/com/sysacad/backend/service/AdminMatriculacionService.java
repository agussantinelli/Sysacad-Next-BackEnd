package com.sysacad.backend.service;

import com.sysacad.backend.dto.admin.MatriculacionRequest;
import com.sysacad.backend.dto.plan.PlanDeEstudioResponse;
import com.sysacad.backend.dto.usuario.UsuarioResponse;
import com.sysacad.backend.dto.facultad.FacultadResponse;
import com.sysacad.backend.dto.carrera.CarreraResponse;
import com.sysacad.backend.exception.ResourceNotFoundException;
import com.sysacad.backend.mapper.UsuarioMapper;
import com.sysacad.backend.mapper.FacultadMapper;
import com.sysacad.backend.mapper.CarreraMapper;
import com.sysacad.backend.modelo.*;
import com.sysacad.backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminMatriculacionService {

    private final UsuarioRepository usuarioRepository;
    private final FacultadRegionalRepository facultadRegionalRepository;
    private final CarreraRepository carreraRepository; 
    private final PlanDeEstudioRepository planDeEstudioRepository; 
    private final MatriculacionRepository matriculacionRepository; 

    private final UsuarioMapper usuarioMapper;
    private final FacultadMapper facultadMapper;
    private final CarreraMapper carreraMapper;

    @Autowired
    public AdminMatriculacionService(UsuarioRepository usuarioRepository, 
                                     FacultadRegionalRepository facultadRegionalRepository,
                                     CarreraRepository carreraRepository,
                                     PlanDeEstudioRepository planDeEstudioRepository,
                                     MatriculacionRepository matriculacionRepository,
                                     UsuarioMapper usuarioMapper,
                                     FacultadMapper facultadMapper,
                                     CarreraMapper carreraMapper) {
        this.usuarioRepository = usuarioRepository;
        this.facultadRegionalRepository = facultadRegionalRepository;
        this.carreraRepository = carreraRepository;
        this.planDeEstudioRepository = planDeEstudioRepository;
        this.matriculacionRepository = matriculacionRepository;
        this.usuarioMapper = usuarioMapper;
        this.facultadMapper = facultadMapper;
        this.carreraMapper = carreraMapper;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponse> buscarUsuariosPorLegajo(String legajo) {
        return usuarioMapper.toDTOs(usuarioRepository.findByLegajoContaining(legajo));
    }

    @Transactional(readOnly = true)
    public List<FacultadResponse> obtenerTodasFacultades() {
        return facultadRegionalRepository.findAll().stream()
                .map(facultadMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CarreraResponse> obtenerCarrerasPorFacultad(UUID facultadId) {
        FacultadRegional facultad = facultadRegionalRepository.findById(facultadId)
                .orElseThrow(() -> new ResourceNotFoundException("Facultad no encontrada"));
        
        return carreraRepository.findByFacultades_Id(facultadId).stream()
                .map(carreraMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<PlanDeEstudioResponse> obtenerPlanesPorCarrera(UUID carreraId) {
        return planDeEstudioRepository.findByIdIdCarrera(carreraId).stream()
                .map(plan -> new PlanDeEstudioResponse(plan))
                .collect(Collectors.toList());
    }

    @Transactional
    public void crearMatriculacion(MatriculacionRequest request) {
        Usuario usuario = usuarioRepository.findById(request.getIdUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        
        FacultadRegional facultad = facultadRegionalRepository.findById(request.getIdFacultad())
                .orElseThrow(() -> new ResourceNotFoundException("Facultad no encontrada"));
        
        
        PlanDeEstudio.PlanId planId = new PlanDeEstudio.PlanId(request.getIdCarrera(), request.getNroPlan());
        PlanDeEstudio plan = planDeEstudioRepository.findById(planId)
                .orElseThrow(() -> new ResourceNotFoundException("Plan de estudio no encontrado"));

        Matriculacion matriculacion = new Matriculacion();
        Matriculacion.MatriculacionId id = new Matriculacion.MatriculacionId(
                request.getIdUsuario(),
                request.getIdFacultad(),
                request.getIdCarrera(),
                request.getNroPlan()
        );

        if (matriculacionRepository.existsById(id)) {
            throw new RuntimeException("El alumno ya se encuentra matriculado en esta carrera.");
        }

        
        List<Matriculacion> matriculacionesActivas = matriculacionRepository
                .findByIdIdUsuarioAndEstado(request.getIdUsuario(), "ACTIVO");
        
        if (!matriculacionesActivas.isEmpty()) {
            Matriculacion existente = matriculacionesActivas.get(0);
            throw new com.sysacad.backend.exception.BusinessLogicException(
                "El alumno ya tiene una matriculación activa en " + 
                existente.getPlan().getCarrera().getNombre() + 
                " (Plan " + existente.getId().getNroPlan() + ")"
            );
        }

        matriculacion.setId(id);
        matriculacion.setUsuario(usuario);
        matriculacion.setFacultad(facultad);
        matriculacion.setPlan(plan);
        matriculacion.setFechaInscripcion(LocalDate.now());
        matriculacion.setEstado("ACTIVO");

        matriculacionRepository.save(matriculacion);
    }
}
