package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.EstudioUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EstudioUsuarioRepository extends JpaRepository<EstudioUsuario, EstudioUsuario.EstudioUsuarioId> {

    List<EstudioUsuario> findByIdIdUsuario(UUID idUsuario);

}