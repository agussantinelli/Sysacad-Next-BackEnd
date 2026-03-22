---
name: naming-conventions
description: Guidelines for file and directory naming conventions in the Sysacad Next project.
---

# 🏷️ Sysacad Next - Naming Conventions

Las convenciones de nombres aseguran la consistencia y previsibilidad del código. En este proyecto Java/Spring, seguimos los estándares de la comunidad adaptados a nuestras necesidades.

## 📄 Archivos y Clases (`PascalCase`)

Todas las clases y archivos `.java` deben usar `PascalCase`.
- **Controllers**: `NombreController.java` (ej. `CarreraController`, `AdminMesaController`).
- **Services**: `NombreService.java` (ej. `UsuarioService`).
- **Repositories**: `NombreRepository.java` (ej. `InscripcionRepository`).
- **Mappers**: `NombreMapper.java` (ej. `MateriaMapper`).
- **Entities**: El nombre de la entidad en singular (ej. `Carrera.java`, `PlanDeEstudio.java`).
- **DTOs**: `NombreRequest.java`, `NombreResponse.java` o `NombreDTO.java`.

## ⚙️ Métodos y Variables (`camelCase`)

- **Métodos**: Deben comenzar con un verbo (ej. `obtenerTodos`, `validarCupo`, `inscribirAExamen`).
- **Variables**: Nombres descriptivos (ej. `carrerasDisponibles`, `idUsuario`).

## 🗄️ Base de Datos (`snake_case`)

Utilizamos `snake_case` para la persistencia, mapeado mediante anotaciones JPA (`@Table`, `@Column`, `@JoinColumn`).
- **Tablas**: Plural (ej. `usuarios`, `comisiones`, `facultades_carreras`).
- **Columnas**: Descriptivas (ej. `id_usuario`, `nombre_completo`, `fecha_inscripcion`).

## 🌐 Endpoints REST (`kebab-case`)

Las URLs de la API deben ser consistentes y en minúsculas.
- **Base Paths**: `/api/admin/carreras`, `/api/inscripciones-examen`.
- **Resources**: Plural por defecto (`/usuarios`, `/mesas`).
- **Actions**: Si no es un CRUD estándar, usa verbos discretos (`/leido`, `/marcar-pagado`).

## 🧪 Tests

- **Clases de Test**: `NombreClaseOriginalTest.java` (ej. `CarreraServiceTest.java`).
- **Métodos de Test**: `deberiaExplotarCuando...` o `shouldReturnOkWhen...` (mantén consistencia con el idioma del archivo).
