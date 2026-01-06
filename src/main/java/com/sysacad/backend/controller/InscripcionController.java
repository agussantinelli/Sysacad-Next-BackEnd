package com.sysacad.backend.controller;

import com.sysacad.backend.dto.CalificacionRequest;
import com.sysacad.backend.dto.InscripcionRequest;
import com.sysacad.backend.dto.InscripcionResponse;
import com.sysacad.backend.modelo.Calificacion;
import com.sysacad.backend.modelo.Inscripcion;
import com.sysacad.backend.modelo.Inscripcion.InscripcionId;
import com.sysacad.backend.modelo.Calificacion.CalificacionId;
import com.sysacad.backend.service.CorrelatividadService;
import com.sysacad.backend.service.InscripcionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    public ResponseEntity<?> inscribirAlumno(@RequestBody InscripcionRequest request) {
        try {
            Inscripcion inscripcion = new Inscripcion();
            InscripcionId id = new InscripcionId(
                    request.getIdUsuario(),
                    request.getIdComision(),
                    request.getTipo(),
                    request.getVecesTipo() != null ? request.getVecesTipo() : 1
            );
            inscripcion.setId(id);
            inscripcion.setCondicion(request.getCondicion());

            Inscripcion guardada = inscripcionService.inscribirAlumno(inscripcion);

            return new ResponseEntity<>(new InscripcionResponse(guardada), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/alumno/{idAlumno}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTUDIANTE')")
    public ResponseEntity<List<InscripcionResponse>> historialAlumno(@PathVariable UUID idAlumno) {
        try {
            List<Inscripcion> historial = inscripcionService.obtenerHistorialAlumno(idAlumno);

            List<InscripcionResponse> response = historial.stream()
                    .map(InscripcionResponse::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    @PostMapping("/notas")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    public ResponseEntity<Void> cargarNota(@RequestBody CalificacionRequest request) {
        try {
            Calificacion calificacion = new Calificacion();
            CalificacionId id = new CalificacionId(
                    request.getIdUsuario(),
                    request.getIdComision(),
                    request.getTipoInscripcion(),
                    request.getVecesTipoInscripcion(),
                    request.getConcepto()
            );
            calificacion.setId(id);
            calificacion.setNota(request.getNota());

            inscripcionService.cargarNota(calificacion);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/validar-correlatividad")
    @PreAuthorize("hasAnyRole('ADMIN', 'ESTUDIANTE')")
    public ResponseEntity<Boolean> validarCorrelatividad(@RequestParam UUID idAlumno, @RequestParam UUID idMateria) {
        return ResponseEntity.ok(correlatividadService.puedeCursar(idAlumno, idMateria));
    }
}