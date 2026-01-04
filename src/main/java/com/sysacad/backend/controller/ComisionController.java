package com.sysacad.backend.controller;

import com.sysacad.backend.modelo.Comision;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.service.ComisionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping
    public ResponseEntity<Comision> crearComision(@RequestBody Comision comision) {
        return new ResponseEntity<>(comisionService.crearComision(comision), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Comision>> listarPorAnio(@RequestParam Integer anio) {
        return ResponseEntity.ok(comisionService.listarPorAnio(anio));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Comision> buscarPorId(@PathVariable UUID id) {
        return comisionService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/profesores")
    public ResponseEntity<Comision> asignarProfesor(@PathVariable UUID id, @RequestBody Usuario profesor) {
        try {
            return ResponseEntity.ok(comisionService.asignarProfesor(id, profesor));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}