package com.sysacad.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CarreraMateriasDTO {
    private java.util.UUID idCarrera;
    private String nombreCarrera;
    private String nombrePlan;
    private List<EstudianteMateriaDTO> materias;

    public CarreraMateriasDTO(java.util.UUID idCarrera, String nombreCarrera, String nombrePlan,
            List<EstudianteMateriaDTO> materias) {
        this.idCarrera = idCarrera;
        this.nombreCarrera = nombreCarrera;
        this.nombrePlan = nombrePlan;
        this.materias = materias;
    }
}