package com.sysacad.backend.controller;

import com.sysacad.backend.dto.plan.PlanDeEstudioRequest;
import com.sysacad.backend.dto.plan.PlanDeEstudioResponse;
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
    private final com.sysacad.backend.mapper.PlanDeEstudioMapper planMapper;

    @Autowired
    public PlanDeEstudioController(PlanDeEstudioService planService, com.sysacad.backend.mapper.PlanDeEstudioMapper planMapper) {
        this.planService = planService;
        this.planMapper = planMapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanDeEstudioResponse> crearPlan(@RequestBody PlanDeEstudioRequest request) {
        
        PlanDeEstudio plan = planMapper.toEntity(request);
        
        
        PlanDeEstudio.PlanId id = new PlanDeEstudio.PlanId(
                request.getIdCarrera(), request.getNroPlan());
        plan.setId(id);
        
        if (plan.getNombre() == null) plan.setNombre(request.getNombrePlan());

        PlanDeEstudio guardado = planService.crearPlanDeEstudio(plan);
        return new ResponseEntity<>(planMapper.toDTO(guardado), HttpStatus.CREATED);
    }

    @PostMapping("/materias")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> agregarMateriaAPlan(@RequestBody PlanMateria planMateria) {
        planService.agregarMateriaAPlan(planMateria);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/vigentes/{idCarrera}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PlanDeEstudioResponse>> listarPlanesVigentes(@PathVariable java.util.UUID idCarrera) {
        List<PlanDeEstudio> planes = planService.listarPlanesVigentes(idCarrera);
        return ResponseEntity.ok(planMapper.toDTOs(planes));
    }

    @GetMapping("/carrera/{idCarrera}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PlanDeEstudioResponse>> listarTodosPorCarrera(@PathVariable java.util.UUID idCarrera) {
        List<PlanDeEstudio> planes = planService.listarTodosPorCarrera(idCarrera);
        return ResponseEntity.ok(planMapper.toDTOs(planes));
    }
}