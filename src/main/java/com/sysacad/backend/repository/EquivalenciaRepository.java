package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.Equivalencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface EquivalenciaRepository extends JpaRepository<Equivalencia, UUID> {
    
    // Buscar reglas aplicables a una materia de origen
    List<Equivalencia> findByIdCarreraOrigenAndNroPlanOrigenAndIdMateriaOrigen(
            UUID idCarreraOrigen, Integer nroPlanOrigen, UUID idMateriaOrigen);

    // Buscar reglas aplicables a un plan de destino específico
    List<Equivalencia> findByIdCarreraDestinoAndNroPlanDestino(
            UUID idCarreraDestino, Integer nroPlanDestino);
}
