package com.sysacad.backend.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminMateriaComisionDTO {
    private UUID idMateria;
    private String nombreMateria;
    private Short nivel;
    private long cantidadInscriptos;
    private List<AlumnoResumenDTO> alumnos; 
    private List<ProfesorResumenDTO> profesores; 
}
