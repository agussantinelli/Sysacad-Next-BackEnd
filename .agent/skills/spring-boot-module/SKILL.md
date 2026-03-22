---
name: spring-boot-module
description: Guía para la creación de nuevos módulos de dominio siguiendo el estándar Sysacad Next.
---

# 🏗️ Sysacad Next - Spring Boot Module Blueprint

Esta skill define el esqueleto obligatorio para cualquier nueva funcionalidad o dominio en el backend. Seguir este patrón asegura que todas las piezas (Entidad, DTO, Mapper, Service, Controller) encajen perfectamente.

## 📋 Los 7 Pilares de un Módulo

Al crear un nuevo dominio (ej. `Materia`), se DEBEN generar los siguientes archivos siguiendo sus respectivas skills (`naming-conventions`, `clean-structure`):

1.  **Modelo (`modelo/Materia.java`)**:
    - Entidad JPA con `@Entity`, `@Data`, y `@Table`.
    - IDs usando `UUID` y `@GeneratedValue`.
2.  **Repisitorio (`repository/MateriaRepository.java`)**:
    - Interface que extiende `JpaRepository<Materia, UUID>`.
3.  **DTOs (`dto/materia/`)**:
    - `MateriaRequest`: Para datos de entrada (POST/PUT).
    - `MateriaResponse`: Para datos de salida.
4.  **Mapper (`mapper/MateriaMapper.java`)**:
    - Interface MapStruct con `(componentModel = "spring")`.
    - Mapeo bidireccional y `updateEntityFromRequest`.
5.  **Servicio (`service/MateriaService.java`)**:
    - Clase anotada con `@Service`.
    - Inyección de dependencias por constructor.
    - Lógica de negocio y uso del Mapper.
6.  **Controlador (`controller/MateriaController.java`)**:
    - Anotado con `@RestController` y `@RequestMapping`.
    - Uso de `@PreAuthorize` para seguridad.
    - Delega todo al `Service`.
7.  **Test (`src/test/java/.../MateriaServiceTest.java`)**:
    - Pruebas unitarias obligatorias (cobertura mínima lógica de negocio).

## 🛡️ Flujo de Implementación (`The Full Loop`)

1.  **Diseño DB**: Define la tabla en `modelo/` y sus relaciones.
2.  **DTOs & Mappers**: Define qué datos entran y salen. Configura MapStruct.
3.  **Contrato REST**: Crea el Controller con los endpoints (aunque devuelvan error o mock).
4.  **Lógica Intermedia**: Implementa el Service con las reglas de negocio (ej. validar correlatividades).
5.  **Testing**: Escribe el test que valide la lógica del Service.

_No consideres un módulo como "terminado" si falta alguno de estos 7 pilares._
