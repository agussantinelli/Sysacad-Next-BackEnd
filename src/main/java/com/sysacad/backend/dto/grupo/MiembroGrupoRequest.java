package com.sysacad.backend.dto.grupo;

import com.sysacad.backend.modelo.enums.RolGrupo;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class MiembroGrupoRequest {
    private UUID idUsuario;
    private RolGrupo rol; // Opcional
}
