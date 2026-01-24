package com.sysacad.backend.controller;

import com.sysacad.backend.dto.aviso.AvisoRequest;
import com.sysacad.backend.dto.aviso.AvisoResponse;
import com.sysacad.backend.modelo.Aviso;
import com.sysacad.backend.service.AvisoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/avisos")
@CrossOrigin(origins = "http://localhost:4200")
public class AvisoController {

    private final AvisoService avisoService;
    private final com.sysacad.backend.mapper.AvisoMapper avisoMapper;

    @Autowired
    public AvisoController(AvisoService avisoService, com.sysacad.backend.mapper.AvisoMapper avisoMapper) {
        this.avisoService = avisoService;
        this.avisoMapper = avisoMapper;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AvisoResponse> publicarAviso(@RequestBody AvisoRequest request) {
        Aviso aviso = avisoMapper.toEntity(request);
        // El servicio setea fecha y estado por defecto, o usamos el request si aplica
        // El request tiene estado, pero el servicio lo sobreescribe a 'PUBLICADO'.
        Aviso nuevo = avisoService.publicarAviso(aviso);
        return new ResponseEntity<>(avisoMapper.toDTO(nuevo), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AvisoResponse>> obtenerUltimosAvisos() {
        List<Aviso> avisos = avisoService.obtenerUltimosAvisos();
        return ResponseEntity.ok(avisoMapper.toDTOs(avisos));
    }
}
