package com.sysacad.backend.dto;

import com.sysacad.backend.dto.EstudianteMateriaDTO;
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
    private List<EstudianteMateriaDTO> materias;

    public CarreraMateriasDTO(String idCarrera, String nombreCarrera, String nombrePlan,
            List<EstudianteMateriaDTO> materias) {
        this.idCarrera = idCarrera;
        this.nombreCarrera = nombreCarrera;
        this.nombrePlan = nombrePlan;
        this.materias = materias;
    }
}