package com.sysacad.backend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class ArchivoServiceTest {

    private ArchivoService archivoService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        archivoService = new ArchivoService();
        // Redirigimos la ubicación de carga al directorio temporal de JUnit
        ReflectionTestUtils.setField(archivoService, "rootLocation", tempDir);
    }

    @Test
    void guardarFoto_DeberiaGuardarArchivoYYRetornarNombre_CuandoEsValido() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "archivo", "test.jpg", "image/jpeg", "contenido de prueba".getBytes()
        );

        String nombreGuardado = archivoService.guardarFoto(file);

        assertNotNull(nombreGuardado);
        assertTrue(nombreGuardado.startsWith("foto_"));
        assertTrue(nombreGuardado.endsWith(".jpg"));
        assertTrue(Files.exists(tempDir.resolve(nombreGuardado)));
    }

    @Test
    void guardarFoto_DeberiaLanzarExcepcion_CuandoArchivoEstaVacio() {
        MockMultipartFile file = new MockMultipartFile("archivo", "", "text/plain", new byte[0]);

        assertThrows(RuntimeException.class, () -> archivoService.guardarFoto(file));
    }

    @Test
    void cargarFoto_DeberiaRetornarBytes_CuandoArchivoExiste() throws Exception {
        String filename = "test_file.txt";
        byte[] content = "hello world".getBytes();
        Files.write(tempDir.resolve(filename), content);

        byte[] resultado = archivoService.cargarFoto(filename);

        assertArrayEquals(content, resultado);
    }

    @Test
    void cargarFoto_DeberiaLanzarExcepcion_CuandoArchivoNoExiste() {
        assertThrows(RuntimeException.class, () -> archivoService.cargarFoto("non_existent.jpg"));
    }
}
