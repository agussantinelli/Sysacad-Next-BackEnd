package com.sysacad.backend.controller;

import com.sysacad.backend.modelo.Carrera;
import com.sysacad.backend.modelo.PlanDeEstudio;
import com.sysacad.backend.modelo.PlanMateria;
import com.sysacad.backend.service.CarreraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<Carrera> registrarCarrera(@RequestBody Carrera carrera) {
        return new ResponseEntity<>(carreraService.registrarCarrera(carrera), HttpStatus.CREATED);
    }

    @GetMapping("/facultad/{idFacultad}")
    public ResponseEntity<List<Carrera>> listarPorFacultad(@PathVariable UUID idFacultad) {
        return ResponseEntity.ok(carreraService.listarCarrerasPorFacultad(idFacultad));
    }

    @PostMapping("/planes")
    public ResponseEntity<PlanDeEstudio> crearPlan(@RequestBody PlanDeEstudio plan) {
        return new ResponseEntity<>(carreraService.crearPlanDeEstudio(plan), HttpStatus.CREATED);
    }

    @GetMapping("/{idCarrera}/planes/vigentes")
    public ResponseEntity<List<PlanDeEstudio>> listarPlanesVigentes(@PathVariable String idCarrera) {
        return ResponseEntity.ok(carreraService.listarPlanesVigentes(idCarrera));
    }

    @PostMapping("/planes/materias")
    public ResponseEntity<Void> agregarMateriaAPlan(@RequestBody PlanMateria planMateria) {
        carreraService.agregarMateriaAPlan(planMateria);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}