package com.sysacad.backend.controller;

import com.sysacad.backend.dto.admin.MatriculacionRequest;
import com.sysacad.backend.dto.carrera.CarreraResponse;
import com.sysacad.backend.dto.facultad.FacultadResponse;
import com.sysacad.backend.dto.plan.PlanDeEstudioResponse;
import com.sysacad.backend.dto.usuario.UsuarioResponse;
import com.sysacad.backend.service.AdminMatriculacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/matriculacion")
@CrossOrigin(origins = "http://localhost:4200")
@PreAuthorize("hasRole('ADMIN')")
public class AdminMatriculacionController {

    private final AdminMatriculacionService matriculacionService;

    @Autowired
    public AdminMatriculacionController(AdminMatriculacionService matriculacionService) {
        this.matriculacionService = matriculacionService;
    }

    @GetMapping("/usuarios/buscar")
    public ResponseEntity<List<UsuarioResponse>> buscarUsuarios(@RequestParam String legajo) {
        return ResponseEntity.ok(matriculacionService.buscarUsuariosPorLegajo(legajo));
    }

    @GetMapping("/facultades")
    public ResponseEntity<List<FacultadResponse>> obtenerFacultades() {
        return ResponseEntity.ok(matriculacionService.obtenerTodasFacultades());
    }

    @GetMapping("/carreras")
    public ResponseEntity<List<CarreraResponse>> obtenerCarreras(@RequestParam UUID facultadId) {
        return ResponseEntity.ok(matriculacionService.obtenerCarrerasPorFacultad(facultadId));
    }

    @GetMapping("/planes")
    public ResponseEntity<List<PlanDeEstudioResponse>> obtenerPlanes(@RequestParam UUID carreraId) {
        return ResponseEntity.ok(matriculacionService.obtenerPlanesPorCarrera(carreraId));
    }

    @PostMapping
    public ResponseEntity<Void> crearMatriculacion(@RequestBody MatriculacionRequest request) {
        matriculacionService.crearMatriculacion(request);
        return ResponseEntity.ok().build();
    }
}
