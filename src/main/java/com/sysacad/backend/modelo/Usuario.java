package com.sysacad.backend.modelo;

import com.sysacad.backend.modelo.enums.*;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "usuarios", uniqueConstraints = {
        @UniqueConstraint(columnNames = "legajo"),
        @UniqueConstraint(columnNames = "mail"),
        @UniqueConstraint(columnNames = {"tipo_documento", "dni"})
})
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 20)
    private String legajo;

    @Column(nullable = false)
    private String password;

    @Column(name = "password_change_required", nullable = false)
    private Boolean passwordChangeRequired = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", nullable = false, length = 20)
    private TipoDocumento tipoDocumento;

    @Column(nullable = false, length = 15)
    private String dni;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false, length = 150)
    private String mail;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 1)
    private Genero genero;

    @Column(length = 50)
    private String telefono;

    @Column(length = 255)
    private String direccion;

    @Column(length = 100)
    private String ciudad;

    @Column(name = "foto_perfil", length = 255)
    private String fotoPerfil;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @Column(name = "titulo_academico", length = 100)
    private String tituloAcademico;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private RolUsuario rol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoUsuario estado;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override
    public String getUsername() {
        return legajo;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }
    @Override
    public boolean isAccountNonLocked() { return true; }
    @Override
    public boolean isCredentialsNonExpired() { return true; }
    @Override
    public boolean isEnabled() { return estado == EstadoUsuario.ACTIVO; }
}