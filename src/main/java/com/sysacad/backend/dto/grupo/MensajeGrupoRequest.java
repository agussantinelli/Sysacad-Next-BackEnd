package com.sysacad.backend.dto.grupo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class MensajeGrupoRequest {
    private String contenido;
    private UUID idUsuarioRemitente; // Opcional si se toma del token
}
