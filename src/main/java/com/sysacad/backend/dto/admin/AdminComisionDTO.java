package com.sysacad.backend.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminComisionDTO {
    private UUID id;
    private String nombre;
    private String turno;
    private Integer anio;
    private Integer nivel;
    private UUID idCarrera;
    private String nombreCarrera;
    private String nombreSalon;
    private List<AdminMateriaComisionDTO> materias;
}
