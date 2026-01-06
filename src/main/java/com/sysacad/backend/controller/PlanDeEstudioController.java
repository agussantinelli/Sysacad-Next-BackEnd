package com.sysacad.backend.controller;

import com.sysacad.backend.modelo.PlanDeEstudio;
import com.sysacad.backend.modelo.PlanMateria;
import com.sysacad.backend.service.PlanDeEstudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/planes")
@CrossOrigin(origins = "http://localhost:4200")
public class PlanDeEstudioController {

    private final PlanDeEstudioService planService;

    @Autowired
    public PlanDeEstudioController(PlanDeEstudioService planService) {
        this.planService = planService;
    }

    // --- ESCRITURA: SOLO ADMIN ---

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanDeEstudio> crearPlan(@RequestBody PlanDeEstudio plan) {
        return new ResponseEntity<>(planService.crearPlanDeEstudio(plan), HttpStatus.CREATED);
    }

    @PostMapping("/materias")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> agregarMateriaAPlan(@RequestBody PlanMateria planMateria) {
        planService.agregarMateriaAPlan(planMateria);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/vigentes/{idCarrera}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PlanDeEstudio>> listarPlanesVigentes(@PathVariable String idCarrera) {
        return ResponseEntity.ok(planService.listarPlanesVigentes(idCarrera));
    }

    @GetMapping("/carrera/{idCarrera}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PlanDeEstudio>> listarTodosPorCarrera(@PathVariable String idCarrera) {
        return ResponseEntity.ok(planService.listarTodosPorCarrera(idCarrera));
    }
}