package com.sysacad.backend.dto;

import com.sysacad.backend.dto.MateriaResponse.SimpleMateriaDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CarreraMateriasDTO {
    // Usamos String para IDs de carrera según tu entidad CarreraId
    private String idCarrera;
    private String nombreCarrera;
    private String nombrePlan;
    private List<SimpleMateriaDTO> materias;

    public CarreraMateriasDTO(String idCarrera, String nombreCarrera, String nombrePlan, List<SimpleMateriaDTO> materias) {
        this.idCarrera = idCarrera;
        this.nombreCarrera = nombreCarrera;
        this.nombrePlan = nombrePlan;
        this.materias = materias;
    }
}