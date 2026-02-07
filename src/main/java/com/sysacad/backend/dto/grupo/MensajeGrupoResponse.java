package com.sysacad.backend.dto.grupo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MensajeGrupoResponse {
    private UUID id;
    private UUID idGrupo;
    private UUID idUsuarioRemitente;
    private String nombreRemitente;
    private String apellidoRemitente;
    private String fotoRemitente;
    private String contenido;
    private Boolean editado;
    private LocalDateTime fechaEnvio;
    private Boolean leido;
}
