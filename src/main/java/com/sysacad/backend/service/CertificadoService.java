package com.sysacad.backend.service;

import com.sysacad.backend.dto.alumno.AlumnoCertificadoDTO;
import com.sysacad.backend.modelo.Matriculacion;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.repository.MatriculacionRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import com.sysacad.backend.service.pdf.IPdfGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class CertificadoService {

    private final UsuarioRepository usuarioRepository;
    private final MatriculacionRepository matriculacionRepository; // Para saber qué carrera cursa
    private final IPdfGenerator pdfGenerator;

    @Autowired
    public CertificadoService(UsuarioRepository usuarioRepository, 
                              MatriculacionRepository matriculacionRepository, 
                              IPdfGenerator pdfGenerator) {
        this.usuarioRepository = usuarioRepository;
        this.matriculacionRepository = matriculacionRepository;
        this.pdfGenerator = pdfGenerator;
    }

    @Transactional(readOnly = true)
    public byte[] generarCertificadoRegular(UUID idUsuario) {
        // 1. Obtener datos del alumno
        Usuario alumno = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        // 2. Obtener carrera principal (simplificado: tomamos la primera matriculación vigente)
        // En un caso real, el alumno elegiría de qué carrera quiere el certificado si tiene varias.
        Matriculacion matricula = matriculacionRepository.findByIdIdUsuario(alumno.getId()).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("El alumno no está inscripto en ninguna carrera"));

        String nombreCarrera = matricula.getPlan().getCarrera().getNombre();
        // Obtener nombre de Facultad. Asumimos que la carrera tiene facultades asociadas, tomamos la primera.
        String nombreFacultad = matricula.getPlan().getCarrera().getFacultades().stream()
                .findFirst()
                .map(f -> "Facultad Regional " + f.getCiudad()) // O nombreCompleto si lo tuviera, usaste ciudad en el DTO
                .orElse("Facultad Regional Rosario"); // Fallback

        // 3. Construir DTO inmutable
        AlumnoCertificadoDTO datos = new AlumnoCertificadoDTO(
                alumno.getNombre() + " " + alumno.getApellido(),
                alumno.getDni(),
                alumno.getLegajo(),
                nombreCarrera,
                nombreFacultad,
                LocalDate.now()
        );

        // 4. Generar PDF
        return pdfGenerator.generarCertificado(datos);
    }
}
