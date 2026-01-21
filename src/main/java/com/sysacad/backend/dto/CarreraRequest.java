package com.sysacad.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class CarreraRequest {
    // Parte de la PK compuesta
    private UUID idFacultad;
    private Integer nroCarrera; // Ej: 1, 2
    private String alias; // Ej: "ISI", "IM"

    private String nombre; // Ej: "Ingeniería en Sistemas de Información"
}