package com.sysacad.backend.dto.profesor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfesorEstadisticasDTO {
    
    private long cantidadTotalAlumnos;
    private long cantidadPromocionados;
    private long cantidadRegulares;
    private long cantidadLibres;
    private BigDecimal notaPromedio;

    
    private long cantidadTotalInscriptosExamen;
    private long cantidadAprobadosExamen;
    private long cantidadDesaprobadosExamen;
    private long cantidadAusentesExamen;
}
