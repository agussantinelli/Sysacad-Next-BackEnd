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
    private final com.sysacad.backend.service.UsuarioService usuarioService;
    private final com.sysacad.backend.mapper.UsuarioMapper usuarioMapper;

    @Autowired
    public AdminController(AdminService adminService,
                           com.sysacad.backend.service.UsuarioService usuarioService,
                           com.sysacad.backend.mapper.UsuarioMapper usuarioMapper) {
        this.adminService = adminService;
        this.usuarioService = usuarioService;
        this.usuarioMapper = usuarioMapper;
    }

    @GetMapping("/usuarios/{id}")
    public ResponseEntity<com.sysacad.backend.dto.usuario.UsuarioResponse> obtenerUsuario(@PathVariable UUID id) {
        return usuarioService.obtenerPorId(id)
                .map(usuarioMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
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

    @DeleteMapping("/inscripciones/{id}")
    public ResponseEntity<Void> eliminarInscripcion(@PathVariable UUID id, @RequestParam String tipo) {
        adminService.eliminarInscripcion(id, tipo);
        return ResponseEntity.noContent().build();
    }
}
