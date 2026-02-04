package com.sysacad.backend.controller;

import com.sysacad.backend.dto.reporte.ReporteCertificadoDTO;
import com.sysacad.backend.service.CertificadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reportes")
public class ReporteController {

    private final CertificadoService certificadoService;

    @Autowired
    public ReporteController(CertificadoService certificadoService) {
        this.certificadoService = certificadoService;
    }

    @GetMapping("/certificados")
    // @PreAuthorize("hasRole('ADMIN')") // Uncomment/Enable if needed
    public ResponseEntity<List<ReporteCertificadoDTO>> obtenerHistorialCertificados() {
        List<ReporteCertificadoDTO> historial = certificadoService.obtenerHistorialDescargas();
        return ResponseEntity.ok(historial);
    }
}
