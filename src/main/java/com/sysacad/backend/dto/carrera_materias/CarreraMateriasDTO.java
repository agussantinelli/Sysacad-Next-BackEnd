package com.sysacad.backend.dto.carrera_materias;

import com.sysacad.backend.dto.estudiante_materia.EstudianteMateriaDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class CarreraMateriasDTO {
    private java.util.UUID idCarrera;
    private String nombreCarrera;
    private java.util.UUID idFacultad;
    private String nombreFacultad;
    private String nombrePlan;
    private List<EstudianteMateriaDTO> materias;

    public CarreraMateriasDTO(java.util.UUID idCarrera, String nombreCarrera, java.util.UUID idFacultad, String nombreFacultad, String nombrePlan,
            List<EstudianteMateriaDTO> materias) {
        this.idCarrera = idCarrera;
        this.nombreCarrera = nombreCarrera;
        this.idFacultad = idFacultad;
        this.nombreFacultad = nombreFacultad;
        this.nombrePlan = nombrePlan;
        this.materias = materias;
    }
}