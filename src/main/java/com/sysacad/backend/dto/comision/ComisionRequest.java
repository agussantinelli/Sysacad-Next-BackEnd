package com.sysacad.backend.dto.comision;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;
import java.util.List;

@Data
@NoArgsConstructor
public class ComisionRequest {
    // Datos básicos
    private String nombre; // Ej: "Comisión 1K3"
    private String turno;
    private Integer anio;

    // Referencias (Solo IDs)
    private UUID idSalon;

    // Relaciones (Solo IDs)
    private List<UUID> idsMaterias; // Materias que se dictan en esta comisión
    private List<UUID> idsProfesores; // Profesores asignados inicialmente
}