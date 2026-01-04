package com.sysacad.backend.controller;

import com.sysacad.backend.modelo.Calificacion;
import com.sysacad.backend.modelo.Inscripcion;
import com.sysacad.backend.service.CorrelatividadService;
import com.sysacad.backend.service.InscripcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> inscribirAlumno(@RequestBody Inscripcion inscripcion) {
        try {
            // 1. Validar Correlatividades antes de inscribir
            // Nota: Aquí asumimos que la inscripción viene con el ID de usuario y el ID de la materia a la que pertenece la comisión
            // Como Inscripción apunta a Comisión, necesitaríamos navegar hasta la materia.
            // Para simplificar, si el frontend envía los IDs correctos, validamos.

            // Ejemplo de uso:
            // boolean apto = correlatividadService.puedeCursar(inscripcion.getId().getIdUsuario(), idMateria);
            // if (!apto) return ResponseEntity.status(HttpStatus.FORBIDDEN).body("No cumple correlativas");

            return new ResponseEntity<>(inscripcionService.inscribirAlumno(inscripcion), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/validar-correlatividad")
    public ResponseEntity<Boolean> validarCorrelatividad(@RequestParam UUID idAlumno, @RequestParam UUID idMateria) {
        boolean puedeCursar = correlatividadService.puedeCursar(idAlumno, idMateria);
        return ResponseEntity.ok(puedeCursar);
    }

    @GetMapping("/alumno/{idAlumno}")
    public ResponseEntity<List<Inscripcion>> historialAlumno(@PathVariable UUID idAlumno) {
        return ResponseEntity.ok(inscripcionService.obtenerHistorialAlumno(idAlumno));
    }

    @PostMapping("/notas")
    public ResponseEntity<Void> cargarNota(@RequestBody Calificacion calificacion) {
        inscripcionService.cargarNota(calificacion);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}