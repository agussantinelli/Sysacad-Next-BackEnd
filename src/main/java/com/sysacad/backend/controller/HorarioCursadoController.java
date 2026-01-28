package com.sysacad.backend.controller;

import com.sysacad.backend.dto.horario.HorarioRequest;
import com.sysacad.backend.dto.horario.HorarioResponse;
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
    private final com.sysacad.backend.mapper.HorarioMapper horarioMapper;

    @Autowired
    public HorarioCursadoController(HorarioCursadoService horarioService, ComisionService comisionService,
            MateriaService materiaService, com.sysacad.backend.mapper.HorarioMapper horarioMapper) {
        this.horarioService = horarioService;
        this.comisionService = comisionService;
        this.materiaService = materiaService;
        this.horarioMapper = horarioMapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> registrarHorario(@RequestBody HorarioRequest request) {
        try {
            Comision comision = comisionService.buscarPorId(request.getIdComision())
                    .orElseThrow(() -> new RuntimeException("Comisión no encontrada"));

            Materia materia = materiaService.buscarPorId(request.getIdMateria())
                    .orElseThrow(() -> new RuntimeException("Materia no encontrada"));
            
            HorarioCursado horario = horarioMapper.toEntity(request);
            
            HorarioCursadoId id = new HorarioCursadoId(
                    request.getIdComision(),
                    request.getIdMateria(),
                    request.getDia(),
                    request.getHoraDesde());
            horario.setId(id);
            horario.setComision(comision);
            horario.setMateria(materia);
            if (horario.getHoraHasta() == null) horario.setHoraHasta(request.getHoraHasta());

            HorarioCursado nuevo = horarioService.registrarHorario(horario);
            return new ResponseEntity<>(horarioMapper.toDTO(nuevo), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/comision/{idComision}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR', 'ESTUDIANTE')")
    public ResponseEntity<List<HorarioResponse>> obtenerPorComision(@PathVariable UUID idComision) {
        List<HorarioCursado> horarios = horarioService.obtenerPorComision(idComision);
        return ResponseEntity.ok(horarioMapper.toDTOs(horarios));
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
