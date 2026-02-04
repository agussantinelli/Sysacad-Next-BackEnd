package com.sysacad.backend.dto.reporte;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReporteCertificadoDTO {
    private String legajo;
    private String mail;
    private String nombre;
    private String apellido;
    private String tipoCertificado;
    private LocalDateTime fecha;
}
