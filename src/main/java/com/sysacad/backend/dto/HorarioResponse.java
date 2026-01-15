package com.sysacad.backend.dto;

import com.sysacad.backend.modelo.HorarioCursado;
import com.sysacad.backend.modelo.enums.DiaSemana;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;
import java.util.UUID;

@Data
@NoArgsConstructor
public class HorarioResponse {
    private UUID idComision;
    private String nombreComision;
    private UUID idMateria;
    private String nombreMateria;
    private DiaSemana dia;
    private LocalTime horaDesde;
    private LocalTime horaHasta;

    public HorarioResponse(HorarioCursado horario) {
        if (horario.getId() != null) {
            this.idComision = horario.getId().getIdComision();
            this.idMateria = horario.getId().getIdMateria();
            this.dia = horario.getId().getDia();
            this.horaDesde = horario.getId().getHoraDesde();
        }
        this.horaHasta = horario.getHoraHasta();

        if (horario.getComision() != null) {
            this.nombreComision = horario.getComision().getNombre() + " (" + horario.getComision().getAnio() + ")";
        }
        if (horario.getMateria() != null) {
            this.nombreMateria = horario.getMateria().getNombre();
        }
    }
}
