package com.sysacad.backend.dto.materia;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MateriaDetalleDTO {
    private UUID id;
    private String nombre;
    private String codigo;
    private Integer nivel;
    private Short horasCursado;
    private String tipoMateria;
    private List<String> correlativas; // List of names or codes
}
