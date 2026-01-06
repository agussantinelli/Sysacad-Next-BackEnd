package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.Inscripcion;
import com.sysacad.backend.modelo.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface InscripcionRepository extends JpaRepository<Inscripcion, Inscripcion.InscripcionId> {

    List<Inscripcion> findByIdIdUsuario(UUID idUsuario);

    List<Inscripcion> findByIdIdComision(UUID idComision);

    @Query("SELECT DISTINCT i.usuario FROM Inscripcion i " +
            "JOIN i.comision c " +
            "JOIN c.materias m " +
            "WHERE m.id = :idMateria")
    List<Usuario> findAlumnosByMateria(@Param("idMateria") UUID idMateria);
}