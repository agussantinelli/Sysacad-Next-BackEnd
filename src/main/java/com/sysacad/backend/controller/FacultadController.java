package com.sysacad.backend.controller;

import com.sysacad.backend.dto.FacultadRequest;
import com.sysacad.backend.dto.FacultadResponse;
import com.sysacad.backend.modelo.FacultadRegional;
import com.sysacad.backend.service.FacultadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public ResponseEntity<FacultadResponse> crearFacultad(@RequestBody FacultadRequest request) {
        // DTO -> Entidad
        FacultadRegional facultad = new FacultadRegional();
        facultad.setCiudad(request.getCiudad());
        facultad.setProvincia(request.getProvincia());

        FacultadRegional guardada = facultadService.crearFacultad(facultad);

        // Entidad -> DTO
        return new ResponseEntity<>(new FacultadResponse(guardada), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FacultadResponse>> listarTodas() {
        List<FacultadRegional> facultades = facultadService.listarTodas();

        List<FacultadResponse> response = facultades.stream()
                .map(FacultadResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FacultadResponse> buscarPorId(@PathVariable UUID id) {
        try {
            FacultadRegional facultad = facultadService.buscarPorId(id);
            return ResponseEntity.ok(new FacultadResponse(facultad));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}