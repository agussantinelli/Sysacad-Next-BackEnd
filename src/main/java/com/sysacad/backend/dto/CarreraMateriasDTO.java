package com.sysacad.backend.dto;

import com.sysacad.backend.dto.EstudianteMateriaDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CarreraMateriasDTO {
    // Usamos String para IDs de carrera según tu entidad CarreraId
    private Integer nroCarrera;
    private String nombreCarrera;
    private String nombrePlan;
    private List<EstudianteMateriaDTO> materias;

    public CarreraMateriasDTO(Integer nroCarrera, String nombreCarrera, String nombrePlan,
            List<EstudianteMateriaDTO> materias) {
        this.nroCarrera = nroCarrera;
        this.nombreCarrera = nombreCarrera;
        this.nombrePlan = nombrePlan;
        this.materias = materias;
    }
}