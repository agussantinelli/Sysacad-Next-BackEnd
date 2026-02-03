package com.sysacad.backend.dto.comision;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CalificacionDTO {
    private String concepto;
    private BigDecimal nota;
    private LocalDate fecha;
}
