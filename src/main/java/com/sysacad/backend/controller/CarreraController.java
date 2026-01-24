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

    @Autowired
    public CarreraController(CarreraService carreraService, PlanDeEstudioService planService,
                             com.sysacad.backend.repository.FacultadRegionalRepository facultadRepository) {
        this.carreraService = carreraService;
        this.planService = planService;
        this.facultadRepository = facultadRepository;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CarreraResponse> registrarCarrera(@RequestBody CarreraRequest request) {
        Carrera carrera = new Carrera();
        carrera.setNombre(request.getNombre());
        carrera.setAlias(request.getAlias());

        // Buscar facultad y asociar (ahora es M:N)
        // Nota: Asumimos que al crear, se asocia a la facultad del request.
        // Si la carrera ya existe (mismo ID/Alias), deberíamos solo asociarla, pero aquí asumimos creación nueva.
        if (request.getIdFacultad() != null) {
             com.sysacad.backend.modelo.FacultadRegional facultad = facultadRepository.findById(request.getIdFacultad())
                     .orElseThrow(() -> new RuntimeException("Facultad no encontrada"));
             java.util.Set<com.sysacad.backend.modelo.FacultadRegional> facultades = new java.util.HashSet<>();
             facultades.add(facultad);
             carrera.setFacultades(facultades);
        }

        Carrera guardada = carreraService.registrarCarrera(carrera);
        return new ResponseEntity<>(new CarreraResponse(guardada), HttpStatus.CREATED);
    }

    @GetMapping("/facultad/{idFacultad}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<CarreraResponse>> listarPorFacultad(@PathVariable UUID idFacultad) {
        List<Carrera> carreras = carreraService.listarCarrerasPorFacultad(idFacultad);

        List<CarreraResponse> response = carreras.stream()
                .map(CarreraResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @PostMapping("/planes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanDeEstudioResponse> crearPlan(@RequestBody PlanDeEstudioRequest request) {
        PlanDeEstudio plan = new PlanDeEstudio();
        PlanDeEstudio.PlanId id = new PlanDeEstudio.PlanId(
                request.getIdCarrera(), request.getNroPlan());
        plan.setId(id);
        plan.setNombre(request.getNombrePlan());
        plan.setFechaInicio(request.getFechaInicio());
        plan.setFechaFin(request.getFechaFin());
        plan.setEsVigente(request.getEsVigente());

        PlanDeEstudio guardado = planService.crearPlanDeEstudio(plan);
        return new ResponseEntity<>(new PlanDeEstudioResponse(guardado), HttpStatus.CREATED);
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

        List<PlanDeEstudioResponse> response = planes.stream()
                .map(PlanDeEstudioResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
}