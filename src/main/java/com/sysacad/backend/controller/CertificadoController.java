package com.sysacad.backend.controller;

import com.sysacad.backend.service.CertificadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.sysacad.backend.repository.UsuarioRepository;
import com.sysacad.backend.modelo.Usuario;

@RestController
@RequestMapping("/api/alumnos")
public class CertificadoController {

    private final CertificadoService certificadoService;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public CertificadoController(CertificadoService certificadoService, UsuarioRepository usuarioRepository) {
        this.certificadoService = certificadoService;
        this.usuarioRepository = usuarioRepository;
    }

    @GetMapping("/certificado-regular")
    @PreAuthorize("hasRole('ESTUDIANTE')")
    public ResponseEntity<byte[]> descargarCertificadoRegular(Authentication authentication) {
        String username = authentication.getName();
        Usuario alumno = usuarioRepository.findByLegajoOrMail(username, username)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        byte[] pdfBytes = certificadoService.generarCertificadoRegular(alumno.getId());

        String filename = "certificado_regularidad_" + alumno.getLegajo() + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(pdfBytes);
    }
}
