package com.sysacad.backend.dto.plan;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
public class PlanDeEstudioRequest {
    private UUID idCarrera;
    private Integer nroPlan; 
    private String nombrePlan; 
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean esVigente;
}