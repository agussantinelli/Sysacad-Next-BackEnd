package com.sysacad.backend.controller;

import com.sysacad.backend.dto.admin.DetalleMesaRequest;
import com.sysacad.backend.dto.admin.MesaAdminDTO;
import com.sysacad.backend.dto.admin.MesaRequest;
import com.sysacad.backend.dto.admin.ProfesorDisponibleDTO;
import com.sysacad.backend.service.AdminMesaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/mesas")
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasRole('ADMIN')")
public class AdminMesaController {

    private final AdminMesaService mesaService;

    @Autowired
    public AdminMesaController(AdminMesaService mesaService) {
        this.mesaService = mesaService;
    }

    @GetMapping
    public ResponseEntity<List<MesaAdminDTO>> obtenerMesasConEstadisticas() {
        return ResponseEntity.ok(mesaService.obtenerTodasConEstadisticas());
    }

    @PostMapping("/turnos")
    public ResponseEntity<Void> crearTurno(@RequestBody com.sysacad.backend.dto.admin.MesaExamenRequest request) {
        mesaService.crearMesaExamen(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/detalles")
    public ResponseEntity<Void> agregarDetalleMesa(@RequestBody DetalleMesaRequest request) {
        mesaService.agregarDetalleMesa(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profesores-disponibles")
    public ResponseEntity<List<ProfesorDisponibleDTO>> obtenerProfesoresDisponibles(
            @RequestParam UUID idMateria,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime hora) {
        return ResponseEntity.ok(mesaService.obtenerProfesoresDisponibles(idMateria, fecha, hora));
    }

    @GetMapping("/turnos")
    public ResponseEntity<List<com.sysacad.backend.dto.mesa_examen.MesaExamenResponse>> obtenerTurnos() {
       return ResponseEntity.ok(mesaService.obtenerTurnos());
    }

    @DeleteMapping("/{idMesa}/detalle/{nroDetalle}")
    public ResponseEntity<Void> eliminarDetalleMesa(@PathVariable UUID idMesa, @PathVariable Integer nroDetalle) {
        mesaService.eliminarDetalleMesa(idMesa, nroDetalle);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/turnos/{id}")
    public ResponseEntity<Void> eliminarTurno(@PathVariable UUID id) {
        mesaService.eliminarTurno(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/turnos/{id}")
    public ResponseEntity<Void> editarTurno(@PathVariable UUID id, @RequestBody com.sysacad.backend.dto.admin.MesaExamenRequest request) {
        mesaService.editarMesaExamen(id, request);
        return ResponseEntity.ok().build();
    }
}
