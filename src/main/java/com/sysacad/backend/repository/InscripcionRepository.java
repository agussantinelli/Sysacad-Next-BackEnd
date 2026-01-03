package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.Inscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Inscripcion.InscripcionId> {

    List<Inscripcion> findByIdIdUsuario(UUID idUsuario);

    List<Inscripcion> findByIdIdComision(UUID idComision);
}