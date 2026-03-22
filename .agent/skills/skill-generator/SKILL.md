---
name: skill-generator
description: A tool for creating new skills following the MarketFlex standard.
---

# Skill Generator

Use this skill whenever you need to create a new skill for the MarketFlex ecosystem.

## Instructions

1. **Structure**: Every skill must live in `.agent/skills/<skill-name>/`.
2. **SKILL.md**: The main file must be `SKILL.md` with YAML frontmatter containing `name` and `description`.
3. **Sections**: 
   - `# <Skill Name>` header.
   - `## Context`: Why this skill exists.
   - `## Guidelines`: Step-by-step instructions or rules.
   - `## Examples`: Small code snippets or commands.
4. **Naming**: Use kebab-case for directory names.
