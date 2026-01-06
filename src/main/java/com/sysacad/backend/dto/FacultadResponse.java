package com.sysacad.backend.dto;

import com.sysacad.backend.modelo.FacultadRegional;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class FacultadResponse {
    private UUID id;
    private String ciudad;
    private String provincia;
    private String nombreCompleto;

    public FacultadResponse(FacultadRegional facultad) {
        this.id = facultad.getId();
        this.ciudad = facultad.getCiudad();
        this.provincia = facultad.getProvincia();
        this.nombreCompleto = "UTN - " + facultad.getCiudad();
    }
}