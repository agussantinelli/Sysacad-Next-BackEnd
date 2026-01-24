package com.sysacad.backend.controller;

import com.sysacad.backend.dto.carrera_materias.CarreraMateriasDTO;
import com.sysacad.backend.service.MatriculacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/alumnos")
@CrossOrigin(origins = "http://localhost:4200")
public class MatriculacionController {

    private final MatriculacionService matriculacionService;

    @Autowired
    public MatriculacionController(MatriculacionService matriculacionService) {
        this.matriculacionService = matriculacionService;
    }

    @GetMapping("/mis-carreras-materias")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    public ResponseEntity<List<CarreraMateriasDTO>> obtenerMisMateriasPorCarrera() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String legajo = auth.getName();

        List<CarreraMateriasDTO> response = matriculacionService.obtenerMateriasPorCarreraDelAlumno(legajo);

        if (response.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(response);
    }
}