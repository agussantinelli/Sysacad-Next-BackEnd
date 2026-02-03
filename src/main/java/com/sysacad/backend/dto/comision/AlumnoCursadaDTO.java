package com.sysacad.backend.dto.comision;

import com.sysacad.backend.modelo.enums.EstadoCursada;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlumnoCursadaDTO {
    private UUID idInscripcion;
    private String nombre;
    private String apellido;
    private Long legajo;
    private EstadoCursada estado;
}
