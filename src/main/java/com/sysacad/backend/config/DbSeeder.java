package com.sysacad.backend.config;

import com.sysacad.backend.modelo.*;
import com.sysacad.backend.modelo.enums.*;
import com.sysacad.backend.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Configuration
public class DbSeeder {

    @Bean
    @Transactional
    CommandLineRunner initDatabase(
            UTNSeeder utnSeeder, // Inyectamos nuestro nuevo seeder
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // 1. Ejecutar carga masiva de planes académicos
            utnSeeder.seed();

            // 2. Cargar Usuarios de Prueba si no existen
            if (usuarioRepository.count() == 0) {
                System.out.println(">> DbSeeder: Creando usuarios de prueba...");

                // ADMIN
                crearUsuario(usuarioRepository, passwordEncoder, "1", "Admin", "Sistema", "admin@sysacad.com",
                        RolUsuario.ADMIN);
                // PROFESOR
                crearUsuario(usuarioRepository, passwordEncoder, "DOC-2024", "Nicolas", "Cabello", "nic@sysacad.com",
                        RolUsuario.PROFESOR);
                // ESTUDIANTE
                crearUsuario(usuarioRepository, passwordEncoder, "45123", "Marty", "McFly", "marty@sysacad.com",
                        RolUsuario.ESTUDIANTE);

                System.out.println(">> DbSeeder: Usuarios creados.");
            }
        };
    }

    private void crearUsuario(UsuarioRepository repo, PasswordEncoder encoder, String legajo, String nombre,
            String apellido, String mail, RolUsuario rol) {
        Usuario u = new Usuario();
        u.setLegajo(legajo);
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setMail(mail);
        u.setDni(legajo + "000");
        u.setPassword(encoder.encode("1234"));
        u.setRol(rol);
        u.setTipoDocumento(TipoDocumento.DNI);
        u.setGenero(Genero.M);
        u.setEstado("ACTIVO");
        u.setFechaNacimiento(LocalDate.of(2000, 1, 1));
        u.setFechaIngreso(LocalDate.now());
        if (rol == RolUsuario.PROFESOR) {
            u.setTituloAcademico("Ingeniero");
        }
        repo.save(u);
    }
}