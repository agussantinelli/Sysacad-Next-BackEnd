package com.sysacad.backend.modelo;

import com.sysacad.backend.modelo.enums.CuatrimestreDictado;
import com.sysacad.backend.modelo.enums.DuracionMateria;
import com.sysacad.backend.modelo.enums.ModalidadMateria;
import com.sysacad.backend.modelo.enums.TipoMateria;
import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

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

    @Enumerated(EnumType.STRING)
    @Column(name = "modalidad", nullable = false, length = 20)
    private ModalidadMateria modalidad = ModalidadMateria.PRESENCIAL; // Default en Java

    @Enumerated(EnumType.STRING)
    @Column(name = "cuatrimestre_dictado", length = 20)
    private CuatrimestreDictado cuatrimestreDictado;

    @Column(name = "horas_cursado", nullable = false)
    private Short horasCursado;

    @Column(name = "rendir_libre", nullable = false)
    private Boolean rendirLibre = false;

    @Column(nullable = false)
    private Boolean optativa = false;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "correlativas",
            joinColumns = @JoinColumn(name = "id_materia"),
            inverseJoinColumns = @JoinColumn(name = "id_correlativa")
    )
    private List<Materia> correlativas = new ArrayList<>();
}