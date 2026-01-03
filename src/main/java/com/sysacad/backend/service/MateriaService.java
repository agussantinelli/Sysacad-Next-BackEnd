package com.sysacad.backend.service;

import com.sysacad.backend.modelo.Materia;
import com.sysacad.backend.modelo.enums.TipoMateria;
import com.sysacad.backend.repository.MateriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MateriaService {

    private final MateriaRepository materiaRepository;

    @Autowired
    public MateriaService(MateriaRepository materiaRepository) {
        this.materiaRepository = materiaRepository;
    }

    @Transactional
    public Materia crearMateria(Materia materia) {
        return materiaRepository.save(materia);
    }

    @Transactional
    public Materia actualizarMateria(UUID id, Materia materiaNueva) {
        return materiaRepository.findById(id).map(materiaExistente -> {
            materiaExistente.setNombre(materiaNueva.getNombre());
            materiaExistente.setDescripcion(materiaNueva.getDescripcion());
            materiaExistente.setTipoMateria(materiaNueva.getTipoMateria());
            materiaExistente.setDuracion(materiaNueva.getDuracion());
            materiaExistente.setHorasCursado(materiaNueva.getHorasCursado());
            materiaExistente.setRendirLibre(materiaNueva.getRendirLibre());
            materiaExistente.setOptativa(materiaNueva.getOptativa());
            return materiaRepository.save(materiaExistente);
        }).orElseThrow(() -> new RuntimeException("Materia no encontrada"));
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
}