package com.sysacad.backend.service.pdf;

import com.sysacad.backend.dto.alumno.AlumnoCertificadoDTO;

public interface IPdfGenerator {
    byte[] generarCertificado(AlumnoCertificadoDTO datos);
}
