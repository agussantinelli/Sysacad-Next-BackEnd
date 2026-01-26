package com.sysacad.backend.modelo;

import com.sysacad.backend.modelo.enums.EstadoAvisoPersona;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Entity
@Data
@Table(name = "avisos_personas")
public class AvisoPersona {

    @EmbeddedId
    private AvisoPersonaId id;

    @ManyToOne
    @MapsId("idAviso")
    @JoinColumn(name = "id_aviso")
    private Aviso aviso;

    @ManyToOne
    @MapsId("idPersona")
    @JoinColumn(name = "id_persona")
    private Usuario persona;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoAvisoPersona estado;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Embeddable
    public static class AvisoPersonaId implements Serializable {
        private UUID idAviso;
        private UUID idPersona;
    }
}
