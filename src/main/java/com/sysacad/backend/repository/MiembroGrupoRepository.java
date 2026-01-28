package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.MiembroGrupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MiembroGrupoRepository extends JpaRepository<MiembroGrupo, MiembroGrupo.MiembroGrupoId> {
    
    List<MiembroGrupo> findByUsuario_Id(UUID idUsuario);
    
    List<MiembroGrupo> findByGrupo_Id(UUID idGrupo);
    
    boolean existsByGrupo_IdAndUsuario_Id(UUID idGrupo, UUID idUsuario);
}
