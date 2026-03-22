package com.sysacad.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileStorageServiceTest {

    private FileStorageService fileStorageService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService();
        ReflectionTestUtils.setField(fileStorageService, "DIRECTORIO_PERFILES", tempDir.toString() + "/");
    }

    @Test
    void guardarFotoPerfil_DeberiaGuardarCorrectamente_YBorrarAnteriorSiExiste() throws Exception {
        // Crear archivo anterior
        Path anterior = tempDir.resolve("anterior.jpg");
        Files.write(anterior, "data".getBytes());

        MockMultipartFile nuevo = new MockMultipartFile(
                "archivo", "perfil.jpg", "image/jpeg", "nueva data".getBytes()
        );

        String rutaResultado = fileStorageService.guardarFotoPerfil(nuevo, "123", anterior.toString());

        assertNotNull(rutaResultado);
        assertTrue(Files.exists(Path.of(rutaResultado)));
        assertFalse(Files.exists(anterior)); // Debe haberse borrado
    }

    @Test
    void borrarArchivo_DeberiaNoBorrar_CuandoEsImagenSeedeada() throws Exception {
        Path seeded = tempDir.resolve("seeded-test.jpg");
        Files.write(seeded, "data".getBytes());

        fileStorageService.borrarArchivo(seeded.toString());

        assertTrue(Files.exists(seeded)); // No debe borrarse
    }

    @Test
    void deleteAllPerfiles_DeberiaLimpiarElDirectorio() throws Exception {
        Files.createDirectories(tempDir);
        Files.write(tempDir.resolve("file1.jpg"), "data".getBytes());
        Files.write(tempDir.resolve("file2.jpg"), "data".getBytes());

        fileStorageService.deleteAllPerfiles();

        assertEquals(0, Files.list(tempDir).count());
    }
}
