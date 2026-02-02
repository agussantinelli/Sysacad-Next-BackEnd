package com.sysacad.backend.config.seeder;

import com.sysacad.backend.modelo.Aviso;
import com.sysacad.backend.modelo.enums.EstadoAviso;
import com.sysacad.backend.repository.AvisoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class AvisoSeeder {

    private final AvisoRepository avisoRepository;

    public void seed() {
        if (avisoRepository.count() == 0) {
            
            // Aviso 1: Inscripciones
            Aviso aviso1 = new Aviso();
            aviso1.setTitulo("Inicio de Inscripciones 2026");
            aviso1.setDescripcion("Se informa a la comunidad estudiantil que a partir del lunes 03 de Marzo comienzan las inscripciones a cursado anual y cuatrimestral. Recuerden verificar sus correlativas.");
            aviso1.setFechaEmision(LocalDateTime.now().minusDays(2));
            aviso1.setEstado(EstadoAviso.ACTIVO);
            avisoRepository.save(aviso1);

            // Aviso 2: Asueto
            Aviso aviso2 = new Aviso();
            aviso2.setTitulo("Asueto Administrativo");
            aviso2.setDescripcion("El día viernes 24 de Marzo la facultad permanecerá cerrada por feriado nacional.");
            aviso2.setFechaEmision(LocalDateTime.now().minusDays(5));
            aviso2.setEstado(EstadoAviso.ACTIVO);
            avisoRepository.save(aviso2);

            // Aviso 3: Mantenimiento (Oculto/Viejo) -> Si queremos probar filtrado
            Aviso aviso3 = new Aviso();
            aviso3.setTitulo("Mantenimiento del Sistema");
            aviso3.setDescripcion("El sistema Sysacad estará fuera de línea este domingo por mantenimiento programado.");
            aviso3.setFechaEmision(LocalDateTime.now().minusDays(10));
            aviso3.setEstado(EstadoAviso.OCULTO);
            avisoRepository.save(aviso3);
            
            System.out.println(">> AvisoSeeder: Avisos cargados.");
        }
    }
}
