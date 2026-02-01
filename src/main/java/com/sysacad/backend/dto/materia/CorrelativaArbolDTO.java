package com.sysacad.backend.dto.materia;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CorrelativaArbolDTO {
    private UUID idMateria;
    private String nombre;
    private String tipoCorrelatividad;
    private List<CorrelativaArbolDTO> correlativas;
}
