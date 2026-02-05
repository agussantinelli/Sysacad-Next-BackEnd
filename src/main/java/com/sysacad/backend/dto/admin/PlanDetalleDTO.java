package com.sysacad.backend.dto.admin;

import com.sysacad.backend.dto.materia.MateriaDetalleDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PlanDetalleDTO {
    private UUID carreraId;
    private Integer anio;
    private String nombre;
    private boolean esVigente;
    private List<MateriaDetalleDTO> materias;
}
