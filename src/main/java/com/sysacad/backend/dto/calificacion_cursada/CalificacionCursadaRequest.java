package com.sysacad.backend.dto.calificacion_cursada;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CalificacionCursadaRequest {
    private String descripcion;
    private BigDecimal nota;
}
