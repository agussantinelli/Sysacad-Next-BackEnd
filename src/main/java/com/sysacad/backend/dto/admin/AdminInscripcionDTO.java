package com.sysacad.backend.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminInscripcionDTO {
    private UUID id;
    private String tipo; // "CURSADA" o "EXAMEN"
    private UUID idAlumno;
    private String nombre;
    private String apellido;
    private String fotoPerfil;
    private String legajoAlumno;
    private String nombreMateria;
    private String comision; // Nombre comisión (solo para Cursada)
    private LocalDateTime fechaInscripcion;
    private String estado;
}
