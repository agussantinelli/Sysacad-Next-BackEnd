package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.MiembroGrupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MiembroGrupoRepository extends JpaRepository<MiembroGrupo, MiembroGrupo.MiembroGrupoId> {
    List<MiembroGrupo> findByIdUsuario(UUID idUsuario);
    List<MiembroGrupo> findByIdGrupo(UUID idGrupo);
    boolean existsByIdGrupoAndIdUsuario(UUID idGrupo, UUID idUsuario);
}
