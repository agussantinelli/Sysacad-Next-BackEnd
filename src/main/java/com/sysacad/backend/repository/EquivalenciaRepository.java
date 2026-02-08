package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.Equivalencia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface EquivalenciaRepository extends JpaRepository<Equivalencia, UUID> {
    
    
    List<Equivalencia> findByIdCarreraOrigenAndNroPlanOrigenAndIdMateriaOrigen(
            UUID idCarreraOrigen, Integer nroPlanOrigen, UUID idMateriaOrigen);

    
    List<Equivalencia> findByIdCarreraDestinoAndNroPlanDestino(
            UUID idCarreraDestino, Integer nroPlanDestino);
}
