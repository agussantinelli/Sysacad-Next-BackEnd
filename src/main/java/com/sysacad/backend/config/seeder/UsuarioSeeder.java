package com.sysacad.backend.config.seeder;

import com.sysacad.backend.modelo.Usuario;
import com.sysacad.backend.modelo.enums.Genero;
import com.sysacad.backend.modelo.enums.RolUsuario;
import com.sysacad.backend.modelo.enums.TipoDocumento;
import com.sysacad.backend.repository.UsuarioRepository;
import com.sysacad.backend.service.FileStorageService;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
public class UsuarioSeeder {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final FileStorageService fileStorageService;

    public UsuarioSeeder(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder, FileStorageService fileStorageService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.fileStorageService = fileStorageService;
    }

    @Transactional
    public void seed() {
            // Verificar si ya hay usuarios cargados
            if (usuarioRepository.count() > 0) {
                System.out.println(">> UsuarioSeeder: OMITIDO - Los usuarios ya están cargados.");
                return;
            }

            System.out.println(">> UsuarioSeeder: Iniciando carga de usuarios...");
            
            // Limpiar imágenes anteriores
            // fileStorageService.deleteAllPerfiles(); // Comentado para evitar borrar fotos en cada reinicio si cambiamos estrategia

            // ADMIN
            createUsuarioIfNotExists("1", "Homero", "Simpson", "11111111",
                    "admin@sysacad.com", RolUsuario.ADMIN, Genero.M, "Rector",
                    LocalDate.of(1980, 5, 12), "uploads/perfiles/seeded-1.jpg");

            // PROFESORES
            createUsuarioIfNotExists("51111", "Dario", "Cvitanich", "22222222", "dario@sysacad.com", RolUsuario.PROFESOR, Genero.M, "Doctor en Ciencias de la Computación", LocalDate.of(1990, 6, 23), "uploads/perfiles/seeded-51111.jpg");
            createUsuarioIfNotExists("52222", "Laura", "Gomez", "22222223", "laura@sysacad.com", RolUsuario.PROFESOR, Genero.F, "Ingeniera en Sistemas", LocalDate.of(1985, 3, 15), null);
            createUsuarioIfNotExists("53333", "Roberto", "Diaz", "22222224", "roberto@sysacad.com", RolUsuario.PROFESOR, Genero.M, "Licenciado en Física", LocalDate.of(1978, 9, 10), null);
            createUsuarioIfNotExists("54444", "Ana", "Martinez", "22222225", "ana@sysacad.com", RolUsuario.PROFESOR, Genero.F, "Traductora Pública", LocalDate.of(1982, 11, 30), null);
            createUsuarioIfNotExists("55551", "Sandra", "Civiero", "22222226", "sandra@sysacad.com", RolUsuario.PROFESOR, Genero.F, "Matemática", LocalDate.of(1975, 4, 12), null);
            createUsuarioIfNotExists("55552", "Cristian", "Milone", "22222227", "cristian@sysacad.com", RolUsuario.PROFESOR, Genero.M, "Ingeniero Electrónico", LocalDate.of(1988, 8, 25), null);
            createUsuarioIfNotExists("55553", "Gustavo", "Perez", "22222228", "gustavo@sysacad.com", RolUsuario.PROFESOR, Genero.M, "Ingeniero en Sistemas", LocalDate.of(1980, 2, 20), null);
            createUsuarioIfNotExists("55554", "Claudia", "Lopez", "22222229", "claudia@sysacad.com", RolUsuario.PROFESOR, Genero.F, "Licenciada en Educación", LocalDate.of(1975, 5, 15), null);
            createUsuarioIfNotExists("55556", "Jorge", "Garcia", "22222230", "jorge@sysacad.com", RolUsuario.PROFESOR, Genero.M, "Abogado", LocalDate.of(1970, 10, 10), null);
            createUsuarioIfNotExists("55557", "Valeria", "Martinez", "22222231", "valeria@sysacad.com", RolUsuario.PROFESOR, Genero.F, "Contadora", LocalDate.of(1983, 12, 12), null);

            // ESTUDIANTES
            createUsuarioIfNotExists("55555", "Agustin", "Santinelli", "33333333", "agustinsantinelli@gmail.com", RolUsuario.ESTUDIANTE, Genero.M, null, LocalDate.of(2004, 11, 17), "uploads/perfiles/seeded-55555.jpg");
            createUsuarioIfNotExists("56666", "Maria", "Rodriguez", "33333334", "maria@sysacad.com", RolUsuario.ESTUDIANTE, Genero.F, null, LocalDate.of(2003, 5, 20), "uploads/perfiles/seeded-56666.jpg");
            createUsuarioIfNotExists("57777", "Juan", "Perez", "33333335", "juan@sysacad.com", RolUsuario.ESTUDIANTE, Genero.M, null, LocalDate.of(2004, 1, 10), null);
            createUsuarioIfNotExists("58888", "Sofia", "Lopez", "33333336", "sofia@sysacad.com", RolUsuario.ESTUDIANTE, Genero.F, null, LocalDate.of(2001, 7, 8), null);
            createUsuarioIfNotExists("59999", "Miguel", "Torres", "33333337", "miguel@sysacad.com", RolUsuario.ESTUDIANTE, Genero.M, null, LocalDate.of(2004, 3, 30), null);
            createUsuarioIfNotExists("60001", "Lucia", "Fernandez", "33333338", "lucia@sysacad.com", RolUsuario.ESTUDIANTE, Genero.F, null, LocalDate.of(2002, 12, 12), null);
            createUsuarioIfNotExists("60002", "Carlos Alberto", "Tevez Martinez", "33333339", "carlos@sysacad.com", RolUsuario.ESTUDIANTE, Genero.M, null, LocalDate.of(2003, 2, 5), null);
            createUsuarioIfNotExists("60003", "Martin", "Palermo", "33333340", "martin@sysacad.com", RolUsuario.ESTUDIANTE, Genero.M, null, LocalDate.of(2003, 11, 7), null);
            createUsuarioIfNotExists("60004", "Flavia", "Avara", "33333341", "flavia@sysacad.com", RolUsuario.ESTUDIANTE, Genero.F, null, LocalDate.of(2004, 11, 24), null);
            createUsuarioIfNotExists("60010", "Pedro", "Pascal", "33333350", "pedro@sysacad.com", RolUsuario.ESTUDIANTE, Genero.M, null, LocalDate.of(2003, 4, 2), null);
            createUsuarioIfNotExists("60011", "Lionel", "Messi", "33333351", "lio@sysacad.com", RolUsuario.ESTUDIANTE, Genero.M, null, LocalDate.of(1987, 6, 24), null);
            createUsuarioIfNotExists("60012", "Alex", "Morgan", "33333352", "alex@sysacad.com", RolUsuario.ESTUDIANTE, Genero.F, null, LocalDate.of(2002, 5, 3), null);
            createUsuarioIfNotExists("60013", "Diego", "Maradona", "33333353", "diego@sysacad.com", RolUsuario.ESTUDIANTE, Genero.M, null, LocalDate.of(1960, 10, 30), null);
            createUsuarioIfNotExists("60014", "Enzo", "Fernandez", "33333354", "enzo@sysacad.com", RolUsuario.ESTUDIANTE, Genero.M, null, LocalDate.of(2001, 1, 17), null);

            System.out.println(">> Usuarios creados/verificados con éxito.");
        // } (Eliminado el bloque if count == 0 para permitir verificaciones individuales)
    }

    private Usuario createUsuarioIfNotExists(String legajo, String nombre, String apellido, String dni, String mail, RolUsuario rol, Genero genero, String titulo, LocalDate fechaNacimiento, String rutaFoto) {
        if (usuarioRepository.existsByLegajo(legajo)) {
            return usuarioRepository.findByLegajo(legajo).get();
        }
        
        Usuario u = new Usuario();
        u.setLegajo(legajo);
        u.setNombre(nombre);
        u.setApellido(apellido);
        u.setDni(dni);
        u.setPassword(passwordEncoder.encode("123456"));
        u.setTipoDocumento(TipoDocumento.DNI);
        u.setMail(mail);
        u.setRol(rol);
        u.setGenero(genero);
        u.setEstado(com.sysacad.backend.modelo.enums.EstadoUsuario.ACTIVO);
        u.setTituloAcademico(titulo);
        u.setFechaNacimiento(fechaNacimiento != null ? fechaNacimiento : LocalDate.of(1995, 1, 1));
        u.setFechaIngreso(LocalDate.now());
        
        if (rutaFoto != null) {
            u.setFotoPerfil(rutaFoto);
        }

        return usuarioRepository.save(u);
    }
}
