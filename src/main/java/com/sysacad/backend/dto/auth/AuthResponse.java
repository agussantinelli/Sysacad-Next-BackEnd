package com.sysacad.backend.dto.auth;

import com.sysacad.backend.dto.usuario.UsuarioResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String token;
    private UsuarioResponse usuario;
}