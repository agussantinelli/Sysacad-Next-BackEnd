package com.sysacad.backend.controller;

import com.sysacad.backend.dto.materia.MateriaRequest;
import com.sysacad.backend.dto.materia.MateriaResponse;
import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.modelo.enums.ModalidadMateria;
import com.sysacad.backend.modelo.enums.TipoMateria;
import com.sysacad.backend.service.MateriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/materias")
@CrossOrigin(origins = "http://localhost:4200")
public class MateriaController {

    private final MateriaService materiaService;
    private final com.sysacad.backend.mapper.MateriaMapper materiaMapper;

    @Autowired
    public MateriaController(MateriaService materiaService, com.sysacad.backend.mapper.MateriaMapper materiaMapper) {
        this.materiaService = materiaService;
        this.materiaMapper = materiaMapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MateriaResponse> crearMateria(@RequestBody MateriaRequest request) {
        Materia nuevaMateria = materiaMapper.toEntity(request);
        Materia materiaGuardada = materiaService.crearMateria(nuevaMateria);
        return new ResponseEntity<>(materiaMapper.toDTO(materiaGuardada), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    public ResponseEntity<MateriaResponse> actualizarMateria(@PathVariable UUID id, @RequestBody MateriaRequest request) {
        Materia materiaUpdate = materiaMapper.toEntity(request);

        try {
            Materia actualizada = materiaService.actualizarMateria(id, materiaUpdate);
            return ResponseEntity.ok(materiaMapper.toDTO(actualizada));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<MateriaResponse>> listarTodas(@RequestParam(required = false) TipoMateria tipo) {
        List<Materia> materias;
        if (tipo != null) {
            materias = materiaService.buscarPorTipo(tipo);
        } else {
            materias = materiaService.listarTodas();
        }

        List<MateriaResponse> response = materias.stream()
                .map(materiaMapper::toDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MateriaResponse> buscarPorId(@PathVariable UUID id) {
        return materiaService.buscarPorId(id)
                .map(m -> ResponseEntity.ok(materiaMapper.toDTO(m)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarMateria(@PathVariable UUID id) {
        materiaService.eliminarMateria(id);
        return ResponseEntity.noContent().build();
    }

    @Autowired
    private com.sysacad.backend.repository.UsuarioRepository usuarioRepository;

    @Autowired
    private com.sysacad.backend.service.MesaExamenService mesaExamenService;

    @GetMapping("/{idMateria}/mesas")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    public ResponseEntity<List<com.sysacad.backend.dto.mesa_examen.MesaExamenDisponibleDTO>> getMesasPorMateria(
            @PathVariable UUID idMateria,
            org.springframework.security.core.Authentication authentication) {
        
        if (authentication == null) return ResponseEntity.status(401).build();

        String legajo = authentication.getName();
        com.sysacad.backend.modelo.Usuario usuario = usuarioRepository.findByLegajo(legajo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return ResponseEntity.ok(mesaExamenService.obtenerMesasDisponibles(idMateria, usuario.getId()));
    }
}