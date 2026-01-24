package com.sysacad.backend.controller;

import com.sysacad.backend.dto.salon.SalonRequest;
import com.sysacad.backend.dto.salon.SalonResponse;
import com.sysacad.backend.modelo.FacultadRegional;
import com.sysacad.backend.modelo.Salon;
import com.sysacad.backend.service.FacultadService;
import com.sysacad.backend.service.SalonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/salones")
@CrossOrigin(origins = "http://localhost:4200")
public class SalonController {

    private final SalonService salonService;
    private final FacultadService facultadService;
    private final com.sysacad.backend.mapper.SalonMapper salonMapper;

    @Autowired
    public SalonController(SalonService salonService, FacultadService facultadService, com.sysacad.backend.mapper.SalonMapper salonMapper) {
        this.salonService = salonService;
        this.facultadService = facultadService;
        this.salonMapper = salonMapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SalonResponse> crearSalon(@RequestBody SalonRequest request) {
        Salon salon = salonMapper.toEntity(request);
        
        FacultadRegional facultad = facultadService.buscarPorId(request.getIdFacultad());
        salon.setFacultad(facultad);

        Salon nuevo = salonService.crearSalon(salon);
        return new ResponseEntity<>(salonMapper.toDTO(nuevo), HttpStatus.CREATED);
    }

    @GetMapping("/facultad/{idFacultad}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR', 'ESTUDIANTE')")
    public ResponseEntity<List<SalonResponse>> listarPorFacultad(@PathVariable UUID idFacultad) {
        List<Salon> salones = salonService.listarPorFacultad(idFacultad);
        return ResponseEntity.ok(salonMapper.toDTOs(salones));
    }
}
