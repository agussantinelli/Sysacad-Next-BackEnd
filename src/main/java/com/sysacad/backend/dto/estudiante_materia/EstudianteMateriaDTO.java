package com.sysacad.backend.dto.estudiante_materia;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstudianteMateriaDTO {
    private UUID idMateria;
    private String nombre;
    private Short nivel;
    private String estado; // "PENDIENTE", "CURSANDO", "REGULAR", "APROBADA"
    private String nota; // "8", "9.50", "-"
    private Boolean sePuedeInscribir;
    private Boolean esElectiva;
    private Short horasCursado;
    private String cuatrimestre;
    private java.util.List<String> correlativas;
}
