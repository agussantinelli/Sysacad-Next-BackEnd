# 🚨 Manejo de Errores y Excepciones

Este documento describe la estrategia centralizada de manejo de errores implementada en el backend de Sysacad Next.

## 📐 Estructura de Respuesta

Todas las excepciones (tanto de negocio como inesperadas) son capturadas por el `GlobalExceptionHandler` y devuelven una respuesta JSON estandarizada.

### Formato JSON
```json
{
  "timestamp": "2026-01-24T14:30:00.123456",
  "status": 404,
  "error": "Not Found",
  "message": "Usuario no encontrado con ID: 123e4567-e89b-...",
  "path": "/api/usuarios/123"
}
```

- **timestamp**: Fecha y hora del error.
- **status**: Código de estado HTTP.
- **error**: Descripción breve del estado HTTP.
- **message**: Mensaje descriptivo del error (útil para mostrar en UI o debug).
- **path**: URI donde ocurrió el error.

---

## 🚦 Códigos HTTP y Excepciones

### 400 Bad Request (`BusinessLogicException`)
Se utiliza cuando la solicitud es sintácticamente correcta pero viola una regla de negocio.
- **Ejemplos:**
  - "El alumno ya está inscripto a esta materia."
  - "No se puede eliminar una materia con alumnos inscriptos."
  - "Contraseña incorrecta."
  - "El email ya está registrado."

### 404 Not Found (`ResourceNotFoundException`)
Se utiliza cuando el recurso solicitado no existe en la base de datos.
- **Ejemplos:**
  - "Usuario no encontrado."
  - "Materia no encontrada con ID X."
  - "Comisión no inexistente."

### 500 Internal Server Error (`RuntimeException` / `Exception`)
Errores no controlados o fallos inesperados del sistema (bugs, fallos de conexión a DB, etc.).
- **Acción:** Reportar al equipo de desarrollo.

---

## 💻 Implementación

El manejo se realiza mediante **Spring AOP** con `@ControllerAdvice`.

- **Paquete:** `com.sysacad.backend.exception`
- **Handler:** `GlobalExceptionHandler.java`

Los servicios (`@Service`) lanzan las excepciones personalizadas (`BusinessLogicException`, `ResourceNotFoundException`) y el controlador las propaga automáticamente hasta que el Handler las intercepte.
