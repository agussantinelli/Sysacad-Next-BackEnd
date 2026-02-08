package com.sysacad.backend.modelo;

import com.sysacad.backend.modelo.enums.TipoCorrelatividad;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "correlatividades")
public class Correlatividad implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_correlatividad")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_materia", nullable = false)
    private Materia materia;

    @ManyToOne
    @JoinColumn(name = "id_correlativa", nullable = false)
    private Materia correlativa;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoCorrelatividad tipo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "plan_id_carrera", referencedColumnName = "id_carrera"),
            @JoinColumn(name = "plan_nro_plan", referencedColumnName = "nro_plan")
    })
    private PlanDeEstudio plan;

    public Correlatividad(Materia materia, Materia correlativa, TipoCorrelatividad tipo, PlanDeEstudio plan) {
        this.materia = materia;
        this.correlativa = correlativa;
        this.tipo = tipo;
        this.plan = plan;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Correlatividad that = (Correlatividad) o;
        
        return Objects.equals(materia != null ? materia.getId() : null, that.materia != null ? that.materia.getId() : null) &&
               Objects.equals(correlativa != null ? correlativa.getId() : null, that.correlativa != null ? that.correlativa.getId() : null) &&
               tipo == that.tipo &&
               Objects.equals(plan != null ? plan.getId() : null, that.plan != null ? that.plan.getId() : null);
    }

    @Override
    public int hashCode() {
        
        return Objects.hash(
            materia != null ? materia.getId() : null,
            correlativa != null ? correlativa.getId() : null,
            tipo,
            plan != null ? plan.getId() : null
        );
    }

    @Override
    public String toString() {
        return "Correlatividad{" +
                "id=" + id +
                ", materia=" + (materia != null ? materia.getNombre() : "null") +
                ", correlativa=" + (correlativa != null ? correlativa.getNombre() : "null") +
                ", tipo=" + tipo +
                ", plan=" + (plan != null ? plan.getId() : "null") +
                '}';
    }
}
