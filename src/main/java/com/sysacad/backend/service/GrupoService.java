package com.sysacad.backend.service;

import com.sysacad.backend.dto.grupo.GrupoRequest;
import com.sysacad.backend.dto.grupo.MensajeGrupoRequest;
import com.sysacad.backend.exception.BusinessLogicException;
import com.sysacad.backend.exception.ResourceNotFoundException;
import com.sysacad.backend.modelo.Grupo;
import com.sysacad.backend.modelo.MensajeGrupo;
import com.sysacad.backend.modelo.MiembroGrupo;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolGrupo;
import com.sysacad.backend.repository.GrupoRepository;
import com.sysacad.backend.repository.MensajeGrupoRepository;
import com.sysacad.backend.repository.MiembroGrupoRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import com.sysacad.backend.repository.ComisionRepository;
import com.sysacad.backend.repository.MateriaRepository;
import com.sysacad.backend.repository.AsignacionMateriaRepository;
import com.sysacad.backend.repository.InscripcionCursadoRepository;
import com.sysacad.backend.modelo.Comision;
import com.sysacad.backend.modelo.Materia;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GrupoService {

    private final GrupoRepository grupoRepository;
    private final MiembroGrupoRepository miembroGrupoRepository;
    private final MensajeGrupoRepository mensajeGrupoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ComisionRepository comisionRepository;
    private final MateriaRepository materiaRepository;
    private final AsignacionMateriaRepository asignacionMateriaRepository;
    private final InscripcionCursadoRepository inscripcionCursadoRepository;

    @Transactional
    public Grupo crearGrupo(GrupoRequest request, UUID idUsuarioCreador) {
        Grupo grupo = new Grupo();
        grupo.setNombre(request.getNombre());
        grupo.setDescripcion(request.getDescripcion());
        grupo.setTipo(request.getTipo());
        
        grupo = grupoRepository.save(grupo);

        // Agregar creador como administrador
        agregarMiembro(grupo.getId(), idUsuarioCreador, RolGrupo.ADMIN);
        
        // Agregar otros miembros si vienen en el request
        if (request.getIdsIntegrantes() != null) {
            for (UUID id : request.getIdsIntegrantes()) {
                if (!id.equals(idUsuarioCreador)) { // Evitar duplicar al creador
                    agregarMiembro(grupo.getId(), id, RolGrupo.MIEMBRO);
                }
            }
        }
        
        return grupo;
    }

    @Transactional
    public void agregarMiembro(UUID idGrupo, UUID idUsuario, RolGrupo rol) {
        Grupo grupo = grupoRepository.findById(idGrupo)
                .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado"));
        Usuario usuario = usuarioRepository.findById(idUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (miembroGrupoRepository.existsByGrupo_IdAndUsuario_Id(idGrupo, idUsuario)) {
            // Ya es miembro, no hacemos nada (o podríamos lanzar error)
            return;
        }

        MiembroGrupo miembro = new MiembroGrupo();
        miembro.setId(new MiembroGrupo.MiembroGrupoId(idGrupo, idUsuario));
        miembro.setGrupo(grupo);
        miembro.setUsuario(usuario);
        miembro.setRol(rol != null ? rol : RolGrupo.MIEMBRO);
        miembro.setUltimoAcceso(LocalDateTime.now());
        
        miembroGrupoRepository.save(miembro);
    }

    @Transactional
    public void eliminarMiembro(UUID idGrupo, UUID idUsuario) {
        MiembroGrupo.MiembroGrupoId id = new MiembroGrupo.MiembroGrupoId(idGrupo, idUsuario);
        if (!miembroGrupoRepository.existsById(id)) {
            throw new ResourceNotFoundException("El usuario no es miembro del grupo");
        }
        miembroGrupoRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Grupo> obtenerMisGrupos(UUID idUsuario) {
        return miembroGrupoRepository.findByUsuario_Id(idUsuario).stream()
                .map(MiembroGrupo::getGrupo)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Grupo obtenerPorId(UUID id) {
        return grupoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado con ID: " + id));
    }

    // Lógica Mensajes

    @Transactional
    public MensajeGrupo enviarMensaje(UUID idGrupo, MensajeGrupoRequest request, UUID idRemitente) {
        Grupo grupo;
        if (idGrupo == null) {
            if (request.getIdComision() == null || request.getIdMateria() == null) {
                throw new BusinessLogicException("Debe proporcionar el ID del grupo o IDs de comisión y materia");
            }
            grupo = grupoRepository.findByIdComisionAndIdMateria(request.getIdComision(), request.getIdMateria())
                    .orElseGet(() -> {
                        Comision comision = comisionRepository.findById(request.getIdComision())
                                .orElseThrow(() -> new ResourceNotFoundException("Comisión no encontrada"));
                        Materia materia = materiaRepository.findById(request.getIdMateria())
                                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada"));
                        
                        Grupo g = new Grupo();
                        g.setNombre(comision.getNombre() + " - " + materia.getNombre());
                        g.setIdComision(comision.getId());
                        g.setIdMateria(materia.getId());
                        g.setTipo("COMISION_MATERIA");
                        g = grupoRepository.save(g);
                        
                        poblarMiembrosAutomaticamente(g);
                        return g;
                    });
        } else {
            grupo = grupoRepository.findById(idGrupo)
                    .orElseThrow(() -> new ResourceNotFoundException("Grupo no encontrado"));
        }

        // Asegurar que el remitente sea ADMIN si es elegible y no lo es aún
        asegurarMiembroAdminSiEligible(grupo, idRemitente);

        // Autorización Estricta: Solo miembros con rol ADMIN pueden enviar mensajes
        MiembroGrupo miembro = miembroGrupoRepository.findById(new MiembroGrupo.MiembroGrupoId(grupo.getId(), idRemitente))
                .orElseThrow(() -> new BusinessLogicException("No tienes permiso para enviar mensajes en este grupo."));

        if (miembro.getRol() != RolGrupo.ADMIN) {
            throw new BusinessLogicException("Solo los administradores pueden enviar mensajes en este grupo.");
        }

        Usuario usuario = usuarioRepository.getReferenceById(idRemitente);

        MensajeGrupo mensaje = new MensajeGrupo();
        mensaje.setGrupo(grupo);
        mensaje.setUsuario(usuario);
        mensaje.setContenido(request.getContenido());
        
        return mensajeGrupoRepository.save(mensaje);
    }

    private void poblarMiembrosAutomaticamente(Grupo grupo) {
        if (grupo.getIdComision() == null || grupo.getIdMateria() == null) return;

        // 1. Agregar Jefe de Cátedra como ADMIN
        asignacionMateriaRepository.findByIdIdMateriaAndCargo(grupo.getIdMateria(), com.sysacad.backend.modelo.enums.RolCargo.JEFE_CATEDRA)
                .forEach(a -> agregarMiembro(grupo.getId(), a.getUsuario().getId(), RolGrupo.ADMIN));

        // 2. Agregar Profesores de la comisión como ADMIN
        comisionRepository.findById(grupo.getIdComision()).ifPresent(c -> {
            c.getProfesores().forEach(p -> agregarMiembro(grupo.getId(), p.getId(), RolGrupo.ADMIN));
        });

        // 3. Agregar Alumnos cursando como MIEMBRO
        inscripcionCursadoRepository.findByComisionIdAndMateriaIdAndEstado(
                grupo.getIdComision(), grupo.getIdMateria(), com.sysacad.backend.modelo.enums.EstadoCursada.CURSANDO)
                .forEach(i -> agregarMiembro(grupo.getId(), i.getUsuario().getId(), RolGrupo.MIEMBRO));
    }

    private void asegurarMiembroAdminSiEligible(Grupo grupo, UUID idUsuario) {
        if (grupo.getIdComision() == null || grupo.getIdMateria() == null) return;

        // Si ya es ADMIN, no hacer nada
        boolean yaEsAdmin = miembroGrupoRepository.findById(new MiembroGrupo.MiembroGrupoId(grupo.getId(), idUsuario))
                .map(m -> m.getRol() == RolGrupo.ADMIN).orElse(false);
        if (yaEsAdmin) return;

        // Verificar si es Jefe de Cátedra
        boolean esJefe = asignacionMateriaRepository.findByIdIdUsuarioAndCargo(idUsuario, com.sysacad.backend.modelo.enums.RolCargo.JEFE_CATEDRA)
                .stream().anyMatch(a -> a.getMateria().getId().equals(grupo.getIdMateria()));
        
        if (esJefe) {
            agregarMiembro(grupo.getId(), idUsuario, RolGrupo.ADMIN);
            return;
        }

        // Verificar si es profesor de la comisión
        boolean esProfesorComision = comisionRepository.findById(grupo.getIdComision())
                .map(c -> c.getProfesores().stream().anyMatch(p -> p.getId().equals(idUsuario))).orElse(false);

        if (esProfesorComision) {
            agregarMiembro(grupo.getId(), idUsuario, RolGrupo.ADMIN);
        }
    }

    @Transactional(readOnly = true)
    public Page<MensajeGrupo> obtenerMensajes(UUID idGrupo, Pageable pageable) {
         if (!grupoRepository.existsById(idGrupo)) {
             throw new ResourceNotFoundException("Grupo no encontrado");
         }
         return mensajeGrupoRepository.findByGrupoIdOrderByFechaEnvioDesc(idGrupo, pageable);
    }

    @Transactional
    public void marcarComoLeido(UUID idGrupo, UUID idUsuario) {
        MiembroGrupo.MiembroGrupoId id = new MiembroGrupo.MiembroGrupoId(idGrupo, idUsuario);
        MiembroGrupo miembro = miembroGrupoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no es miembro del grupo"));
        
        miembro.setUltimoAcceso(LocalDateTime.now());
        miembroGrupoRepository.save(miembro);
    }

    @Transactional(readOnly = true)
    public List<MiembroGrupo> obtenerMiembros(UUID idGrupo) {
        if (!grupoRepository.existsById(idGrupo)) {
            throw new ResourceNotFoundException("Grupo no encontrado");
        }
        return miembroGrupoRepository.findByGrupo_Id(idGrupo);
    }
}
