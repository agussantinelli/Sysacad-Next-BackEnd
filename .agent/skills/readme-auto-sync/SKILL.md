---
name: readme-auto-sync
description: Reglas obligatorias para actualizar el README.md automáticamente al crear archivos o tests.
---

# 📝 README Auto-Sync

Esta skill define el comportamiento obligatorio del agente AI respecto al mantenimiento del archivo `README.md` del proyecto Backend de Sysacad Next.

## 🏛️ Contexto
Para asegurar que la documentación del proyecto nunca quede obsoleta, es mandatorio que el agente actualice el `README.md` de manera proactiva y automática cada vez que realice cambios estructurales o agregue cobertura de pruebas.

## ⚖️ Reglas Obligatorias (Guidelines)

1.  **Estructura del Proyecto (Árbol de Directorios)**:
    - **CUÁNDO**: Cada vez que crees, muevas o elimines un archivo (`.java`, `.xml`, `.md`, etc.) o un paquete (directorio).
    - **QUÉ HACER**: Debes generar un nuevo árbol de directorios (o listar recursivamente) y actualizar la sección "Estructura del Proyecto" en el `README.md` para que refleje el estado exacto actual.

2.  **Contador de Tests y Estadísticas**:
    - **CUÁNDO**: Cada vez que crees un nuevo archivo de test (`Test.java`) o agregues/elimines tests dentro de un archivo existente.
    - **QUÉ HACER**: Debes ejecutar la suite de testing (ej. `mvn test`), leer la salida de la consola para obtener el número exacto de tests que han pasado, y actualizar el badge o sección de estadísticas en el `README.md`.

## 🛡️ Proactividad
**NO ESPERES** a que el usuario te pida que actualices el README. Si tus acciones previas disparan las reglas anteriores, tu siguiente paso inmediato debe ser actualizar el `README.md` silenciosamente antes de notificar al usuario que terminaste la tarea.
