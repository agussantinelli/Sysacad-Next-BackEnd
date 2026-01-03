package com.sysacad.backend.service;

import com.sysacad.backend.modelo.Carrera;
import com.sysacad.backend.modelo.PlanDeEstudio;
import com.sysacad.backend.modelo.PlanMateria;
import com.sysacad.backend.repository.CarreraRepository;
import com.sysacad.backend.repository.PlanDeEstudioRepository;
import com.sysacad.backend.repository.PlanMateriaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class CarreraService {

    private final CarreraRepository carreraRepository;
    private final PlanDeEstudioRepository planRepository;
    private final PlanMateriaRepository planMateriaRepository;

    @Autowired
    public CarreraService(CarreraRepository carreraRepository,
                          PlanDeEstudioRepository planRepository,
                          PlanMateriaRepository planMateriaRepository) {
        this.carreraRepository = carreraRepository;
        this.planRepository = planRepository;
        this.planMateriaRepository = planMateriaRepository;
    }

    @Transactional
    public Carrera registrarCarrera(Carrera carrera) {
        return carreraRepository.save(carrera);
    }

    @Transactional(readOnly = true)
    public List<Carrera> listarCarrerasPorFacultad(UUID idFacultad) {
        return carreraRepository.findByIdIdFacultad(idFacultad);
    }

    @Transactional
    public PlanDeEstudio crearPlanDeEstudio(PlanDeEstudio plan) {
        return planRepository.save(plan);
    }

    @Transactional
    public void agregarMateriaAPlan(PlanMateria planMateria) {
        planMateriaRepository.save(planMateria);
    }

    @Transactional(readOnly = true)
    public List<PlanDeEstudio> listarPlanesVigentes(String idCarrera) {
        return planRepository.findByIdIdCarreraAndEsVigenteTrue(idCarrera);
    }
}