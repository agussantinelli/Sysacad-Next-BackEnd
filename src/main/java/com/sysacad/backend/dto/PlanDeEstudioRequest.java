package com.sysacad.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
public class PlanDeEstudioRequest {
    private UUID idCarrera;
    private Integer nroPlan; // PK, ej: 2023
    private String nombrePlan; // Atributo, ej: "Plan 2023"
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean esVigente;
}