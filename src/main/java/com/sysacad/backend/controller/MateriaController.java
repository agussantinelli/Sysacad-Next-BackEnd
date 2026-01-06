package com.sysacad.backend.controller;

import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.modelo.enums.TipoMateria;
import com.sysacad.backend.service.MateriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/materias")
@CrossOrigin(origins = "http://localhost:4200")
public class MateriaController {

    private final MateriaService materiaService;

    @Autowired
    public MateriaController(MateriaService materiaService) {
        this.materiaService = materiaService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Materia> crearMateria(@RequestBody Materia materia) {
        return new ResponseEntity<>(materiaService.crearMateria(materia), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    public ResponseEntity<Materia> actualizarMateria(@PathVariable UUID id, @RequestBody Materia materia) {
        try {
            return ResponseEntity.ok(materiaService.actualizarMateria(id, materia));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Materia>> listarTodas(@RequestParam(required = false) TipoMateria tipo) {
        if (tipo != null) {
            return ResponseEntity.ok(materiaService.buscarPorTipo(tipo));
        }
        return ResponseEntity.ok(materiaService.listarTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Materia> buscarPorId(@PathVariable UUID id) {
        return materiaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarMateria(@PathVariable UUID id) {
        materiaService.eliminarMateria(id);
        return ResponseEntity.noContent().build();
    }
}