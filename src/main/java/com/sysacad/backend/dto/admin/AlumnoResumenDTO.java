package com.sysacad.backend.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AlumnoResumenDTO {
    private UUID idUsuario;
    private String nombre;
    private String apellido;
    private String legajo;
}
