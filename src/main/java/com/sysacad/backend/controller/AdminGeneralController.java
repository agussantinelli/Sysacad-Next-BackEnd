package com.sysacad.backend.controller;

import com.sysacad.backend.dto.CarreraResponse;
import com.sysacad.backend.dto.materia.SimpleMateriaDTO;
import com.sysacad.backend.service.AdminGeneralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/general")
@PreAuthorize("hasRole('ADMIN')")
public class AdminGeneralController {

    private final AdminGeneralService adminGeneralService;

    @Autowired
    public AdminGeneralController(AdminGeneralService adminGeneralService) {
        this.adminGeneralService = adminGeneralService;
    }

    @GetMapping("/carreras")
    public ResponseEntity<List<CarreraResponse>> obtenerTodasLasCarreras() {
        return ResponseEntity.ok(adminGeneralService.obtenerTodasLasCarreras());
    }

    @GetMapping("/materias/buscar")
    public ResponseEntity<List<SimpleMateriaDTO>> buscarMateriasPorCarrera(@RequestParam UUID idCarrera, @RequestParam String query) {
        return ResponseEntity.ok(adminGeneralService.buscarMateriasPorCarreraYNombre(idCarrera, query));
    }
}
