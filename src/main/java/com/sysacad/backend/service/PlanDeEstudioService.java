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

@Service
public class PlanDeEstudioService {

    private final PlanDeEstudioRepository planRepository;
    private final PlanMateriaRepository planMateriaRepository;
    private final CarreraRepository carreraRepository;

    @Autowired
    public PlanDeEstudioService(PlanDeEstudioRepository planRepository,
                                PlanMateriaRepository planMateriaRepository,
                                CarreraRepository carreraRepository) {
        this.planRepository = planRepository;
        this.planMateriaRepository = planMateriaRepository;
        this.carreraRepository = carreraRepository;
    }

    @Transactional
    public PlanDeEstudio crearPlanDeEstudio(PlanDeEstudio plan) {
        // Validación: Verificar que la carrera padre exista antes de crear el plan
        Carrera.CarreraId carreraId = new Carrera.CarreraId(
                plan.getId().getIdFacultad(),
                plan.getId().getIdCarrera()
        );

        if (!carreraRepository.existsById(carreraId)) {
            throw new RuntimeException("No se puede crear el plan: La carrera especificada no existe.");
        }

        return planRepository.save(plan);
    }

    @Transactional(readOnly = true)
    public List<PlanDeEstudio> listarPlanesVigentes(String idCarrera) {
        return planRepository.findByIdIdCarreraAndEsVigenteTrue(idCarrera);
    }

    @Transactional(readOnly = true)
    public List<PlanDeEstudio> listarTodosPorCarrera(String idCarrera) {
        return planRepository.findByIdIdCarrera(idCarrera);
    }

    @Transactional
    public void agregarMateriaAPlan(PlanMateria planMateria) {
        // Validación: Verificar que el plan de estudios exista
        // Se usa la clase estática PlanId definida en la entidad PlanDeEstudio
        PlanDeEstudio.PlanId planId = new PlanDeEstudio.PlanId(
                planMateria.getId().getIdFacultad(),
                planMateria.getId().getIdCarrera(),
                planMateria.getId().getNombrePlan()
        );

        if (!planRepository.existsById(planId)) {
            throw new RuntimeException("El plan de estudios indicado no existe.");
        }

        planMateriaRepository.save(planMateria);
    }
}