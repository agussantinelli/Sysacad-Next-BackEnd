package com.sysacad.backend.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MateriaDisponibleDTO {
    private UUID id;
    private String nombre;
    private String codigo;
    private Short nivel;
    private Short horasCursado;
    private String cuatrimestreDictado;
    private Boolean esElectiva;
}
