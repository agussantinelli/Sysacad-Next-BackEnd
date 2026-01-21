package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CarreraRepository extends JpaRepository<Carrera, Carrera.CarreraId> {

    List<Carrera> findByIdIdFacultad(UUID idFacultad);

    // Búsqueda por alias (antiguo ID string)
    // Nota: El alias debería ser único por facultad, pero aquí buscamos global o
    // por facultad si fuera necesario
    // Por simplicidad buscamos global primero o por facultad y alias
    java.util.Optional<Carrera> findByIdIdFacultadAndAlias(UUID idFacultad, String alias);
}