package com.sysacad.backend.service.pdf;

import com.sysacad.backend.dto.alumno.AlumnoCertificadoDTO;
import com.sysacad.backend.dto.examen.ProfesorCertificadoDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class OpenPdfGeneratorTest {

    private OpenPdfGenerator openPdfGenerator;

    @BeforeEach
    void setUp() {
        openPdfGenerator = new OpenPdfGenerator();
    }

    @Test
    void generarCertificado_DeberiaRetornarByteArrayNoVacio() {
        AlumnoCertificadoDTO datos = new AlumnoCertificadoDTO(
                "Juan Perez",
                "12345678",
                "123456",
                "Ingenieria",
                "Rosario",
                LocalDate.now()
        );

        byte[] result = openPdfGenerator.generarCertificado(datos);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void generarCertificadoProfesor_DeberiaRetornarByteArrayNoVacio() {
        ProfesorCertificadoDTO datos = new ProfesorCertificadoDTO(
                "Maria Lopez",
                "87654321",
                "654321",
                "PROFESOR TITULAR",
                LocalDate.now()
        );

        byte[] result = openPdfGenerator.generarCertificadoProfesor(datos);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }
}
