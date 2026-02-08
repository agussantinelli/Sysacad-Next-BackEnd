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
public class AdminInscripcionRequest {
    private UUID idAlumno;
    private String tipo; 
    private UUID idReferencia; 
    private UUID idMateria; 
    private Integer nroDetalle; 
}