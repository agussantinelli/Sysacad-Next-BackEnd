package com.sysacad.backend.service;

import com.sysacad.backend.modelo.AsignacionMateria;
import com.sysacad.backend.modelo.Comision;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolCargo;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.exception.BusinessLogicException;
import com.sysacad.backend.exception.ResourceNotFoundException;
import com.sysacad.backend.repository.AsignacionMateriaRepository;
import com.sysacad.backend.repository.ComisionRepository;
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
public class ComisionService {

    private final ComisionRepository comisionRepository;
    private final AsignacionMateriaRepository asignacionMateriaRepository;
    private final UsuarioRepository usuarioRepository;

    @Autowired
    public ComisionService(ComisionRepository comisionRepository,
                           AsignacionMateriaRepository asignacionMateriaRepository,
                           UsuarioRepository usuarioRepository) {
        this.comisionRepository = comisionRepository;
        this.asignacionMateriaRepository = asignacionMateriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public Comision crearComision(Comision comision) {
        return comisionRepository.save(comision);
    }

    @Transactional
    public Comision asignarProfesor(UUID idComision, Usuario profesor) {
        // 1. Validar que el usuario a asignar sea PROFESOR
        // (Nota: Si viene solo el ID, habría que buscarlo primero en la BD)
        if (profesor.getRol() != RolUsuario.PROFESOR) {
            throw new BusinessLogicException("El usuario a asignar debe tener rol de PROFESOR");
        }

        Comision comision = comisionRepository.findById(idComision)
                .orElseThrow(() -> new ResourceNotFoundException("Comisión no encontrada con ID: " + idComision));

        // 2. VALIDACIÓN DE SEGURIDAD (Jefe de Cátedra)
        validarPermisoAsignacion(comision);

        comision.getProfesores().add(profesor);
        return comisionRepository.save(comision);
    }

    @Transactional(readOnly = true)
    public List<Comision> listarPorAnio(Integer anio) {
        return comisionRepository.findByAnio(anio);
    }

    @Transactional(readOnly = true)
    public Optional<Comision> buscarPorId(UUID id) {
        return comisionRepository.findById(id);
    }

    // --- SEGURIDAD: Validar si es Admin o Jefe de Cátedra vinculado ---
    private void validarPermisoAsignacion(Comision comision) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Si es ADMIN, pase libre.
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        if (isAdmin) return;

        // Si es PROFESOR, verificar si es JEFE de alguna materia de esta comisión
        String legajoUsuario = auth.getName();
        Usuario solicitante = usuarioRepository.findByLegajo(legajoUsuario)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));

        // Buscamos todas las materias donde el solicitante es JEFE
        List<AsignacionMateria> misJefaturas = asignacionMateriaRepository
                .findByIdIdUsuarioAndCargo(solicitante.getId(), RolCargo.JEFE_CATEDRA);

        // Verificamos intersección: ¿Alguna materia de la comisión está bajo su jefatura?
        boolean esJefeDeEstaComision = comision.getMaterias().stream()
                .anyMatch(materiaComision -> misJefaturas.stream()
                        .anyMatch(jefatura -> jefatura.getMateria().getId().equals(materiaComision.getId())));

        if (!esJefeDeEstaComision) {
            throw new BusinessLogicException("Acceso denegado: Solo un Admin o Jefe de Cátedra de las materias de esta comisión puede asignar profesores.");
        }
    }
}