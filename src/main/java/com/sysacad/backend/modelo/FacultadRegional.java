package com.sysacad.backend.modelo;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

@Entity
@Data
@Table(name = "facultades_regionales")
public class FacultadRegional {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 100)
    private String ciudad;

    @Column(nullable = false, length = 100)
    private String provincia;

    @ManyToMany(mappedBy = "facultades", fetch = FetchType.LAZY)
    private java.util.Set<Carrera> carreras;
}