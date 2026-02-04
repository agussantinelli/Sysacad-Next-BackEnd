package com.sysacad.backend.config.seeder;

import com.sysacad.backend.modelo.Aviso;
import com.sysacad.backend.modelo.AvisoPersona;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.EstadoAviso;
import com.sysacad.backend.modelo.enums.EstadoAvisoPersona;
import com.sysacad.backend.repository.AvisoRepository;
import com.sysacad.backend.repository.AvisoPersonaRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AvisoSeeder {

    private final AvisoRepository avisoRepository;
    private final UsuarioRepository usuarioRepository;
    private final AvisoPersonaRepository avisoPersonaRepository;

    public void seed() {
        if (avisoRepository.count() == 0) {
            
            System.out.println(">> AvisoSeeder: Cargando avisos...");
            List<Usuario> todosLosUsuarios = usuarioRepository.findAll();

            // Aviso 1: Inscripciones
            crearAvisoConDestinatarios(
                "Inicio de Inscripciones 2026",
                "Se informa a la comunidad estudiantil que a partir del lunes 03 de Marzo comienzan las inscripciones a cursado anual y cuatrimestral. Recuerden verificar sus correlativas.",
                LocalDateTime.now().minusDays(2),
                EstadoAviso.ACTIVO,
                todosLosUsuarios
            );

            // Aviso 2: Asueto
            crearAvisoConDestinatarios(
                "Asueto Administrativo",
                "El día viernes 24 de Marzo la facultad permanecerá cerrada por feriado nacional.",
                LocalDateTime.now().minusDays(5),
                EstadoAviso.ACTIVO,
                todosLosUsuarios
            );

            // Aviso 3: Mantenimiento (Oculto/Viejo)
            crearAvisoConDestinatarios(
                "Mantenimiento del Sistema",
                "El sistema Sysacad estará fuera de línea este domingo por mantenimiento programado.",
                LocalDateTime.now().minusDays(10),
                EstadoAviso.OCULTO,
                todosLosUsuarios
            );
            
            System.out.println(">> AvisoSeeder: Avisos cargados y vinculados a " + todosLosUsuarios.size() + " usuarios.");
        }
    }

    private void crearAvisoConDestinatarios(String titulo, String descripcion, LocalDateTime fecha, EstadoAviso estado, List<Usuario> destinatarios) {
        Aviso aviso = new Aviso();
        aviso.setTitulo(titulo);
        aviso.setDescripcion(descripcion);
        aviso.setFechaEmision(fecha);
        aviso.setEstado(estado);
        aviso = avisoRepository.save(aviso);

        for (Usuario u : destinatarios) {
            AvisoPersona ap = new AvisoPersona();
            // ID compuesto
            ap.setId(new AvisoPersona.AvisoPersonaId(aviso.getId(), u.getId()));
            ap.setAviso(aviso);
            ap.setPersona(u);
            
            // Si es ADMIN, ya nace LEIDO (porque el admin los crea)
            if (u.getRol() == com.sysacad.backend.modelo.enums.RolUsuario.ADMIN) {
                ap.setEstado(EstadoAvisoPersona.LEIDO);
            } else {
                ap.setEstado(EstadoAvisoPersona.ENVIADO);
            }
            
            avisoPersonaRepository.save(ap);
        }
    }
}
