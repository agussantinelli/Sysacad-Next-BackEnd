package com.sysacad.backend.dto;

import com.sysacad.backend.modelo.Carrera;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
public class CarreraResponse {
    private UUID idFacultad;
    private Integer nroCarrera;
    private String alias;
    private String nombre;
    private String nombreFacultad;

    public CarreraResponse(Carrera carrera) {
        this.idFacultad = carrera.getId().getIdFacultad();
        this.nroCarrera = carrera.getId().getNroCarrera();
        this.alias = carrera.getAlias();
        this.nombre = carrera.getNombre();

        if (carrera.getFacultad() != null) {
            this.nombreFacultad = carrera.getFacultad().getCiudad();
        }
    }
}