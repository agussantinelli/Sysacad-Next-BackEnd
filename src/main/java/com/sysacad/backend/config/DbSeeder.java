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
            UTNSeeder utnSeeder, // Inyectamos nuestro nuevo seeder masivo
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            utnSeeder.seed();

            // 2. Cargar Usuarios de Prueba si no existen
            if (usuarioRepository.count() == 0) {
                System.out.println(">> DbSeeder: Creando usuarios de prueba...");

                //  ADMIN
                Usuario admin = new Usuario();
                admin.setLegajo("1");
                admin.setNombre("Homero");
                admin.setApellido("Simpson");
                admin.setDni("11111111");
                admin.setPassword(passwordEncoder.encode("123456"));
                admin.setTipoDocumento(TipoDocumento.DNI);
                admin.setMail("admin@sysacad.com");
                admin.setRol(RolUsuario.ADMIN);
                admin.setGenero(Genero.M);
                admin.setEstado("ACTIVO");
                admin.setFechaNacimiento(LocalDate.of(1980, 1, 1));
                admin.setFechaIngreso(LocalDate.now());
                usuarioRepository.save(admin);

                //  PROFESOR
                Usuario profe = new Usuario();
                profe.setLegajo("51111");
                profe.setNombre("Nicolas");
                profe.setApellido("Cabello");
                profe.setDni("22222222");
                profe.setPassword(passwordEncoder.encode("123456"));
                profe.setTipoDocumento(TipoDocumento.DNI);
                profe.setMail("nic@sysacad.com");
                profe.setRol(RolUsuario.PROFESOR);
                profe.setTituloAcademico("Dr. en Matemáticas");
                profe.setGenero(Genero.M);
                profe.setEstado("ACTIVO");
                profe.setFechaNacimiento(LocalDate.of(1912, 6, 23));
                profe.setFechaIngreso(LocalDate.now());
                usuarioRepository.save(profe);

                // --- ESTUDIANTE ---
                Usuario alumno = new Usuario();
                alumno.setLegajo("55555");
                alumno.setNombre("Agustin");
                alumno.setApellido("Santinelli");
                alumno.setDni("33333333");
                alumno.setPassword(passwordEncoder.encode("123456"));
                alumno.setTipoDocumento(TipoDocumento.DNI);
                alumno.setMail("agus@sysacad.com");
                alumno.setRol(RolUsuario.ESTUDIANTE);
                alumno.setGenero(Genero.M);
                alumno.setEstado("ACTIVO");
                alumno.setFechaNacimiento(LocalDate.of(2004, 11, 17));
                alumno.setFechaIngreso(LocalDate.now());
                usuarioRepository.save(alumno);

                System.out.println(">> Seeding Completado (Con contraseñas encriptadas).");
            }
        };
    }
}