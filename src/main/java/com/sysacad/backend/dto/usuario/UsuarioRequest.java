package com.sysacad.backend.dto.usuario;

import com.sysacad.backend.modelo.enums.Genero;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.modelo.enums.TipoDocumento;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class UsuarioRequest {
    private String legajo;
    private String password; // Solo entra, nunca sale en el Response
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
    private com.sysacad.backend.modelo.enums.EstadoUsuario estado;
}