# Edira-API

API backend construida con **Spring Boot** para soportar aplicaciones de gestión (ej. Edira).  
Incluye seguridad, validación, migraciones de base de datos, manejo de errores estandarizado y documentación automática.

---

## 🚀 Stack Principal
- **Java 24** con **Spring Boot 3**
- **Spring Security** (autenticación y autorización con roles)
- **Spring Data JPA** + **MySQL**
- **Flyway** para migraciones de base de datos
- **Spring Validation** para validaciones de entidades y DTOs
- **Swagger / OpenAPI** (`springdoc-openapi`) para documentación interactiva
- **Testcontainers** para pruebas de integración con MySQL
- **JUnit 5** + **Spring Boot Test**

---

## ⚙️ Cómo levantar el proyecto

### Requisitos
- JDK 24
- Maven Wrapper (`./mvnw`)
- MySQL corriendo en `localhost:3306` (configurable en `application.properties`)

### Pasos
```bash
# Construir el proyecto
./mvnw clean package

# Correr la aplicación
./mvnw spring-boot:run
