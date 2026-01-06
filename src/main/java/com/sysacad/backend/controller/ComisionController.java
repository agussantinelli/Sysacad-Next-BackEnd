package com.sysacad.backend.controller;

import com.sysacad.backend.dto.ComisionRequest;
import com.sysacad.backend.dto.ComisionResponse;
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

    @Autowired
    public ComisionController(ComisionService comisionService) {
        this.comisionService = comisionService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ComisionResponse> crearComision(@RequestBody ComisionRequest request) {
        Comision nueva = new Comision();
        nueva.setNombre(request.getNombre());
        nueva.setTurno(request.getTurno());
        nueva.setAnio(request.getAnio());

        Comision guardada = comisionService.crearComision(nueva);
        return new ResponseEntity<>(new ComisionResponse(guardada), HttpStatus.CREATED);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    public ResponseEntity<List<ComisionResponse>> listarPorAnio(@RequestParam Integer anio) {
        List<Comision> comisiones = comisionService.listarPorAnio(anio);

        List<ComisionResponse> dtos = comisiones.stream()
                .map(ComisionResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ComisionResponse> buscarPorId(@PathVariable UUID id) {
        return comisionService.buscarPorId(id)
                .map(c -> ResponseEntity.ok(new ComisionResponse(c)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/profesores")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    public ResponseEntity<ComisionResponse> asignarProfesor(@PathVariable UUID id, @RequestBody Usuario profesor) {
        try {
            Comision actualizada = comisionService.asignarProfesor(id, profesor);
            return ResponseEntity.ok(new ComisionResponse(actualizada));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}