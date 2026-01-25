package com.sysacad.backend.dto.usuario;

import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.Genero;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.modelo.enums.TipoDocumento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class UsuarioResponse {
    private UUID id;
    private String legajo;
    private TipoDocumento tipoDocumento;
    private String dni;
    private String nombre;
    private String apellido;
    private String mail;
    private LocalDate fechaNacimiento;
    private Genero genero;
    private String telefono;
    private String direccion;
    private String ciudad;
    private String fotoPerfil;
    private LocalDate fechaIngreso;
    private String tituloAcademico;
    private RolUsuario rol;
    private String estado;

    private String tipoIdentificador;
    private Integer anioIngreso;

    private Boolean passwordChangeRequired;

    private List<InfoCarrera> carreras;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InfoCarrera {
        private String nombreCarrera;
        private String facultad;
    }
}