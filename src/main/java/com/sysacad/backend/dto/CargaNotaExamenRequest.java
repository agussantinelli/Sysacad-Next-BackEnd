package com.sysacad.backend.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CargaNotaExamenRequest {
    private BigDecimal nota;
    private String estado; // APROBADO, DESAPROBADO, AUSENTE
}
