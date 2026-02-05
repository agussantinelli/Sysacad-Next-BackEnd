package com.sysacad.backend.controller;

import com.sysacad.backend.dto.admin.FacultadRequest;
import com.sysacad.backend.dto.facultad.FacultadResponse;
import com.sysacad.backend.service.AdminFacultadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/facultades")
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasRole('ADMIN')")
public class AdminFacultadController {

    private final AdminFacultadService facultadService;

    @Autowired
    public AdminFacultadController(AdminFacultadService facultadService) {
        this.facultadService = facultadService;
    }

    @GetMapping
    public ResponseEntity<List<FacultadResponse>> obtenerTodas() {
        return ResponseEntity.ok(facultadService.obtenerTodas());
    }

    @PostMapping
    public ResponseEntity<Void> crearFacultad(@RequestBody FacultadRequest request) {
        facultadService.crearFacultad(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarFacultad(@PathVariable UUID id) {
        facultadService.eliminarFacultad(id);
        return ResponseEntity.noContent().build();
    }
}
