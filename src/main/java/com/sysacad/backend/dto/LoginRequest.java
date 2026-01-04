package com.sysacad.backend.dto;

import lombok.Data;

@Data
public class LoginRequest {
    // Puede ser Legajo O Email
    private String identificador;
    private String password;

    private String tipoIdentificador;
}