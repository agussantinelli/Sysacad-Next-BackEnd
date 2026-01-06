package com.sysacad.backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FacultadRequest {
    private String ciudad;
    private String provincia;
}