package com.sysacad.backend.dto.calificacion_cursada;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
public class CalificacionCursadaResponse {
    private UUID id;
    private String descripcion;
    private BigDecimal nota;
    private LocalDate fecha;
}
