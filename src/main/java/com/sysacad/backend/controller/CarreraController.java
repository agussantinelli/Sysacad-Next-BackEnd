package com.sysacad.backend.controller;

import com.sysacad.backend.dto.carrera.CarreraRequest;
import com.sysacad.backend.dto.carrera.CarreraResponse;
import com.sysacad.backend.dto.plan.PlanDeEstudioRequest;
import com.sysacad.backend.dto.plan.PlanDeEstudioResponse;
import com.sysacad.backend.modelo.Carrera;
import com.sysacad.backend.modelo.PlanDeEstudio;
import com.sysacad.backend.modelo.PlanMateria;
import com.sysacad.backend.service.CarreraService;
import com.sysacad.backend.service.PlanDeEstudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/carreras")
@CrossOrigin(origins = "http://localhost:4200")
public class CarreraController {

    private final CarreraService carreraService;
    private final PlanDeEstudioService planService;
    private final com.sysacad.backend.repository.FacultadRegionalRepository facultadRepository;
    private final com.sysacad.backend.mapper.CarreraMapper carreraMapper;
    private final com.sysacad.backend.mapper.PlanDeEstudioMapper planDeEstudioMapper;

    @Autowired
    public CarreraController(CarreraService carreraService, PlanDeEstudioService planService,
                             com.sysacad.backend.repository.FacultadRegionalRepository facultadRepository,
                             com.sysacad.backend.mapper.CarreraMapper carreraMapper,
                             com.sysacad.backend.mapper.PlanDeEstudioMapper planDeEstudioMapper) {
        this.carreraService = carreraService;
        this.planService = planService;
        this.facultadRepository = facultadRepository;
        this.carreraMapper = carreraMapper;
        this.planDeEstudioMapper = planDeEstudioMapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CarreraResponse> registrarCarrera(@RequestBody CarreraRequest request) {
        Carrera carrera = carreraMapper.toEntity(request);
        
        // Buscar facultad y asociar
        if (request.getIdFacultad() != null) {
             com.sysacad.backend.modelo.FacultadRegional facultad = facultadRepository.findById(request.getIdFacultad())
                     .orElseThrow(() -> new RuntimeException("Facultad no encontrada"));
             java.util.Set<com.sysacad.backend.modelo.FacultadRegional> facultades = new java.util.HashSet<>();
             facultades.add(facultad);
             carrera.setFacultades(facultades);
        }

        Carrera guardada = carreraService.registrarCarrera(carrera);
        return new ResponseEntity<>(carreraMapper.toDTO(guardada), HttpStatus.CREATED);
    }

    @GetMapping("/facultad/{idFacultad}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CarreraResponse>> listarPorFacultad(@PathVariable UUID idFacultad) {
        List<Carrera> carreras = carreraService.listarCarrerasPorFacultad(idFacultad);
        return ResponseEntity.ok(carreraMapper.toDTOs(carreras));
    }

    @PostMapping("/planes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanDeEstudioResponse> crearPlan(@RequestBody PlanDeEstudioRequest request) {
        PlanDeEstudio plan = planDeEstudioMapper.toEntity(request);
        
        PlanDeEstudio.PlanId id = new PlanDeEstudio.PlanId(
                request.getIdCarrera(), request.getNroPlan());
        plan.setId(id);
        
        if (plan.getNombre() == null) plan.setNombre(request.getNombrePlan()); 
        
        PlanDeEstudio guardado = planService.crearPlanDeEstudio(plan);
        return new ResponseEntity<>(planDeEstudioMapper.toDTO(guardado), HttpStatus.CREATED);
    }

    @PostMapping("/planes/materias")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> agregarMateriaAPlan(@RequestBody PlanMateria planMateria) {
        planService.agregarMateriaAPlan(planMateria);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{idCarrera}/planes/vigentes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PlanDeEstudioResponse>> listarPlanesVigentes(
            @PathVariable UUID idCarrera,
            @RequestParam(required = false) UUID idFacultad) {

        List<PlanDeEstudio> planes = planService.listarPlanesVigentes(idCarrera);

        return ResponseEntity.ok(planDeEstudioMapper.toDTOs(planes));
    }
}