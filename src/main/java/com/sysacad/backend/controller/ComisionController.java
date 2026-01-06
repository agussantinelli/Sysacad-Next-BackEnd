package com.sysacad.backend.controller;

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

@RestController
@RequestMapping("/api/comisiones")
@CrossOrigin(origins = "http://localhost:4200")
public class ComisionController {

    private final ComisionService comisionService;

    @Autowired
    public ComisionController(ComisionService comisionService) {
        this.comisionService = comisionService;
    }

    // SEGURIDAD: SOLO ADMIN PUEDE CREAR
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Comision> crearComision(@RequestBody Comision comision) {
        return new ResponseEntity<>(comisionService.crearComision(comision), HttpStatus.CREATED);
    }

    // SEGURIDAD: PROFESOR Y ADMIN PUEDEN LISTAR POR AÑO
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    public ResponseEntity<List<Comision>> listarPorAnio(@RequestParam Integer anio) {
        return ResponseEntity.ok(comisionService.listarPorAnio(anio));
    }

    // SEGURIDAD: SOLO ADMIN PUEDE BUSCAR POR ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Comision> buscarPorId(@PathVariable UUID id) {
        return comisionService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // SEGURIDAD: ADMIN O JEFE DE CÁTEDRA (Validado en el servicio)
    @PostMapping("/{id}/profesores")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    public ResponseEntity<Comision> asignarProfesor(@PathVariable UUID id, @RequestBody Usuario profesor) {
        try {
            return ResponseEntity.ok(comisionService.asignarProfesor(id, profesor));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null); // O un mensaje de error más específico
        }
    }
}