package com.sysacad.backend.dto.salon;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@NoArgsConstructor
public class SalonRequest {
    private UUID idFacultad;
    private String nombre;
    private String piso;
}
