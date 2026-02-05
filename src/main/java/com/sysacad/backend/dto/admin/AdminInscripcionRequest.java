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
    private String tipo; // "CURSADA" o "EXAMEN"
    private UUID idReferencia; // ID of Comision or MesaExamen
    private UUID idMateria; // Required for Cursada (to pick which subject in the commission) and validation
    private Integer nroDetalle; // Required for Examen (composite key part)
}