package com.sysacad.backend.controller;

import com.sysacad.backend.dto.PlanDeEstudioRequest;
import com.sysacad.backend.dto.PlanDeEstudioResponse;
import com.sysacad.backend.modelo.PlanDeEstudio;
import com.sysacad.backend.modelo.PlanMateria;
import com.sysacad.backend.service.PlanDeEstudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/planes")
@CrossOrigin(origins = "http://localhost:4200")
public class PlanDeEstudioController {

    private final PlanDeEstudioService planService;

    @Autowired
    public PlanDeEstudioController(PlanDeEstudioService planService) {
        this.planService = planService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanDeEstudioResponse> crearPlan(@RequestBody PlanDeEstudioRequest request) {
        PlanDeEstudio plan = new PlanDeEstudio();
        PlanDeEstudio.PlanId id = new PlanDeEstudio.PlanId(
                request.getIdFacultad(), request.getNroCarrera(), request.getNombrePlan());
        plan.setId(id);
        plan.setFechaInicio(request.getFechaInicio());
        plan.setFechaFin(request.getFechaFin());
        plan.setEsVigente(request.getEsVigente());

        PlanDeEstudio guardado = planService.crearPlanDeEstudio(plan);
        return new ResponseEntity<>(new PlanDeEstudioResponse(guardado), HttpStatus.CREATED);
    }

    @PostMapping("/materias")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> agregarMateriaAPlan(@RequestBody PlanMateria planMateria) {
        planService.agregarMateriaAPlan(planMateria);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/vigentes/{nroCarrera}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PlanDeEstudioResponse>> listarPlanesVigentes(@PathVariable Integer nroCarrera) {
        List<PlanDeEstudio> planes = planService.listarPlanesVigentes(nroCarrera);
        return ResponseEntity.ok(planes.stream()
                .map(PlanDeEstudioResponse::new)
                .collect(Collectors.toList()));
    }

    @GetMapping("/carrera/{nroCarrera}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PlanDeEstudioResponse>> listarTodosPorCarrera(@PathVariable Integer nroCarrera) {
        List<PlanDeEstudio> planes = planService.listarTodosPorCarrera(nroCarrera);
        return ResponseEntity.ok(planes.stream()
                .map(PlanDeEstudioResponse::new)
                .collect(Collectors.toList()));
    }
}