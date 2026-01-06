package com.sysacad.backend.controller;

import com.sysacad.backend.modelo.Carrera;
import com.sysacad.backend.modelo.PlanDeEstudio;
import com.sysacad.backend.modelo.PlanMateria;
import com.sysacad.backend.service.CarreraService;
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

    @Autowired
    public CarreraController(CarreraService carreraService) {
        this.carreraService = carreraService;
    }

    // --- SEGURIDAD: ESCRITURA (SOLO ADMIN) ---

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Carrera> registrarCarrera(@RequestBody Carrera carrera) {
        return new ResponseEntity<>(carreraService.registrarCarrera(carrera), HttpStatus.CREATED);
    }

    @PostMapping("/planes")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PlanDeEstudio> crearPlan(@RequestBody PlanDeEstudio plan) {
        return new ResponseEntity<>(carreraService.crearPlanDeEstudio(plan), HttpStatus.CREATED);
    }

    @PostMapping("/planes/materias")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> agregarMateriaAPlan(@RequestBody PlanMateria planMateria) {
        carreraService.agregarMateriaAPlan(planMateria);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/facultad/{idFacultad}")
    @PreAuthorize("isAuthenticated()") // Buena práctica: Explicitar que requiere login
    public ResponseEntity<List<Carrera>> listarPorFacultad(@PathVariable UUID idFacultad) {
        return ResponseEntity.ok(carreraService.listarCarrerasPorFacultad(idFacultad));
    }

    @GetMapping("/{idCarrera}/planes/vigentes")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PlanDeEstudio>> listarPlanesVigentes(
            @PathVariable String idCarrera,
            @RequestParam(required = false) UUID idFacultad) { // Agregado opcional para robustez

        if (idFacultad != null) {
            // Si tu servicio lo soporta, usa ambos IDs
            // return ResponseEntity.ok(carreraService.listarPlanes(idFacultad, idCarrera));
        }
        return ResponseEntity.ok(carreraService.listarPlanesVigentes(idCarrera));
    }
}