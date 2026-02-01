package com.sysacad.backend.service;

import com.sysacad.backend.modelo.AsignacionMateria;
import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolCargo;
import com.sysacad.backend.modelo.enums.TipoMateria;
import com.sysacad.backend.exception.BusinessLogicException;
import com.sysacad.backend.exception.ResourceNotFoundException;
import com.sysacad.backend.repository.AsignacionMateriaRepository;
import com.sysacad.backend.repository.MateriaRepository;
import com.sysacad.backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MateriaService {

    private final MateriaRepository materiaRepository;
    private final AsignacionMateriaRepository asignacionMateriaRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public MateriaService(MateriaRepository materiaRepository,
                          AsignacionMateriaRepository asignacionMateriaRepository,
                          UsuarioRepository usuarioRepository) {
        this.materiaRepository = materiaRepository;
        this.asignacionMateriaRepository = asignacionMateriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Materia crearMateria(Materia materia) {

        if (materiaRepository.findByNombre(materia.getNombre()).isPresent()) {
            throw new BusinessLogicException("Ya existe una materia con ese nombre.");
        }
        return materiaRepository.save(materia);
    }

    @Transactional
    public Materia actualizarMateria(UUID id, Materia materiaNueva) {
        Materia materiaExistente = materiaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada con ID: " + id));

        validarPermisoEdicion(materiaExistente);

        materiaExistente.setNombre(materiaNueva.getNombre());
        materiaExistente.setDescripcion(materiaNueva.getDescripcion());
        materiaExistente.setTipoMateria(materiaNueva.getTipoMateria());
        materiaExistente.setDuracion(materiaNueva.getDuracion());
        materiaExistente.setCuatrimestreDictado(materiaNueva.getCuatrimestreDictado());
        materiaExistente.setHorasCursado(materiaNueva.getHorasCursado());
        materiaExistente.setRendirLibre(materiaNueva.getRendirLibre());
        materiaExistente.setOptativa(materiaNueva.getOptativa());

        return materiaRepository.save(materiaExistente);
    }

    @Transactional(readOnly = true)
    public List<Materia> listarTodas() {
        return materiaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Materia> buscarPorId(UUID id) {
        return materiaRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Materia> buscarPorNombre(String nombre) {
        return materiaRepository.findByNombreContainingIgnoreCase(nombre);
    }

    @Transactional(readOnly = true)
    public List<Materia> buscarPorTipo(TipoMateria tipo) {
        return materiaRepository.findByTipoMateria(tipo);
    }

    @Transactional
    public void eliminarMateria(UUID id) {
        materiaRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<com.sysacad.backend.dto.materia.CorrelativaArbolDTO> obtenerArbolCorrelativas(UUID idMateria, UUID idCarrera, Integer nroPlan) {
        Materia materia = materiaRepository.findById(idMateria)
                .orElseThrow(() -> new ResourceNotFoundException("Materia no encontrada con ID: " + idMateria));

        return construirArbol(materia, idCarrera, nroPlan);
    }

    private List<com.sysacad.backend.dto.materia.CorrelativaArbolDTO> construirArbol(Materia materia, UUID idCarrera, Integer nroPlan) {
        if (materia.getCorrelativas() == null) {
            return java.util.Collections.emptyList();
        }

        return materia.getCorrelativas().stream()
                .filter(c -> c.getPlan() != null &&
                             c.getPlan().getId().getIdCarrera().equals(idCarrera) &&
                             c.getPlan().getId().getNroPlan().equals(nroPlan))
                .map(c -> new com.sysacad.backend.dto.materia.CorrelativaArbolDTO(
                        c.getCorrelativa().getId(),
                        c.getCorrelativa().getNombre(),
                        c.getTipo().toString(),
                        construirArbol(c.getCorrelativa(), idCarrera, nroPlan)
                ))
                .collect(java.util.stream.Collectors.toList());
    }

    private void validarPermisoEdicion(Materia materia) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) return;

        String legajoUsuario = auth.getName(); // Asumiendo que el principal es el legajo
        Usuario profesor = usuarioRepository.findByLegajo(legajoUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado en base de datos"));

        List<AsignacionMateria> misJefaturas = asignacionMateriaRepository
                .findByIdIdUsuarioAndCargo(profesor.getId(), RolCargo.JEFE_CATEDRA);

        boolean esJefeDeEstaMateria = misJefaturas.stream()
                .anyMatch(asignacion -> asignacion.getMateria().getId().equals(materia.getId()));

        if (!esJefeDeEstaMateria) {
            throw new BusinessLogicException("Acceso denegado: Solo el Jefe de Cátedra puede editar esta materia.");
        }
    }
}