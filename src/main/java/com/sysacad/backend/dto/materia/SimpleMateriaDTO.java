package com.sysacad.backend.dto.materia;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimpleMateriaDTO {
    private UUID id;
    private String nombre;
}
