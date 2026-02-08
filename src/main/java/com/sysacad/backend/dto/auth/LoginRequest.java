package com.sysacad.backend.dto.auth;

import lombok.Data;

@Data
public class LoginRequest {
    
    private String identificador;
    private String password;

    private String tipoIdentificador;
}