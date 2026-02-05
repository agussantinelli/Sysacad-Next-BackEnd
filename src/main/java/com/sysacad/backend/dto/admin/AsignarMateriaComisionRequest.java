package com.sysacad.backend.dto.admin;

import com.sysacad.backend.modelo.enums.DiaSemana;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class AsignarMateriaComisionRequest {
    private UUID idMateria;
    private List<UUID> idsProfesores;
    private List<HorarioRequestDTO> horarios;

    @Data
    @NoArgsConstructor
    public static class HorarioRequestDTO {
        private DiaSemana dia;
        private LocalTime horaDesde;
        private LocalTime horaHasta;
    }
}
