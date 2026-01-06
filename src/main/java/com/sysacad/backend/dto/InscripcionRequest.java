package com.sysacad.backend.dto;

import com.sysacad.backend.modelo.enums.TipoInscripcion;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class InscripcionRequest {
    // Necesarios para armar la Composite Key (InscripcionId)
    private UUID idUsuario;
    private UUID idComision;
    private TipoInscripcion tipo; // CURSADO o EXAMEN

    // Opcional: si no se envía, el backend asume 1
    private Integer vecesTipo = 1;

    private String condicion; // "REGULAR", "LIBRE" (Inicial)
}