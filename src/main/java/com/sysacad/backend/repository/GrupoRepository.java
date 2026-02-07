package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.Grupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GrupoRepository extends JpaRepository<Grupo, UUID> {

    Optional<Grupo> findByIdComisionAndIdMateria(UUID idComision, UUID idMateria);

    List<Grupo> findByIdMateria(UUID idMateria);
}
