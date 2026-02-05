package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.Matriculacion;
import com.sysacad.backend.modelo.Matriculacion.MatriculacionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MatriculacionRepository extends JpaRepository<Matriculacion, MatriculacionId> {
    
    List<Matriculacion> findByIdIdUsuario(UUID idUsuario);

    List<Matriculacion> findByUsuario_Id(UUID idUsuario);

    boolean existsByFacultad_Id(UUID idFacultad);

    long countByIdIdCarrera(UUID idCarrera);

    long countByFacultad_Id(UUID idFacultad);
}
