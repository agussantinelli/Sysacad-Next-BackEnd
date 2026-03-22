---
name: no-browser
description: Regla de oro que prohíbe terminantemente el uso del subagente del navegador (browser subagent).
---

# No Browser

Este skill establece una prohibición absoluta sobre el uso del subagente del navegador en cualquier circunstancia dentro del proyecto MarketFlex.

## Contexto

El usuario ha solicitado explícitamente que no se utilice el navegador para realizar verificaciones, pruebas o cualquier otra tarea. Todas las validaciones deben realizarse mediante inspección de código, ejecución de comandos en terminal, o mediante la confirmación directa del usuario.

## Guías

1. **PROHIBICIÓN TOTAL**: Nunca, bajo ninguna circunstancia, se debe llamar a la herramienta `browser_subagent`.
2. **Alternativas de Verificación**:
   - Inspeccionar el código fuente generado.
   - Analizar los logs del terminal (servidor backend/frontend).
   - Utilizar herramientas de búsqueda en el código (`grep_search`, `find_by_name`).
   - Consultar al usuario mediante `notify_user` para que verifique manualmente los cambios visuales.
3. **Justificación**: Si sientes la tentación de usar el navegador, recuerda que esta prohibición es una directriz de seguridad y preferencia del usuario para este entorno.

## Ejemplos

### Mal (Prohibido)
❌ `[browser_subagent] -> Navigate to http://localhost:2611...`

### Bien (Permitido)
✅ `[grep_search] -> Buscar clases CSS en el archivo generado`
✅ `[notify_user] -> "He aplicado los cambios visuales. Por favor, verifícalos en tu navegador."`
