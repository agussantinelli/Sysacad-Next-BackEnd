package com.sysacad.backend.modelo;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "grupos")
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(length = 50)
    private String tipo;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private com.sysacad.backend.modelo.enums.EstadoGrupo estado = com.sysacad.backend.modelo.enums.EstadoGrupo.ACTIVO;

    @Column(name = "id_comision")
    private UUID idComision;

    @Column(name = "id_materia")
    private UUID idMateria;
}
