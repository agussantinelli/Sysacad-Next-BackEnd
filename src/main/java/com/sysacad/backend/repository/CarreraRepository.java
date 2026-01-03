package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.Carrera;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CarreraRepository extends JpaRepository<Carrera, Carrera.CarreraId> {

    List<Carrera> findByIdIdFacultad(UUID idFacultad);
}