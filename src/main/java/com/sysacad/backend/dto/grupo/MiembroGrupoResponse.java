package com.sysacad.backend.dto.grupo;

import com.sysacad.backend.modelo.enums.RolGrupo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MiembroGrupoResponse {
    private UUID idUsuario;
    private String nombre;
    private String apellido;
    private RolGrupo rol;
    private LocalDateTime fechaUnion;
    private LocalDateTime ultimoAcceso;
}
