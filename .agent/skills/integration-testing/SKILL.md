---
name: integration-testing
description: Guías para pruebas de interacción de módulos y flujos completos con Spring Boot Test.
---

# 🔗 Sysacad Next - Integration Testing

Las pruebas de integración validan que los componentes del sistema (Controladores, Servicios, Repositorios, Base de Datos) trabajen juntos correctamente. A diferencia de los tests unitarios, aquí probamos el **contexto real** de la aplicación.

## 🛠️ Herramientas de Integración

1.  **`@SpringBootTest`**: Levanta el contexto de aplicación completo. Se usa para flujos E2E internos (de Controller a DB).
2.  **`MockMvc`**: Herramienta principal para simular peticiones HTTP sin desplegar un servidor real.
3.  **`@AutoConfigureMockMvc`**: Anotación necesaria para inyectar el cliente `MockMvc`.
4.  **`@MockBean`**: Se utiliza para mockear únicamente servicios externos (ej. `EmailService`) y así no depender de redes externas.
5.  **`@Transactional`**: **CRÍTICO**. Asegura que cada test se ejecute en una transacción que hace rollback al terminar, manteniendo la DB limpia.

## ⚖️ Patrones de Diseño de Tests

### 1. El Flujo de "Capa a Capa" (Full Stack Integration)
Un test de integración debe validar la comunicación real entre todas las capas del backend. **NO se deben usar Mocks para Controller, Service o Repository**:

1.  **Controller**: Recibe la petición, valida el DTO y la seguridad.
2.  **Service**: Ejecuta la lógica de negocio real (ej. validación de correlatividades).
3.  **Repository**: Realiza la consulta/persistencia real vía JPA.
4.  **Database (H2/TestDB)**: Los datos se guardan y consultan físicamente.

```java
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class InscripcionIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private InscripcionRepository inscripcionRepository; // Inyección real

    @Test
    void deberiaInscribirAlumnoACarrera() throws Exception {
        // 1. Preparar DTO de entrada
        String request = "{ \"estudianteLegajo\": \"55555\", \"carreraId\": \"...\" }";

        // 2. Ejecutar petición al Controller -> Service -> Repository
        mockMvc.perform(post("/api/admin/matriculacion")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isCreated());
                
        // 3. VERIFICAR CAPA DE DATOS: Confirmar que el repositorio realmente guardó la entidad
        boolean existe = inscripcionRepository.existsByEstudianteLegajo("55555");
        assertTrue(existe, "El alumno debería estar matriculado en la DB");
    }
}
```

### 2. Aislamiento y Estado Real
- **Mocking Selectivo**: Solo se permite `@MockBean` para interfaces externas al backend (ej. `EmailService`, `StorageService`). Todo lo que sea parte del dominio (`modelo/`, `service/`, `repository/`) debe ser real.
- **Rollback Automático**: Gracias a `@Transactional`, cada test comienza con la base de datos en un estado conocido y deshace sus cambios al finalizar.

## 🛡️ Reglas de Oro
1.  **No Mocks de Dominio**: En integración, los Repositorios y Servicios internos **NO** se mockean. Se usa la lógica real.
2.  **Validación de Side-Effects**: Verifica no solo el status HTTP, sino que los datos realmente cambiaron en la base de datos si la operación fue un POST/PUT/DELETE.
3.  **Seguridad**: Si el endpoint está protegido, usa los helpers de Spring Security (`@WithMockUser` o inyectando tokens JWT válidos) en la petición.
