package com.sysacad.backend.dto.profesor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfesorEstadisticasDTO {
    // Cursadas
    private long cantidadTotalAlumnos;
    private long cantidadPromocionados;
    private long cantidadRegulares;
    private long cantidadLibres;
    private BigDecimal notaPromedio;

    // Examenes
    private long cantidadTotalInscriptosExamen;
    private long cantidadAprobadosExamen;
    private long cantidadDesaprobadosExamen;
    private long cantidadAusentesExamen;
}
