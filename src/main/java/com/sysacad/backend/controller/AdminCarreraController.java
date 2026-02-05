package com.sysacad.backend.controller;

import com.sysacad.backend.dto.admin.CarreraAdminDTO;
import com.sysacad.backend.dto.admin.PlanDetalleDTO;
import com.sysacad.backend.service.AdminCarreraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/carreras")
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasRole('ADMIN')")
public class AdminCarreraController {

    private final AdminCarreraService carreraService;

    @Autowired
    public AdminCarreraController(AdminCarreraService carreraService) {
        this.carreraService = carreraService;
    }

    @GetMapping
    public ResponseEntity<List<CarreraAdminDTO>> obtenerCarrerasConEstadisticas() {
        return ResponseEntity.ok(carreraService.obtenerTodasConEstadisticas());
    }

    @GetMapping("/simples")
    public ResponseEntity<List<com.sysacad.backend.dto.carrera.CarreraResponse>> obtenerTodasSimples() {
        return ResponseEntity.ok(carreraService.obtenerTodas());
    }

    @PostMapping("/{carreraId}/facultades/{facultadId}")
    public ResponseEntity<Void> asociarCarreraFacultad(@PathVariable UUID carreraId, @PathVariable UUID facultadId) {
        carreraService.asociarCarreraFacultad(carreraId, facultadId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{carreraId}/planes/detallados")
    public ResponseEntity<List<PlanDetalleDTO>> obtenerPlanesDetallados(@PathVariable UUID carreraId) {
        return ResponseEntity.ok(carreraService.obtenerPlanesDetallados(carreraId));
    }

    @GetMapping("/{carreraId}/plan/{anio}")
    public ResponseEntity<PlanDetalleDTO> obtenerDetallePlan(@PathVariable UUID carreraId, @PathVariable Integer anio) {
        return ResponseEntity.ok(carreraService.obtenerDetallePlan(carreraId, anio));
    }
}
