package com.sysacad.backend.dto.inscripcion_cursado;

import lombok.Data;
import java.util.UUID;

@Data
public class InscripcionCursadoRequest {
    private UUID idUsuario; 
    private UUID idMateria;
    private UUID idComision;
}
