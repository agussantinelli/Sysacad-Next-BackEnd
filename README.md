<h1 align="center">⚙️ Sysacad Next - Backend API</h1>

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

<div align="center">
    <img src="https://img.shields.io/badge/Java-17%2B-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java Badge"/>
    <img src="https://img.shields.io/badge/Spring%20Boot-3.2-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot Badge"/>
    <img src="https://img.shields.io/badge/PostgreSQL-15-4169E1?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL Badge"/>
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
    <li><strong>Manejo de Errores Global:</strong> <code>@ControllerAdvice</code> para respuestas de error estandarizadas (RFC 7807).</li>
</ul>

<hr>

<h2>🛠️ Stack Tecnológico</h2>

<table>
    <thead>
        <tr>
            <th>Capa</th>
            <th>Tecnología</th>
            <th>Uso Principal</th>
        </tr>
    </thead>
    <tbody>
        <tr>
            <td><strong>Core</strong></td>
            <td>Java 17 / 21</td>
            <td>Lenguaje base, utilizando Records y nuevas features.</td>
        </tr>
        <tr>
            <td><strong>Framework</strong></td>
            <td>Spring Boot 3.x</td>
            <td>Inyección de dependencias y configuración automática.</td>
        </tr>
        <tr>
            <td><strong>Datos</strong></td>
            <td>Spring Data JPA</td>
            <td>ORM (Hibernate) para persistencia en PostgreSQL/MySQL.</td>
        </tr>
    </tbody>
</table>

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
