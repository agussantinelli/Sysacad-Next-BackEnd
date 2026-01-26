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

    // --- Mensajes ---

    @Transactional
    public MensajeGrupo enviarMensaje(UUID idGrupo, MensajeGrupoRequest request, UUID idRemitente) {
        // Validar que el remitente sea miembro del grupo
        if (!miembroGrupoRepository.existsByGrupo_IdAndUsuario_Id(idGrupo, idRemitente)) {
            throw new BusinessLogicException("El remitente no pertenece al grupo");
        }

        Grupo grupo = grupoRepository.getReferenceById(idGrupo);
        Usuario usuario = usuarioRepository.getReferenceById(idRemitente);

        MensajeGrupo mensaje = new MensajeGrupo();
        mensaje.setGrupo(grupo);
        mensaje.setUsuario(usuario);
        mensaje.setContenido(request.getContenido());
        
        return mensajeGrupoRepository.save(mensaje);
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
