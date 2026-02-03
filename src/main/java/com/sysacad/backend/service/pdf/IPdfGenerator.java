package com.sysacad.backend.service.pdf;

import com.sysacad.backend.dto.examen.ProfesorCertificadoDTO;

public interface IPdfGenerator {
    byte[] generarCertificado(AlumnoCertificadoDTO datos);
    byte[] generarCertificadoProfesor(ProfesorCertificadoDTO datos);
}
