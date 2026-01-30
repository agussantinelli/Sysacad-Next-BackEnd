package com.sysacad.backend.modelo;

import com.sysacad.backend.modelo.enums.TipoCorrelatividad;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "correlatividades")
public class Correlatividad implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_correlatividad")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_materia", nullable = false)
    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    private Materia materia;

    @ManyToOne
    @JoinColumn(name = "id_correlativa", nullable = false)
    @lombok.ToString.Exclude
    @lombok.EqualsAndHashCode.Exclude
    private Materia correlativa;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoCorrelatividad tipo;

    public Correlatividad(Materia materia, Materia correlativa, TipoCorrelatividad tipo) {
        this.materia = materia;
        this.correlativa = correlativa;
        this.tipo = tipo;
    }
}
