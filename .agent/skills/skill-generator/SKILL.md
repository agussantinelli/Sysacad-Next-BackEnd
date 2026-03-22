---
name: skill-generator
description: A tool for creating new skills following the Sysacad Next standard.
---

# 🛠️ Skill Generator

Utiliza esta skill cada vez que necesites crear una nueva directriz o "skill" para el ecosistema de **Sysacad Next**.

## Instrucciones

1.  **Estructura**: Cada skill debe vivir en una subcarpeta de `.agent/skills/<skill-name>/`.
2.  **SKILL.md**: El archivo principal debe ser `SKILL.md` con frontmatter YAML que contenga `name` (id de la skill) y `description`.
3.  **Secciones**: 
    - `# <Nombre de la Skill>` (Header principal).
    - `## Contexto`: Por qué existe esta skill.
    - `## Guías`: Reglas o instrucciones paso a paso.
    - `## Ejemplos`: Snippets de código o capturas de pantalla/comandos.
4.  **Nombramiento**: Usa `kebab-case` para el nombre del directorio.
