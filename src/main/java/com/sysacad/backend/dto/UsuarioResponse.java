package com.sysacad.backend.dto;

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
    private LocalDate fechaIngreso; // Fecha de creación del usuario en sistema
    private String tituloAcademico;
    private RolUsuario rol;
    private String estado;

    private String tipoIdentificador;

    // NUEVO: Año de ingreso académico (Calculado de la inscripción más antigua)
    private Integer anioIngreso;

    private List<InfoCarrera> carreras;

    public UsuarioResponse(Usuario usuario) {
        this.id = usuario.getId();
        this.legajo = usuario.getLegajo();
        this.tipoDocumento = usuario.getTipoDocumento();
        this.dni = usuario.getDni();
        this.nombre = usuario.getNombre();
        this.apellido = usuario.getApellido();
        this.mail = usuario.getMail();
        this.fechaNacimiento = usuario.getFechaNacimiento();
        this.genero = usuario.getGenero();
        this.telefono = usuario.getTelefono();
        this.direccion = usuario.getDireccion();
        this.ciudad = usuario.getCiudad();
        this.fotoPerfil = usuario.getFotoPerfil();
        this.fechaIngreso = usuario.getFechaIngreso();
        this.tituloAcademico = usuario.getTituloAcademico();
        this.rol = usuario.getRol();
        this.estado = usuario.getEstado();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class InfoCarrera {
        private String nombreCarrera;
        private String facultad;
    }
}