package com.sysacad.backend.controller;

import com.sysacad.backend.dto.comision.ComisionRequest;
import com.sysacad.backend.dto.comision.ComisionResponse;
import com.sysacad.backend.modelo.Comision;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.service.ComisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/comisiones")
@CrossOrigin(origins = "http://localhost:4200")
public class ComisionController {

    private final ComisionService comisionService;
    private final com.sysacad.backend.mapper.ComisionMapper comisionMapper;

    @Autowired
    public ComisionController(ComisionService comisionService, com.sysacad.backend.mapper.ComisionMapper comisionMapper) {
        this.comisionService = comisionService;
        this.comisionMapper = comisionMapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ComisionResponse> crearComision(@RequestBody ComisionRequest request) {
        Comision nueva = comisionMapper.toEntity(request);
        Comision guardada = comisionService.crearComision(nueva);
        return new ResponseEntity<>(comisionMapper.toDTO(guardada), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    public ResponseEntity<List<ComisionResponse>> listarPorAnio(@RequestParam Integer anio) {
        List<Comision> comisiones = comisionService.listarPorAnio(anio);
        return ResponseEntity.ok(comisionMapper.toDTOs(comisiones));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    public ResponseEntity<ComisionResponse> buscarPorId(@PathVariable UUID id) {
        return comisionService.buscarPorId(id)
                .map(c -> ResponseEntity.ok(comisionMapper.toDTO(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/profesores")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    public ResponseEntity<ComisionResponse> asignarProfesor(@PathVariable UUID id, @RequestBody Usuario profesor) {
        try {
            Comision actualizada = comisionService.asignarProfesor(id, profesor);
            return ResponseEntity.ok(comisionMapper.toDTO(actualizada));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}