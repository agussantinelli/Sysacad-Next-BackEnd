package com.sysacad.backend.dto.admin;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class MesaExamenRequest {
    private String nombre; 
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
}
