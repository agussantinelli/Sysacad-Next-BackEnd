package com.sysacad.backend.repository;

import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByLegajoOrMail(String legajo, String mail);

    Optional<Usuario> findByLegajo(String legajo);

    Optional<Usuario> findByMail(String mail);

    Optional<Usuario> findByDni(String dni);

    List<Usuario> findByRol(RolUsuario rol);

    List<Usuario> findByApellidoContainingIgnoreCase(String apellido);

    boolean existsByMail(String mail);
    
    boolean existsByLegajo(String legajo);
}