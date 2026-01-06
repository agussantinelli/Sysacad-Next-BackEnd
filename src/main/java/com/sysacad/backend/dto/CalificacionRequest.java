package com.sysacad.backend.dto;

import com.sysacad.backend.modelo.enums.TipoInscripcion;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
public class CalificacionRequest {
    // Datos para identificar la inscripción unívocamente (PK Compuesta)
    private UUID idUsuario;
    private UUID idComision;
    private TipoInscripcion tipoInscripcion;
    private Integer vecesTipoInscripcion;

    // Datos de la calificación
    private String concepto; // Ej: "Parcial 1", "Recuperatorio", "TP Final"
    private BigDecimal nota;
}