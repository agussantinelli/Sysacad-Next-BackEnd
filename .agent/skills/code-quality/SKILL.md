---
name: code-quality
description: Guidelines for clean code, readability, and the "No Comments" policy applied to Java/Spring.
---

# ✨ Sysacad Next - Code Quality

La calidad del código es innegociable. Buscamos un backend robusto, legible y fácil de mantener siguiendo los principios de Clean Code y las mejores prácticas de Java/Spring.

## ⚖️ Principios Fundamentales

1.  **POLÍTICA DE "NO COMMENTS"**: El código debe ser auto-explicativo. Evita comentarios que expliquen *qué* hace el código. Si un método es complejo, refactorízalo o mejora los nombres de variables.
    - *Excepción*: Javadoc para APIs públicas o explicaciones de algoritmos matemáticos/de negocio extremadamente complejos ("por qué", no "cómo").
2.  **Lombok al Máximo**: Aprovecha Lombok para eliminar el ruido (`@Data`, `@Builder`, `@Slf4j`, `@NoArgsConstructor`). Reduce el "clutter" visual de getters/setters manuales.
3.  **Nombres Descriptivos**: Usa nombres que revelen la intención.
    - Clases: Sustantivos (`CarreraService`, `UsuarioRepository`).
    - Métodos: Verbos (`inscribirAlumno`, `validarCorrelatividades`).
4.  **Métodos Pequeños (SRP)**: Cada método debe tener una única responsabilidad y no exceder las 20-30 líneas preferentemente.
5.  **Fail-Fast**: Valida las condiciones de borde y parámetros al inicio del método. Lanza excepciones claras de la capa `exception`.
6.  **Uso de Optional y Streams**: Evita los `null` checks tradicionales. Usa `Optional` para retornos que pueden ser vacíos y la API de `Stream` para procesamiento de colecciones de forma declarativa.
7.  **Manejo de Errores Centralizado**: No uses bloques `try-catch` genéricos en los controladores. Deja que las excepciones fluyan hacia el `GlobalExceptionHandler`.
8.  **Inmutabilidad**: Usa `final` para variables locales y parámetros de métodos que no cambian su valor.
