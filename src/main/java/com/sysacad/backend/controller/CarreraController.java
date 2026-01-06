package com.sysacad.backend.controller;

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

@RestController
@RequestMapping("/api/carreras")
@CrossOrigin(origins = "http://localhost:4200")
public class CarreraController {

    private final CarreraService carreraService;
    private final PlanDeEstudioService planService;

    @Autowired
    public CarreraController(CarreraService carreraService, PlanDeEstudioService planService) {
        this.carreraService = carreraService;
        this.planService = planService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Carrera> registrarCarrera(@RequestBody Carrera carrera) {
        return new ResponseEntity<>(carreraService.registrarCarrera(carrera), HttpStatus.CREATED);
    }

    @GetMapping("/facultad/{idFacultad}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Carrera>> listarPorFacultad(@PathVariable UUID idFacultad) {
        return ResponseEntity.ok(carreraService.listarCarrerasPorFacultad(idFacultad));
    }


    @PostMapping("/planes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanDeEstudio> crearPlan(@RequestBody PlanDeEstudio plan) {
        return new ResponseEntity<>(planService.crearPlanDeEstudio(plan), HttpStatus.CREATED);
    }

    @PostMapping("/planes/materias")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> agregarMateriaAPlan(@RequestBody PlanMateria planMateria) {
        planService.agregarMateriaAPlan(planMateria);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{idCarrera}/planes/vigentes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PlanDeEstudio>> listarPlanesVigentes(
            @PathVariable String idCarrera,
            @RequestParam(required = false) UUID idFacultad) {
        return ResponseEntity.ok(planService.listarPlanesVigentes(idCarrera));
    }
}