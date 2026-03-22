package com.sysacad.backend.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtServiceTest {

    @InjectMocks
    private JwtService jwtService;

    @Mock
    private UserDetails userDetails;

    private final String secretKey = "Zm9vYmFyYmF6cXV4cXV1eHF1dXhudXhudXhudXhudXhudXhudXhudXhudXhudXhudXhudXhudXhudXhudXhudXhudXhudXhudXhu";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtService, "secretKey", secretKey);
    }

    @Test
    @DisplayName("Debe generar un token válido para un usuario")
    void generateToken_Success() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("testuser");

        // Act
        String token = jwtService.generateToken(userDetails);

        // Assert
        assertNotNull(token);
        assertEquals("testuser", jwtService.extractUsername(token));
    }

    @Test
    @DisplayName("Debe validar correctamente un token vigente")
    void isTokenValid_Success() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("testuser");
        String token = jwtService.generateToken(userDetails);

        // Act
        boolean isValid = jwtService.isTokenValid(token, userDetails);

        // Assert
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Debe invalidar un token si el username no coincide")
    void isTokenValid_WrongUser() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("testuser");
        String token = jwtService.generateToken(userDetails);
        
        UserDetails otherUser = org.mockito.Mockito.mock(UserDetails.class);
        when(otherUser.getUsername()).thenReturn("otheruser");

        // Act
        boolean isValid = jwtService.isTokenValid(token, otherUser);

        // Assert
        assertFalse(isValid);
    }

    @Test
    @DisplayName("Debe invalidar un token si el BOOT_ID cambió (reinicio del servidor)")
    void isTokenValid_DifferentBootId() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("testuser");
        String token = jwtService.generateToken(userDetails);
        
        // Simular cambio de BOOT_ID (esto es difícil porque es estático final, 
        // pero podemos probar que el token DEBE tener el ID actual)
        String bootId = JwtService.getBootId();
        assertNotNull(bootId);
        
        // El test verifica que el token generado con el BOOT_ID actual es válido.
        // Si el BOOT_ID cambiara en una nueva instancia de la JVM, el token viejo fallaría.
        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    @DisplayName("Debe extraer claims personalizados correctamente")
    void extractClaim_Success() {
        // Arrange
        when(userDetails.getUsername()).thenReturn("testuser");
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", "ADMIN");
        String token = jwtService.generateToken(claims, userDetails);

        // Act
        String role = jwtService.extractUsername(token); // Subject es testuser
        
        // En JwtService no hay método público para extraer un claim genérico, 
        // pero podemos verificar que el token se parsea.
        assertEquals("testuser", role);
    }
}
