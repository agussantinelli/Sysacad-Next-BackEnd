---
name: test
description: Guidelines for unit and integration testing in the Sysacad Next BackEnd.
---

# 🧪 Sysacad Next - Testing Strategy

La confiabilidad del sistema académico depende de una estrategia de pruebas sólida. Utilizamos el stack estándar de Spring Boot para validaciones unitarias y de integración.

## 🛠️ Stack Tecnológico

1.  **JUnit 5 (Jupiter)**: Motor de pruebas principal.
2.  **Mockito**: Para simular dependencias (`Mock`, `Spy`).
3.  **AssertJ**: Para aserciones fluidas y legibles.
4.  **Spring Boot Test**:
    - `@SpringBootTest`: Para pruebas de integración completas.
    - `@WebMvcTest`: Para probar controladores de forma aislada (Slicing).
    - `@DataJpaTest`: Para pruebas de persistencia con H2 o DB de pruebas.

## ⚖️ Directrices de Pruebas

1.  **Nombramiento**: Los métodos deben describir el comportamiento esperado (ej. `deberiaLanzarExcepcionCuandoCupoAgotado`).
2.  **Unit Tests (Services)**: Son prioritarios. Deben probar la lógica de negocio usando Mocks para los repositorios.
3.  **Manejo de Transacciones**: Usa `@Transactional` en los tests de integración para asegurar que los cambios se reviertan automáticamente tras cada ejecución.
4.  **Aislamiento**: Los tests no deben depender del estado dejado por otros tests ni del orden de ejecución.
5.  **Exhaustividad**: Es mandatario cubrir la mayor cantidad de casos posibles por cada archivo. La profundidad de los tests debe ser proporcional a la complejidad de la lógica de negocio.

## 🚀 Comandos Maven
- `mvn test`: Ejecutar todas las pruebas.
- `mvn test -Dtest=NombreClaseTest`: Ejecutar un test específico.
