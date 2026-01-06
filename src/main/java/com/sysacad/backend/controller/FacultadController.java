package com.sysacad.backend.controller;

import com.sysacad.backend.modelo.FacultadRegional;
import com.sysacad.backend.service.FacultadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/facultades")
@CrossOrigin(origins = "http://localhost:4200")
public class FacultadController {

    private final FacultadService facultadService;

    @Autowired
    public FacultadController(FacultadService facultadService) {
        this.facultadService = facultadService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FacultadRegional> crearFacultad(@RequestBody FacultadRegional facultad) {
        return new ResponseEntity<>(facultadService.crearFacultad(facultad), HttpStatus.CREATED);
    }


    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FacultadRegional>> listarTodas() {
        return ResponseEntity.ok(facultadService.listarTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FacultadRegional> buscarPorId(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(facultadService.buscarPorId(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}