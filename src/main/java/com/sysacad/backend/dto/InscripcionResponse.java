package com.sysacad.backend.dto;

import com.sysacad.backend.modelo.Inscripcion;
import com.sysacad.backend.modelo.enums.TipoInscripcion;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@NoArgsConstructor
public class InscripcionResponse {
    private String nombreMateria;
    private String comision;
    private Integer anioCursado;

    private TipoInscripcion tipo;
    private String fechaInscripcion;
    private String condicion; // REGULAR, APROBADO, ETC.
    private BigDecimal notaFinal;

    // IDs de referencia ocultos (útiles para el frontend)
    private UUID idMateria;
    private UUID idComision;

    public InscripcionResponse(Inscripcion inscripcion) {
        this.tipo = inscripcion.getId().getTipo();
        this.condicion = inscripcion.getCondicion();
        this.notaFinal = inscripcion.getNotaFinal();
        this.idComision = inscripcion.getId().getIdComision();

        if (inscripcion.getFechaInscripcion() != null) {
            this.fechaInscripcion = inscripcion.getFechaInscripcion()
                    .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
        }

        if (inscripcion.getComision() != null) {
            this.comision = inscripcion.getComision().getNombre();
            this.anioCursado = inscripcion.getComision().getAnio();

            // Acceso a materia a través de la comisión (asumiendo que tomamos la primera para el nombre)
            if (!inscripcion.getComision().getMaterias().isEmpty()) {
                this.nombreMateria = inscripcion.getComision().getMaterias().get(0).getNombre();
                this.idMateria = inscripcion.getComision().getMaterias().get(0).getId();
            }
        }
    }
}