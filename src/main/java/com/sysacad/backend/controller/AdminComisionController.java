package com.sysacad.backend.controller;

import com.sysacad.backend.dto.admin.AdminComisionDTO;
import com.sysacad.backend.dto.admin.AsignarMateriaComisionRequest;
import com.sysacad.backend.dto.admin.ProfesorDisponibleDTO;
import com.sysacad.backend.dto.comision.ComisionRequest;
import com.sysacad.backend.dto.salon.SalonResponse;
import com.sysacad.backend.service.AdminComisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/comisiones")
@PreAuthorize("hasRole('ADMIN')")
public class AdminComisionController {

    private final AdminComisionService adminComisionService;

    @Autowired
    public AdminComisionController(AdminComisionService adminComisionService) {
        this.adminComisionService = adminComisionService;
    }

    @GetMapping
    public ResponseEntity<List<AdminComisionDTO>> obtenerTodas() {
        return ResponseEntity.ok(adminComisionService.obtenerTodasConDetalle());
    }

    @PostMapping
    public ResponseEntity<Void> crearComision(@RequestBody @jakarta.validation.Valid ComisionRequest request) {
        adminComisionService.crearComision(request);
        return ResponseEntity.status(201).build();
    }

    @GetMapping("/salones-disponibles")
    public ResponseEntity<List<SalonResponse>> obtenerSalonesDisponibles(
            @RequestParam String turno,
            @RequestParam Integer anio) {
        return ResponseEntity.ok(adminComisionService.obtenerSalonesDisponibles(turno, anio)); // Changed comisionService to adminComisionService
    }

    @PostMapping("/{id}/materias")
    public ResponseEntity<Void> asignarMateria(@PathVariable UUID id, @RequestBody AsignarMateriaComisionRequest request) {
        adminComisionService.asignarMateria(id, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/profesores-disponibles")
    public ResponseEntity<List<ProfesorDisponibleDTO>> obtenerProfesoresDisponibles(@RequestBody AsignarMateriaComisionRequest request) {
        // Reuse the request DTO to pass idMateria and horarios
        return ResponseEntity.ok(adminComisionService.obtenerProfesoresDisponibles(request.getIdMateria(), request.getHorarios()));
    }
    
    @GetMapping("/{id}/materias-disponibles")
    public ResponseEntity<List<com.sysacad.backend.dto.admin.MateriaDisponibleDTO>> obtenerMateriasDisponibles(@PathVariable UUID id) {
        return ResponseEntity.ok(adminComisionService.obtenerMateriasDisponibles(id));
    }
}
