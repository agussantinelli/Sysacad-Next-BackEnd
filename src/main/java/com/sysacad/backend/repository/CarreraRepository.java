package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CarreraRepository extends JpaRepository<Carrera, UUID> {

    List<Carrera> findByFacultades_Id(UUID idFacultad);

    
    java.util.Optional<Carrera> findByAlias(String alias);

    boolean existsByNombre(String nombre);
    
    boolean existsByAlias(String alias);
}