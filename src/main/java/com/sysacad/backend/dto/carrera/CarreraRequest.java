package com.sysacad.backend.dto.carrera;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class CarreraRequest {
    private java.util.UUID idFacultad;
    private String alias; 
    private String nombre; 
}