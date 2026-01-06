package com.sysacad.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@NoArgsConstructor
public class PlanDeEstudioRequest {
    // IDs de la Carrera Padre
    private UUID idFacultad;
    private String idCarrera;

    // Identificador del Plan
    private String nombrePlan; // Ej: "Plan 2008", "Plan 2023"

    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean esVigente;
}