package com.sysacad.backend.dto.inscripcion_examen;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CargaNotaExamenRequest {
    private BigDecimal nota;
    private com.sysacad.backend.modelo.enums.EstadoExamen estado; // APROBADO, DESAPROBADO, AUSENTE
}
