package com.sysacad.backend.modelo;

import com.sysacad.backend.modelo.enums.Genero;
import com.sysacad.backend.modelo.enums.TipoDocumento;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Data
@Table(name = "profesores", uniqueConstraints = {
        @UniqueConstraint(columnNames = "mail"),
        @UniqueConstraint(columnNames = {"tipo_documento", "dni"})
})
public class Profesor {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

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

    @Column(name = "titulo_academico", length = 100)
    private String tituloAcademico;

    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 1)
    private Genero genero;

    @Column(name = "fecha_ingreso", nullable = false)
    private LocalDate fechaIngreso;

    @Column(nullable = false, length = 20)
    private String estado;
}