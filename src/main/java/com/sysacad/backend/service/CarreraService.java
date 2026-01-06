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

    // --- LÓGICA DE CARRERAS ---

    @Transactional
    public Carrera registrarCarrera(Carrera carrera) {
        // Validación: Evitar duplicados lógicos antes de ir a la BD
        if (carreraRepository.existsById(carrera.getId())) {
            throw new RuntimeException("La carrera ya existe en esta facultad.");
        }
        return carreraRepository.save(carrera);
    }

    @Transactional(readOnly = true)
    public List<Carrera> listarCarrerasPorFacultad(UUID idFacultad) {
        return carreraRepository.findByIdIdFacultad(idFacultad);
    }

    // --- LÓGICA DE PLANES ---

    @Transactional
    public PlanDeEstudio crearPlanDeEstudio(PlanDeEstudio plan) {
        // Validación CRÍTICA: Verificar que la carrera padre exista.
        // Reconstruimos el ID de la carrera desde los datos del plan.
        Carrera.CarreraId carreraId = new Carrera.CarreraId(plan.getId().getIdFacultad(), plan.getId().getIdCarrera());

        if (!carreraRepository.existsById(carreraId)) {
            throw new RuntimeException("No se puede crear el plan: La carrera especificada no existe.");
        }

        return planRepository.save(plan);
    }

    @Transactional(readOnly = true)
    public List<PlanDeEstudio> listarPlanesVigentes(String idCarrera) {
        return planRepository.findByIdIdCarreraAndEsVigenteTrue(idCarrera);
    }

    // --- LÓGICA DE MATERIAS DEL PLAN ---

    @Transactional
    public void agregarMateriaAPlan(PlanMateria planMateria) {
        // Validación: Verificar que el plan de estudios exista
        PlanDeEstudio.PlanDeEstudioId planId = new PlanDeEstudio.PlanDeEstudioId(
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