package com.sysacad.backend.controller;

import com.sysacad.backend.dto.SalonRequest;
import com.sysacad.backend.dto.SalonResponse;
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

    @Autowired
    public SalonController(SalonService salonService, FacultadService facultadService) {
        this.salonService = salonService;
        this.facultadService = facultadService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<SalonResponse> crearSalon(@RequestBody SalonRequest request) {
        Salon salon = new Salon();
        salon.setNombre(request.getNombre());
        salon.setPiso(request.getPiso());

        FacultadRegional facultad = facultadService.buscarPorId(request.getIdFacultad());
        salon.setFacultad(facultad);

        Salon nuevo = salonService.crearSalon(salon);
        return new ResponseEntity<>(new SalonResponse(nuevo), HttpStatus.CREATED);
    }

    @GetMapping("/facultad/{idFacultad}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR', 'ESTUDIANTE')")
    public ResponseEntity<List<SalonResponse>> listarPorFacultad(@PathVariable UUID idFacultad) {
        List<Salon> salones = salonService.listarPorFacultad(idFacultad);
        List<SalonResponse> response = salones.stream()
                .map(SalonResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }
}
