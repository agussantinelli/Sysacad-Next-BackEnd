package com.sysacad.backend.controller;

import com.sysacad.backend.dto.admin.AdminEstadisticasDTO;
import com.sysacad.backend.dto.admin.AdminInscripcionDTO;
import com.sysacad.backend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/inscripciones")
    public ResponseEntity<List<AdminInscripcionDTO>> obtenerTodasInscripciones() {
        return ResponseEntity.ok(adminService.obtenerTodasInscripciones());
    }

    @GetMapping("/estadisticas")
    public ResponseEntity<AdminEstadisticasDTO> obtenerEstadisticas(
            @RequestParam(required = false) Integer anio,
            @RequestParam(required = false) UUID facultadId,
            @RequestParam(required = false) UUID carreraId) {
        return ResponseEntity.ok(adminService.obtenerEstadisticas(anio, facultadId, carreraId));
    }
}
