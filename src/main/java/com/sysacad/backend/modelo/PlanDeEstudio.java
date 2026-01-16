package com.sysacad.backend.modelo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Data
@Table(name = "planes_de_estudios")
public class PlanDeEstudio {

        @EmbeddedId
        private PlanId id;

        @Column(name = "fecha_inicio", nullable = false)
        private LocalDate fechaInicio;

        @Column(name = "fecha_fin")
        private LocalDate fechaFin;

        @Column(name = "es_vigente", nullable = false)
        private Boolean esVigente = true;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumns({
                        @JoinColumn(name = "id_facultad", referencedColumnName = "id_facultad", insertable = false, updatable = false),
                        @JoinColumn(name = "id_carrera", referencedColumnName = "id_carrera", insertable = false, updatable = false)
        })
        private Carrera carrera;

        @OneToMany(mappedBy = "plan", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
        private List<PlanMateria> planMaterias = new ArrayList<>();

        public List<Materia> getMaterias() {
                if (planMaterias == null)
                        return new ArrayList<>();
                return planMaterias.stream()
                                .map(PlanMateria::getMateria)
                                .collect(Collectors.toList());
        }

        @Embeddable
        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class PlanId implements Serializable {
                @Column(name = "id_facultad")
                private UUID idFacultad;

                @Column(name = "id_carrera", length = 20)
                private String idCarrera;

                @Column(name = "nombre", length = 100)
                private String nombre;
        }
}