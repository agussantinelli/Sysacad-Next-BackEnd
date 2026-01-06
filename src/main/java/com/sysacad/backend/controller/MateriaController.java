package com.sysacad.backend.controller;

import com.sysacad.backend.dto.MateriaRequest;
import com.sysacad.backend.dto.MateriaResponse;
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
import java.util.stream.Collectors;

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
    public ResponseEntity<MateriaResponse> crearMateria(@RequestBody MateriaRequest request) {
        Materia nuevaMateria = new Materia();
        nuevaMateria.setNombre(request.getNombre());
        nuevaMateria.setDescripcion(request.getDescripcion());
        nuevaMateria.setTipoMateria(request.getTipoMateria());
        nuevaMateria.setDuracion(request.getDuracion());
        nuevaMateria.setCuatrimestreDictado(request.getCuatrimestreDictado());
        nuevaMateria.setHorasCursado(request.getHorasCursado());
        nuevaMateria.setRendirLibre(request.getRendirLibre());
        nuevaMateria.setOptativa(request.getOptativa());

        Materia materiaGuardada = materiaService.crearMateria(nuevaMateria);
        return new ResponseEntity<>(new MateriaResponse(materiaGuardada), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PROFESOR')")
    public ResponseEntity<MateriaResponse> actualizarMateria(@PathVariable UUID id, @RequestBody MateriaRequest request) {
        Materia materiaUpdate = new Materia();
        materiaUpdate.setNombre(request.getNombre());
        materiaUpdate.setDescripcion(request.getDescripcion());
        materiaUpdate.setTipoMateria(request.getTipoMateria());
        materiaUpdate.setDuracion(request.getDuracion());
        materiaUpdate.setCuatrimestreDictado(request.getCuatrimestreDictado());
        materiaUpdate.setHorasCursado(request.getHorasCursado());
        materiaUpdate.setRendirLibre(request.getRendirLibre());
        materiaUpdate.setOptativa(request.getOptativa());

        try {
            Materia actualizada = materiaService.actualizarMateria(id, materiaUpdate);
            return ResponseEntity.ok(new MateriaResponse(actualizada));
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
                .map(MateriaResponse::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<MateriaResponse> buscarPorId(@PathVariable UUID id) {
        return materiaService.buscarPorId(id)
                .map(m -> ResponseEntity.ok(new MateriaResponse(m)))
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminarMateria(@PathVariable UUID id) {
        materiaService.eliminarMateria(id);
        return ResponseEntity.noContent().build();
    }
}