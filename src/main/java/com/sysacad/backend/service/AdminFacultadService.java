package com.sysacad.backend.service;

import com.sysacad.backend.dto.admin.FacultadRequest;
import com.sysacad.backend.dto.facultad.FacultadResponse;
import com.sysacad.backend.modelo.FacultadRegional;
import com.sysacad.backend.repository.FacultadRegionalRepository;
import com.sysacad.backend.repository.MatriculacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminFacultadService {

    private final FacultadRegionalRepository facultadRepository;
    private final MatriculacionRepository matriculacionRepository;

    @Autowired
    public AdminFacultadService(FacultadRegionalRepository facultadRepository,
                                MatriculacionRepository matriculacionRepository) {
        this.facultadRepository = facultadRepository;
        this.matriculacionRepository = matriculacionRepository;
    }

    @Transactional(readOnly = true)
    public List<FacultadResponse> obtenerTodas() {
        return facultadRepository.findAll().stream()
                .map(facultad -> {
                    long matriculados = matriculacionRepository.countByFacultad_Id(facultad.getId());
                    List<String> carreras = facultad.getCarreras().stream()
                            .map(com.sysacad.backend.modelo.Carrera::getNombre)
                            .collect(Collectors.toList());
                    
                    return new FacultadResponse(facultad, matriculados, carreras);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void crearFacultad(FacultadRequest request) {
        if (facultadRepository.existsByCiudadAndProvincia(request.getCiudad(), request.getProvincia())) {
            throw new RuntimeException("Ya existe una facultad en " + request.getCiudad() + ", " + request.getProvincia());
        }
        
        FacultadRegional facultad = new FacultadRegional();
        facultad.setCiudad(request.getCiudad());
        facultad.setProvincia(request.getProvincia());
        facultadRepository.save(facultad);
    }

    @Transactional
    public void eliminarFacultad(UUID id) {
        // Validar si tiene inscripciones (matriculaciones)
        if (matriculacionRepository.existsByFacultad_Id(id)) {
            throw new RuntimeException("No se puede eliminar la facultad porque tiene inscripciones activas.");
        }
        
        if (!facultadRepository.existsById(id)) {
             throw new RuntimeException("Facultad no encontrada.");
        }

        facultadRepository.deleteById(id);
    }
}
