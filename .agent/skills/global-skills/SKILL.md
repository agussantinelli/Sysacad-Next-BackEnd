---
name: global-skills
description: Catalog of all specialized skills used across the Sysacad Next project (Frontend & Backend).
---

# 📚 Sysacad Next Global Skills Catalog

This skill serves as a central index for all architectural and development guidelines established in the Sysacad Next project. Each skill defines rules and best practices that **MUST** be respected by the agent and developers.

## ⚙️ Backend Skills (`Sysacad-Next-BackEnd`)

- **[global-context](file:///c:/Users/Agus/IdeaProjects/Sysacad-Next-BackEnd/.agent/skills/global-context/SKILL.md)**: The "Master Rule". Enforces compliance with all other skills and defines the Layered Architecture.
- **[clean-structure](file:///c:/Users/Agus/IdeaProjects/Sysacad-Next-BackEnd/.agent/skills/clean-structure/SKILL.md)**: Java package organization and layered structure rules.
- **[code-quality](file:///c:/Users/Agus/IdeaProjects/Sysacad-Next-BackEnd/.agent/skills/code-quality/SKILL.md)**: Java SOLID principles, Lombok usage, and "No Comments" policy.
- **[naming-conventions](file:///c:/Users/Agus/IdeaProjects/Sysacad-Next-BackEnd/.agent/skills/naming-conventions/SKILL.md)**: PascalCase for classes, camelCase for methods, snake_case for DB, kebab-case for URLs.
- **[spring-boot-module](file:///c:/Users/Agus/IdeaProjects/Sysacad-Next-BackEnd/.agent/skills/spring-boot-module/SKILL.md)**: Blueprint para la creación de nuevos módulos (Entidad, DTO, Mapper, Service, Controller).
- **[test](file:///c:/Users/Agus/IdeaProjects/Sysacad-Next-BackEnd/.agent/skills/test/SKILL.md)**: JUnit 5 and Mockito testing guidelines.
- **[test-enforcement](file:///c:/Users/Agus/IdeaProjects/Sysacad-Next-BackEnd/.agent/skills/test-enforcement/SKILL.md)**: Mandatory rule requiring one test per business logic file.
- **[integration-testing](file:///c:/Users/Agus/IdeaProjects/Sysacad-Next-BackEnd/.agent/skills/integration-testing/SKILL.md)**: Guías para pruebas de interacción de módulos y flujos completos con Spring Boot Test.
- **[backend-testing](file:///c:/Users/Agus/IdeaProjects/Sysacad-Next-BackEnd/.agent/skills/backend-testing/SKILL.md)**: Specific integration testing patterns for the backend.
- **[no-browser](file:///c:/Users/Agus/IdeaProjects/Sysacad-Next-BackEnd/.agent/skills/no-browser/SKILL.md)**: Prohibición total del uso del subagente del navegador.
- **[readme-auto-sync](file:///c:/Users/Agus/IdeaProjects/Sysacad-Next-BackEnd/.agent/skills/readme-auto-sync/SKILL.md)**: Automated README syncing for project structure.
- **[skill-generator](file:///c:/Users/Agus/IdeaProjects/Sysacad-Next-BackEnd/.agent/skills/skill-generator/SKILL.md)**: Standardized way to create new skills.

## 🛠️ Usage guidelines

1. **Check before coding**: Always consult the relevant skill(s) before implementing a new feature.
2. **Consistency over speed**: If a skill defines a pattern, follow it strictly.
3. **Skill updates**: When a new architectural pattern is established, update the relevant skill or create a new one using the `skill-generator`.
