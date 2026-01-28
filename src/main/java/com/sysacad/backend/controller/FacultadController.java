package com.sysacad.backend.controller;

import com.sysacad.backend.dto.facultad.FacultadRequest;
import com.sysacad.backend.dto.facultad.FacultadResponse;
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
    private final com.sysacad.backend.mapper.FacultadMapper facultadMapper;

    @Autowired
    public FacultadController(FacultadService facultadService, com.sysacad.backend.mapper.FacultadMapper facultadMapper) {
        this.facultadService = facultadService;
        this.facultadMapper = facultadMapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FacultadResponse> crearFacultad(@RequestBody FacultadRequest request) {
        FacultadRegional facultad = facultadMapper.toEntity(request);
        FacultadRegional guardada = facultadService.crearFacultad(facultad);
        return new ResponseEntity<>(facultadMapper.toDTO(guardada), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<FacultadResponse>> listarTodas() {
        List<FacultadRegional> facultades = facultadService.listarTodas();
        return ResponseEntity.ok(facultadMapper.toDTOs(facultades));
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FacultadResponse> buscarPorId(@PathVariable UUID id) {
        try {
            FacultadRegional facultad = facultadService.buscarPorId(id);
            return ResponseEntity.ok(facultadMapper.toDTO(facultad));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}