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
    private String nombre; 
    
    @NotBlank(message = "El turno es obligatorio")
    private String turno;
    
    @NotNull(message = "El año es obligatorio")
    private Integer anio;

    @NotNull(message = "El nivel es obligatorio")
    private Integer nivel;

    @NotNull(message = "La carrera es obligatoria")
    private UUID idCarrera;


    private UUID idSalon;
    
    private String salon; 

    private List<UUID> idsMaterias; 
    private List<UUID> idsProfesores;
}