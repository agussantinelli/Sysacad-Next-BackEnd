package com.sysacad.backend.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Data
@Table(name = "carreras")
public class Carrera {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_carrera")
    private UUID id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "alias", length = 20, nullable = false)
    private String alias;

    @Column(name = "horas_electivas_requeridas")
    private Integer horasElectivasRequeridas;

    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "facultades_carreras",
        joinColumns = @JoinColumn(name = "id_carrera"),
        inverseJoinColumns = @JoinColumn(name = "id_facultad")
    )
    private java.util.Set<FacultadRegional> facultades;
}