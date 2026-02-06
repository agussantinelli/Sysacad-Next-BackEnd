package com.sysacad.backend.modelo;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.util.List;

@Entity
@Data
@Table(name = "comisiones")
public class Comision {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_salon", nullable = true)
    private Salon salon;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 20)
    private String turno;

    @Column(nullable = false)
    private Integer anio;

    @ManyToMany
    @JoinTable(
            name = "materias_comisiones",
            joinColumns = @JoinColumn(name = "id_comision"),
            inverseJoinColumns = @JoinColumn(name = "id_materia")
    )
    private List<Materia> materias;

    @ManyToMany
    @JoinTable(
            name = "profesores_comisiones",
            joinColumns = @JoinColumn(name = "id_comision"),
            inverseJoinColumns = @JoinColumn(name = "id_usuario")
    )
    private List<Usuario> profesores;
}