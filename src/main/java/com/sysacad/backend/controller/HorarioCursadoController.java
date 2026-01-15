package com.sysacad.backend.controller;

import com.sysacad.backend.dto.HorarioRequest;
import com.sysacad.backend.dto.HorarioResponse;
import com.sysacad.backend.modelo.Comision;
import com.sysacad.backend.modelo.HorarioCursado;
import com.sysacad.backend.modelo.HorarioCursado.HorarioCursadoId;
import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.modelo.enums.DiaSemana;
import com.sysacad.backend.service.ComisionService;
import com.sysacad.backend.service.HorarioCursadoService;
import com.sysacad.backend.service.MateriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/horarios")
@CrossOrigin(origins = "http://localhost:4200")
public class HorarioCursadoController {

    private final HorarioCursadoService horarioService;
    private final ComisionService comisionService;
    private final MateriaService materiaService;

    @Autowired
    public HorarioCursadoController(HorarioCursadoService horarioService, ComisionService comisionService,
            MateriaService materiaService) {
        this.horarioService = horarioService;
        this.comisionService = comisionService;
        this.materiaService = materiaService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registrarHorario(@RequestBody HorarioRequest request) {
        try {
            Comision comision = comisionService.buscarPorId(request.getIdComision())
                    .orElseThrow(() -> new RuntimeException("Comisión no encontrada"));

            Materia materia = materiaService.buscarPorId(request.getIdMateria())
                    .orElseThrow(() -> new RuntimeException("Materia no encontrada"));

            HorarioCursado horario = new HorarioCursado();
            HorarioCursadoId id = new HorarioCursadoId(
                    request.getIdComision(),
                    request.getIdMateria(),
                    request.getDia(),
                    request.getHoraDesde());
            horario.setId(id);
            horario.setHoraHasta(request.getHoraHasta());
            horario.setComision(comision);
            horario.setMateria(materia);

            HorarioCursado nuevo = horarioService.registrarHorario(horario);
            return new ResponseEntity<>(new HorarioResponse(nuevo), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/comision/{idComision}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR', 'ESTUDIANTE')")
    public ResponseEntity<List<HorarioResponse>> obtenerPorComision(@PathVariable UUID idComision) {
        List<HorarioCursado> horarios = horarioService.obtenerPorComision(idComision);
        List<HorarioResponse> response = horarios.stream()
                .map(HorarioResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarHorario(
            @RequestParam UUID idComision,
            @RequestParam UUID idMateria,
            @RequestParam DiaSemana dia,
            @RequestParam LocalTime horaDesde) {
        try {
            horarioService.eliminarHorario(idComision, idMateria, dia, horaDesde);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
