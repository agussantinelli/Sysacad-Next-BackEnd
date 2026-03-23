package com.sysacad.backend.dto.admin;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FacultadRequest {
    @jakarta.validation.constraints.NotBlank(message = "La ciudad es requerida")
    private String ciudad;

    @jakarta.validation.constraints.NotBlank(message = "La provincia es requerida")
    private String provincia;
}
