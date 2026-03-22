---
name: backend-testing
description: Standardized testing stack for Sysacad Next Backend using JUnit 5, Mockito, and Spring Boot Test.
---

# ⚙️ Sysacad Next - Backend Testing

Esta skill define los estándares para las pruebas unitarias y de integración del backend, asegurando la calidad de la lógica académica y la integridad de los endpoints.

## 🛠️ Herramientas Clave
- **JUnit 5 (Jupiter)**: Runner principal de pruebas.
- **Mockito**: Framework de mocking para aislar componentes (Servicios de Repositorios).
- **MockMvc**: Utilidad para probar la capa de controladores sin levantar un servidor real (Slice Testing).
- **AssertJ**: Aserciones fluidas para mejorar la legibilidad del test.
- **H2 / DB Test**: Base de datos en memoria o aislada para pruebas de persistencia.

## ⚖️ Directrices

### 1. Ubicación y Nombramiento
- Los tests deben estar en `src/test/java/com/sysacad/backend/`.
- Sigue la estructura de paquetes del código fuente.
- Sufijo: `Test.java` (ej. `MateriaServiceTest.java`).

### 2. Pruebas de Integración (MockMvc)
Usa `@WebMvcTest` para controladores o `@SpringBootTest` con `@AutoConfigureMockMvc` para flujos completos:
```java
@Test
public void deberiaRetornarCarreras() throws Exception {
    mockMvc.perform(get("/api/admin/carreras")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThan(0))));
}
```

### 3. Mocking de Dependencias
En los unit tests de servicios, usa `@ExtendWith(MockitoExtension.class)` y `@Mock`:
```java
@Mock
private MateriaRepository materiaRepository;

@InjectMocks
private MateriaService materiaService;

@Test
public void deberiaValidarCorrelatividades() {
    when(materiaRepository.findById(any())).thenReturn(Optional.of(materiaMock));
    // ... ejecución y aserción
}
```

### 4. Generación de Datos
- Crea una clase `TestData` o similar para centralizar la creación de entidades de prueba (Carreras, Alumnos, Notas).
- Evita el hardcoding excesivo; usa constructores o `@Builder` de Lombok.

## 🛡️ Reglas de Oro
1.  **Aislamiento**: Cada test debe ser independiente. Usa `@BeforeEach` para resetear estados si es necesario.
2.  **Validación de Excepciones**: Siempre prueba el "camino triste" (ej. `assertThrows` cuando una correlativa no está aprobada).
3.  **Clean State**: Los tests de integración que escriban en la DB deben ser `@Transactional`.
