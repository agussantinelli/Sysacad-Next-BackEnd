package com.sysacad.backend.dto;

import com.sysacad.backend.modelo.Salon;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
public class SalonResponse {
    private UUID id;
    private UUID idFacultad;
    private String nombreFacultad;
    private String nombre;
    private String piso;

    public SalonResponse(Salon salon) {
        this.id = salon.getId();
        this.nombre = salon.getNombre();
        this.piso = salon.getPiso();
        if (salon.getFacultad() != null) {
            this.idFacultad = salon.getFacultad().getId();
            this.nombreFacultad = salon.getFacultad().getCiudad() + ", " + salon.getFacultad().getProvincia();
        }
    }
}
