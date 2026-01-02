<h1 align="center">⚙️ Sysacad Next - Backend</h1>

<div align="center">
    <a href="https://github.com/agussantinelli/Sysacad-Next-FrontEnd" target="_blank">
        <img src="https://img.shields.io/badge/🚀%20Repo%20Frontend-Angular-DD0031?style=for-the-badge&logo=angular&logoColor=white" alt="Frontend Repo Badge"/>
    </a>
    <a href="https://github.com/agussantinelli/Sysacad-Next-Backend" target="_blank">
        <img src="https://img.shields.io/badge/⚙️%20Repo%20Backend%20(Estás%20Aquí)-Java%20Spring-F80000?style=for-the-badge&logo=spring&logoColor=white" alt="Backend Repo Badge"/>
    </a>
    <a href="https://github.com/agussantinelli" target="_blank">
        <img src="https://img.shields.io/badge/👤%20Contacto-agussantinelli-000000?style=for-the-badge&logo=github&logoColor=white" alt="Contact Badge"/>
    </a>
</div>

<br>

<div align="center">
    <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java Badge"/>
    <img src="https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot Badge"/>
    <img src="https://img.shields.io/badge/Spring%20Security-6-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white" alt="Spring Security Badge"/>
    <img src="https://img.shields.io/badge/PostgreSQL-15-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL Badge"/>
    <img src="https://img.shields.io/badge/Hibernate-ORM-59666C?style=for-the-badge&logo=hibernate&logoColor=white" alt="Hibernate Badge"/>
    <img src="https://img.shields.io/badge/Apache%20Maven-3.8+-C71A36?style=for-the-badge&logo=apachemaven&logoColor=white" alt="Maven Badge"/>
    <img src="https://img.shields.io/badge/Project%20Lombok-Enables-BC0230?style=for-the-badge&logo=lombok&logoColor=white" alt="Lombok Badge"/>
    <img src="https://img.shields.io/badge/JUnit-5-25A162?style=for-the-badge&logo=junit5&logoColor=white" alt="JUnit Badge"/>
</div>

<div align="center">
    <a href="https://drive.google.com/drive/folders/1Yoln2wLucIvrbcWCbQ_bY-hZ4Z1ENIdD" target="_blank">
        <img src="https://img.shields.io/badge/📂%20Documentación%20del%20Proyecto-4285F4?style=for-the-badge&logo=googledrive&logoColor=white" alt="Docs Badge"/>
    </a>
</div>

<hr>

<h2>🎯 Objetivo</h2>

<p>Proveer una API RESTful robusta, segura y escalable que actúe como el cerebro de <strong>Sysacad Next</strong>, gestionando la lógica de negocio compleja (correlatividades, actas, inscripciones) y asegurando la integridad de los datos académicos.</p>

<h2>🧠 Arquitectura y Diseño</h2>

<p>Este backend está construido siguiendo principios de <strong>Clean Architecture</strong> y <strong>SOLID</strong>, priorizando la desacoplación y la testabilidad.</p>
<ul>
    <li><strong>Seguridad Stateless:</strong> Autenticación vía JWT (JSON Web Tokens) con Spring Security.</li>
    <li><strong>Validación Robusta:</strong> Reglas de negocio forzadas en la capa de servicio (Domain Driven Design).</li>
    <li><strong>Optimización:</strong> Consultas JPA optimizadas y uso de DTOs (Projections) para evitar el problema N+1.</li>
    <li><strong>Manejo de Errores Global:</strong> <code>@ControllerAdvice</code> para respuestas de error estandarizadas.</li>
</ul>

<hr>

<h2>🗂️ Modelo de Dominio</h2>

<table>
    <thead>
        <tr>
            <th>Área</th>
            <th>Entidades (Tablas)</th>
            <th>Responsabilidad</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td><strong>🏢 Infraestructura</strong></td>
            <td><code>facultades_regionales</code>, <code>salones</code></td>
            <td>Sedes y aulas físicas. Raíz de la jerarquía (todo depende de una facultad).</td>
        </tr>
        <tr>
            <td><strong>👤 Actores y Comunicación</strong></td>
            <td><code>usuarios</code>, <code>profesores</code>, <code>sanciones</code>, <code>avisos</code></td>
            <td>Gestión de perfiles, roles, registro disciplinario y <strong>cartelera de novedades</strong>.</td>
        </tr>
        <tr>
            <td><strong>📜 Jerarquía Académica</strong></td>
            <td><code>carreras</code>, <code>planes_de_estudios</code></td>
            <td>Definición estructural. Cadena de dependencia estricta: <strong>Facultad → Carrera → Plan</strong>.</td>
        </tr>
        <tr>
            <td><strong>📚 Curricular</strong></td>
            <td><code>materias</code>, <code>plan_materias</code>, <code>correlativas</code></td>
            <td>Catálogo de asignaturas (fuerte), su contextualización en planes (año/carga) y sistema de correlatividades.</td>
        </tr>
        <tr>
            <td><strong>📅 Gestión de Cursada</strong></td>
            <td><code>comisiones</code>, <code>materias_comisiones</code>, <code>profesores_comisiones</code>, <code>asignaciones_materia</code></td>
            <td>Oferta operativa. Soporta relación N:M (Comisión dicta varias materias) y asignación de roles docentes.</td>
        </tr>
        <tr>
            <td><strong>📝 Ciclo del Alumno</strong></td>
            <td><code>estudios_usuario</code>, <code>inscripciones</code>, <code>calificaciones</code></td>
            <td>Trazabilidad total: Matriculación en carrera, inscripción a cursada/examen y registro de historia académica.</td>
        </tr>
    </tbody>
</table>

<hr>

<h2>🛠️ Stack Tecnológico</h2>

<table>
    <thead>
        <tr>
            <th>Capa / Área</th>
            <th>Tecnología</th>
            <th>Uso Principal</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td><strong>Core</strong></td>
            <td>Java 21</td>
            <td>Lenguaje base, utilizando Records y nuevas features.</td>
        </tr>
        <tr>
            <td><strong>Framework</strong></td>
            <td>Spring Boot 3.5.x</td>
            <td>Inyección de dependencias y configuración automática.</td>
        </tr>
        <tr>
            <td><strong>Build & CI</strong></td>
            <td>Apache Maven</td>
            <td>Gestión de dependencias y ciclo de vida de construcción.</td>
        </tr>
        <tr>
            <td><strong>Seguridad</strong></td>
            <td>Spring Security 6</td>
            <td>Filtros JWT, CORS y autorización por Roles.</td>
        </tr>
        <tr>
            <td><strong>Datos</strong></td>
            <td>Spring Data JPA / Hibernate</td>
            <td>ORM potente para el mapeo objeto-relacional y persistencia.</td>
        </tr>
        <tr>
            <td><strong>Testing</strong></td>
            <td>JUnit 5 & Mockito</td>
            <td>Pruebas unitarias y de integración (<code>spring-boot-starter-test</code>).</td>
        </tr>
        <tr>
            <td><strong>Dev Experience</strong></td>
            <td>Spring Boot DevTools</td>
            <td>Hot-reload y reinicio automático en desarrollo local.</td>
        </tr>
        <tr>
            <td><strong>Utilidades</strong></td>
            <td>Lombok</td>
            <td>Reducción de boilerplate code (Getters, Setters).</td>
        </tr>
    </tbody>
</table>

<hr>

<h2>📦 Estructura del Proyecto</h2>

<pre><code>Sysacad-Next-BackEnd/
├── .mvn/                        # Archivos del Wrapper de Maven
├── src/
│   ├── main/
│   │   ├── java/com/sysacad/backend/
│   │   │   ├── modelo/          # Entidades del dominio (JPA) y Enums
│   │   │   └── BackendApplication.java # Punto de entrada de la aplicación
│   │   └── resources/
│   │       ├── static/          # Archivos estáticos públicos
│   │       ├── templates/       # Plantillas de vista
│   │       └── application.properties # Configuración de Spring Boot
│   └── test/                    # Tests unitarios y de integración
├── target/                      # Salida de compilación (ignorar)
├── .gitignore                   # Archivos ignorados por Git
├── HELP.md                      # Documentación de ayuda de Spring Boot
├── mvnw                         # Script Maven Wrapper (Linux/Mac)
├── mvnw.cmd                     # Script Maven Wrapper (Windows)
├── pom.xml                      # Definición de dependencias y build (Maven)
├── README.md                    # Documentación del proyecto
└── sysacad-next.sql             # Script SQL de base de datos
</code></pre>

<hr>

<h2>💻 Setup Local</h2>

<p>Requisitos: JDK 17+, Maven 3.8+ y PostgreSQL (o Docker).</p>

<h2>🤝 Contribuciones</h2>

<p>Para proponer cambios en la lógica de negocio o nuevos endpoints:</p>
<ol>
    <li>Crea una rama siguiendo la convención: <code>feature/nombre-funcionalidad</code> o <code>fix/nombre-bug</code>.</li>
    <li>Asegúrate de que los tests pasen (<code>mvn test</code>).</li>
    <li>Abre un PR hacia `develop`.</li>
</ol>

<p align="center">Desarrollado con ❤️ y mucho ☕ para la comunidad académica.</p>
