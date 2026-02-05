package com.sysacad.backend.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfesorDisponibleDTO {
    private UUID id;
    private String nombre;
    private String apellido;
    private String legajo;
}
