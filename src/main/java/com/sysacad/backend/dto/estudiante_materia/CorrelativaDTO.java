package com.sysacad.backend.dto.estudiante_materia;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CorrelativaDTO {
    private String nombre;
    private String condicion; // "REGULAR" o "PROMOCIONADA" (que implica aprobada)
}
