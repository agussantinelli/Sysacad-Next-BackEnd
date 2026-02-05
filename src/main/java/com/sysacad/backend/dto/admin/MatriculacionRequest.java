package com.sysacad.backend.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MatriculacionRequest {
    private UUID idUsuario;
    private UUID idFacultad;
    private UUID idCarrera;
    private Integer nroPlan;
}
