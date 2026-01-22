package com.sysacad.backend.dto;

import com.sysacad.backend.modelo.Carrera;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class CarreraResponse {
    private UUID id;
    private String alias;
    private String nombre;
    private java.util.List<String> facultades;

    public CarreraResponse(Carrera carrera) {
        this.id = carrera.getId();
        this.alias = carrera.getAlias();
        this.nombre = carrera.getNombre();

        if (carrera.getFacultades() != null) {
            this.facultades = carrera.getFacultades().stream()
                    .map(com.sysacad.backend.modelo.FacultadRegional::getCiudad)
                    .collect(java.util.stream.Collectors.toList());
        }
    }
}