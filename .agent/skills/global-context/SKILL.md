---
name: global-context
description: Directriz maestra del proyecto Sysacad Next Backend. Provee el contexto arquitectónico global y OBLIGA al cumplimiento estricto del resto de las skills.
---

# 🌍 Sysacad Next Backend - Contexto Global y Cumplimiento Estricto

Esta es la **regla maestra** del proyecto. Todas las interacciones, refactorizaciones y creaciones de código en el backend de Sysacad Next deben someterse a los lineamientos descritos aquí. Esta skill actúa como el "vigilante" del resto de las directrices.

## 🏛️ Contexto Arquitectónico (Layered Architecture)

El Sysacad Next Backend sigue una **Arquitectura en Capas (Layered Architecture)** tradicional de Spring Boot, organizada por tipo de componente para mantener la separación de responsabilidades:

1.  **Capa de Controladores (`controller/`)**: Entry points REST. Manejan la comunicación HTTP y seguridad básica (`@PreAuthorize`).
2.  **Capa de Servicios (`service/`)**: El "Domain Guardian". Contiene la lógica de negocio compleja (correlatividades, validaciones, etc.).
3.  **Capa de Mappers (`mapper/`)**: Uso obligatorio de **MapStruct** para la conversión entre Entidades y DTOs.
4.  **Capa de Repositorios (`repository/`)**: Abstracción de acceso a datos mediante Spring Data JPA.
5.  **Capa de Modelo (`modelo/`)**: Entidades JPA con anotaciones de Hibernate y Lombok.
6.  **Capa de DTOs (`dto/`)**: Objetos para transferencia de datos entre cliente y servidor.

## ⚖️ Ley de Cumplimiento de Skills (Enforcement)

El agente de desarrollo **debe obligatoriamente** revisar, considerar y aplicar las reglas de las otras skills instaladas en el directorio `.agent/skills/` antes de escribir o modificar código. NUNCA se debe ignorar una skill especializada a favor de escribir código rápido.

### 📜 Skills Activas que DEBEN ser respetadas:

0. **`global-skills`**: Catálogo central de todas las directrices del proyecto.
1. **`clean-structure`**: Respeta la organización de paquetes de Java y la estructura de capas.
2. **`code-quality`**: Cumple la política de "No Comments", nombres descriptivos, y SOLID aplicado a Java/Spring.
3. **`naming-conventions`**: Clases en `PascalCase`, métodos/variables en `camelCase`, base de datos en `snake_case`.
4. **`no-browser`**: **PROHIBICIÓN TOTAL** del uso del subagente del navegador.
5. **`test`**: Sigue los estándares de JUnit 5 y Mockito para pruebas unitarias e integración.
6. **`readme-auto-sync`**: Actualiza el README.md al crear nuevos componentes o tests.

## 🛡️ Instrucción de Intervención

**SIEMPRE** prioriza la coherencia arquitectónica y la integridad del dominio. 
Si el usuario pide "implementar algo", el agente **debe** seguir el flujo completo: 
1. Definir/Actualizar el `modelo` (Entidad JPA).
2. Crear/Actualizar el `Repository`.
3. Crear los `DTOs` (Request/Response) necesarios.
4. Crear/Actualizar el `Mapper` (MapStruct).
5. Implementar la lógica en el `Service`.
6. Exponer el endpoint en el `Controller`.
7. **Crear el Test correspondiente** (Mandatorio por `test-enforcement`).

_Cualquier desviación de esta estructura es un fallo arquitectónico crítico._
