package com.sysacad.backend.dto.grupo;

import com.sysacad.backend.modelo.enums.RolGrupo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GrupoIntegranteDTO {
    private UUID idUsuario;
    private String nombre;
    private String apellido;
    private RolGrupo rol;
    private String foto;
}
