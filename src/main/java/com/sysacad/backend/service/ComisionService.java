package com.sysacad.backend.service;

import com.sysacad.backend.modelo.Comision;
import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.repository.ComisionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ComisionService {

    private final ComisionRepository comisionRepository;

    @Autowired
    public ComisionService(ComisionRepository comisionRepository) {
        this.comisionRepository = comisionRepository;
    }

    @Transactional
    public Comision crearComision(Comision comision) {
        return comisionRepository.save(comision);
    }

    @Transactional
    public Comision asignarProfesor(UUID idComision, Usuario profesor) {
        if (profesor.getRol() != RolUsuario.PROFESOR) {
            throw new RuntimeException("El usuario debe tener rol de PROFESOR");
        }

        Comision comision = comisionRepository.findById(idComision)
                .orElseThrow(() -> new RuntimeException("Comisión no encontrada"));

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
}