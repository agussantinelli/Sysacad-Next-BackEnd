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

    @EmbeddedId
    private CarreraId id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "alias", length = 20, nullable = false)
    private String alias;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_facultad", insertable = false, updatable = false)
    private FacultadRegional facultad;

    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CarreraId implements Serializable {
        @Column(name = "id_facultad")
        private UUID idFacultad;

        @Column(name = "id_carrera")
        private Integer idCarrera;
    }
}