package com.sysacad.backend.service;

import com.sysacad.backend.dto.alumno.AlumnoCertificadoDTO;
import com.sysacad.backend.dto.examen.ProfesorCertificadoDTO;
import com.sysacad.backend.modelo.Matriculacion;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
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
    private final MatriculacionRepository matriculacionRepository; 
    private final IPdfGenerator pdfGenerator;
    private final com.sysacad.backend.repository.SolicitudCertificadoRepository solicitudCertificadoRepository;

    @Autowired
    public CertificadoService(UsuarioRepository usuarioRepository, 
                              MatriculacionRepository matriculacionRepository, 
                              IPdfGenerator pdfGenerator,
                              com.sysacad.backend.repository.SolicitudCertificadoRepository solicitudCertificadoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.matriculacionRepository = matriculacionRepository;
        this.pdfGenerator = pdfGenerator;
        this.solicitudCertificadoRepository = solicitudCertificadoRepository;
    }

    @Transactional
    public byte[] generarCertificadoRegular(UUID idUsuario) {
        
        Usuario alumno = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));
        
        
        com.sysacad.backend.modelo.SolicitudCertificado solicitud = new com.sysacad.backend.modelo.SolicitudCertificado();
        solicitud.setUsuario(alumno);
        solicitud.setFechaSolicitud(java.time.LocalDateTime.now());
        solicitud.setTipo(com.sysacad.backend.modelo.enums.TipoCertificado.ALUMNO_REGULAR);
        solicitudCertificadoRepository.save(solicitud);

        
        
        Matriculacion matricula = matriculacionRepository.findByIdIdUsuario(alumno.getId()).stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("El alumno no está inscripto en ninguna carrera"));

        String nombreCarrera = matricula.getPlan().getCarrera().getNombre();
        
        String nombreFacultad = matricula.getPlan().getCarrera().getFacultades().stream()
                .findFirst()
                .map(f -> "Facultad Regional " + f.getCiudad()) 
                .orElse("Facultad Regional Rosario"); 

        
        AlumnoCertificadoDTO datos = new AlumnoCertificadoDTO(
                alumno.getNombre() + " " + alumno.getApellido(),
                alumno.getDni(),
                alumno.getLegajo(),
                nombreCarrera,
                nombreFacultad,
                LocalDate.now()
        );

        
        return pdfGenerator.generarCertificado(datos);
    }

    @Transactional
    public byte[] generarCertificadoDocente(UUID idUsuario) {
        
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        
        
        com.sysacad.backend.modelo.SolicitudCertificado solicitud = new com.sysacad.backend.modelo.SolicitudCertificado();
        solicitud.setUsuario(usuario);
        solicitud.setFechaSolicitud(java.time.LocalDateTime.now());
        solicitud.setTipo(com.sysacad.backend.modelo.enums.TipoCertificado.DOCENTE);
        solicitudCertificadoRepository.save(solicitud);

        
        String rolStr = switch (usuario.getRol()) {
            case PROFESOR -> "Docente";
            case ADMIN -> "Administrador";
            default -> "Personal";
        };

        
        ProfesorCertificadoDTO datos = new ProfesorCertificadoDTO(
                usuario.getNombre() + " " + usuario.getApellido(),
                usuario.getDni(),
                usuario.getLegajo(),
                rolStr,
                LocalDate.now()
        );

        
        return pdfGenerator.generarCertificadoProfesor(datos);
    }

    @Transactional(readOnly = true)
    public java.util.List<com.sysacad.backend.dto.reporte.ReporteCertificadoDTO> obtenerHistorialDescargas() {
        return solicitudCertificadoRepository.findAll().stream()
                .map(solicitud -> new com.sysacad.backend.dto.reporte.ReporteCertificadoDTO(
                        solicitud.getUsuario().getLegajo(),
                        solicitud.getUsuario().getMail(),
                        solicitud.getUsuario().getNombre(),
                        solicitud.getUsuario().getApellido(),
                        solicitud.getTipo().name(),
                        solicitud.getFechaSolicitud()
                ))
                .sorted((a, b) -> b.getFecha().compareTo(a.getFecha())) 
                .collect(java.util.stream.Collectors.toList());
    }
}
