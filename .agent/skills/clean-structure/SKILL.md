---
name: clean-structure
description: Architectural patterns and package organization for a clean Spring Boot backend.
---

# 🏛️ Sysacad Next - Clean Structure

El proyecto sigue una **Arquitectura en Capas (Layered Architecture)** organizada por paquetes. Esta estructura es vital para la mantenibilidad y desacoplamiento del sistema académico.

## 📁 Organización de Paquetes (`src/main/java/com/sysacad/backend/`)

1.  **`controller/`**: Entrada de peticiones REST. Validan el acceso mediante `@PreAuthorize` y delegan la lógica a los servicios.
2.  **`service/`**: El núcleo de la aplicación. Contiene la lógica de negocio, validación de reglas académicas y transacciones (`@Transactional`).
3.  **`repository/`**: Abstracción de base de datos usando **Spring Data JPA**.
4.  **`modelo/`**: Entidades persistentes JPA. No deben salir de la capa de servicio.
5.  **`dto/`**: Objetos para el intercambio de datos (Request/Response). 
    - Se recomienda agruparlos por dominio (ej. `dto.admin`, `dto.alumno`).
6.  **`mapper/`**: Interfaces de **MapStruct** para la conversión segura y eficiente entre Entidades y DTOs.
7.  **`exception/`**: Excepciones personalizadas y el `GlobalExceptionHandler` (`@ControllerAdvice`).
8.  **`config/`**: Configuraciones globales (Seguridad, CORS, Seeders).

## ⚖️ Reglas de Oro Estructurales

1.  **DTO-First**: Está terminantemente prohibido exponer entidades JPA (`modelo/`) en los controladores. Toda entrada y salida debe ser un DTO.
2.  **Lógica en Servicios**: Los controladores deben ser delgados. La lógica de decisión, cálculos y validaciones de negocio pertenecen al `service`.
3.  **MapStruct Mandatory**: No realices mapeos manuales `new DTO()` o `setX(entity.getX())`. Usa los mappers definidos en `mapper/`.
4.  **Separación Admin**: La lógica administrativa (ABM complejo, estadísticas globales) debe residir en archivos con el prefijo `Admin*` (ej. `AdminCarreraController`, `AdminCarreraService`) para diferenciarla de la lógica de usuario final.
5.  **Inyección por Constructor**: Prefiere la inyección de dependencias por constructor sobre `@Autowired` en campos para facilitar el testing.
