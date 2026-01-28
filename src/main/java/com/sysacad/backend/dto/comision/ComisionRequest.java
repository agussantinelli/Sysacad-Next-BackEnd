package com.sysacad.backend.dto.comision;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.List;

@Data
@NoArgsConstructor
public class ComisionRequest {

    private String nombre; // Ej: "Comisión 1K3"
    private String turno;
    private Integer anio;

    private UUID idSalon;

    private List<UUID> idsMaterias; 
    private List<UUID> idsProfesores;
}