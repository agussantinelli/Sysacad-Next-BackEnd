package com.sysacad.backend.dto.admin;

import com.sysacad.backend.dto.plan.PlanDeEstudioResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarreraAdminDTO {
    private UUID id;
    private String nombre;
    private String alias;
    private long cantidadMatriculados;
    private List<PlanDeEstudioResponse> planes;
}
