package com.sysacad.backend.controller;

import com.sysacad.backend.modelo.Calificacion;
import com.sysacad.backend.modelo.Inscripcion;
import com.sysacad.backend.service.CorrelatividadService;
import com.sysacad.backend.service.InscripcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inscripciones")
@CrossOrigin(origins = "http://localhost:4200")
public class InscripcionController {

    private final InscripcionService inscripcionService;
    private final CorrelatividadService correlatividadService;

    @Autowired
    public InscripcionController(InscripcionService inscripcionService, CorrelatividadService correlatividadService) {
        this.inscripcionService = inscripcionService;
        this.correlatividadService = correlatividadService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTUDIANTE')")
    public ResponseEntity<?> inscribirAlumno(@RequestBody Inscripcion inscripcion) {
        try {
            return new ResponseEntity<>(inscripcionService.inscribirAlumno(inscripcion), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/validar-correlatividad")
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTUDIANTE')")
    public ResponseEntity<Boolean> validarCorrelatividad(@RequestParam UUID idAlumno, @RequestParam UUID idMateria) {
        boolean puedeCursar = correlatividadService.puedeCursar(idAlumno, idMateria);
        return ResponseEntity.ok(puedeCursar);
    }

    @GetMapping("/alumno/{idAlumno}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTUDIANTE')")
    public ResponseEntity<List<Inscripcion>> historialAlumno(@PathVariable UUID idAlumno) {
        try {
            return ResponseEntity.ok(inscripcionService.obtenerHistorialAlumno(idAlumno));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/notas")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    public ResponseEntity<Void> cargarNota(@RequestBody Calificacion calificacion) {
        try {
            inscripcionService.cargarNota(calificacion);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}