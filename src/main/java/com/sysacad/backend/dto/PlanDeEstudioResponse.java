package com.sysacad.backend.dto;

import com.sysacad.backend.modelo.PlanDeEstudio;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class PlanDeEstudioResponse {
    private Integer nroPlan;
    private String nombrePlan;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private Boolean esVigente;
    private java.util.UUID idCarrera;
    private String nombreCarrera;

    public PlanDeEstudioResponse(PlanDeEstudio plan) {
        this.nroPlan = plan.getId().getNroPlan();
        this.nombrePlan = plan.getNombre();
        this.fechaInicio = plan.getFechaInicio();
        this.fechaFin = plan.getFechaFin();
        this.esVigente = plan.getEsVigente();

        if (plan.getCarrera() != null) {
            this.idCarrera = plan.getCarrera().getId();
            this.nombreCarrera = plan.getCarrera().getNombre();
        }
    }
}