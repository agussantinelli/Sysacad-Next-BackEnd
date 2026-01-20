package com.sysacad.backend.controller;

import com.sysacad.backend.dto.*;
import com.sysacad.backend.service.MesaExamenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mesas")
@CrossOrigin(origins = "*") // Adjust for production
public class MesaExamenController {

    @Autowired
    private MesaExamenService mesaExamenService;

    @PostMapping
    public ResponseEntity<MesaExamenResponse> createMesa(@RequestBody MesaExamenRequest request) {
        return ResponseEntity.ok(mesaExamenService.createMesa(request));
    }

    @GetMapping
    public ResponseEntity<List<MesaExamenResponse>> getAllMesas() {
        return ResponseEntity.ok(mesaExamenService.getAllMesas());
    }

    @PostMapping("/detalles")
    public ResponseEntity<DetalleMesaExamenResponse> addDetalle(@RequestBody DetalleMesaExamenRequest request) {
        return ResponseEntity.ok(mesaExamenService.addDetalle(request));
    }
}
