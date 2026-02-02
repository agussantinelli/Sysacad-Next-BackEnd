package com.sysacad.backend.dto.usuario;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CambioPasswordRequest {
    private String passwordActual;
    private String passwordNueva;
}
