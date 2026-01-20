package com.sysacad.backend.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class InscripcionCursadoRequest {
    private UUID idUsuario; // Optional if token is used
    private UUID idMateria;
    private UUID idComision;
}
