package com.sysacad.backend.dto.comision;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;
import java.util.List;

@Data
@NoArgsConstructor
public class ComisionRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre; // Ej: "Comisión 1K3"
    
    @NotBlank(message = "El turno es obligatorio")
    private String turno;
    
    @NotNull(message = "El año es obligatorio")
    private Integer anio;

    private UUID idSalon;
    
    private String salon; // Nombre del salón (compatibilidad frontend)
    private UUID idCarrera; // Compatibilidad frontend

    private List<UUID> idsMaterias; 
    private List<UUID> idsProfesores;
}