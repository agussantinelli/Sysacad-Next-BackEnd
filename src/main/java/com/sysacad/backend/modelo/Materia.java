package com.sysacad.backend.modelo;

import com.sysacad.backend.modelo.enums.*;
import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Data
@Table(name = "materias")
public class Materia {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_materia", nullable = false, length = 20)
    private TipoMateria tipoMateria;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private DuracionMateria duracion;

    @Column(name = "horas_cursado", nullable = false)
    private Short horasCursado;

    @Column(name = "rendir_libre", nullable = false)
    private Boolean rendirLibre = false;

    @Column(nullable = false)
    private Boolean optativa = false;
}