# Dexwin Notes – Assessment Submission

A small notes application built with **Java 21** and **Spring Boot 3**, featuring:

- JWT-based authentication
- CRUD notes with soft delete
- Search + tag filter + pagination + sorting
- H2 file-based database with Flyway migrations
- Minimal UI served from the same app (Thymeleaf + vanilla JS)
- OpenAPI (Swagger UI)
- Tests, including a search + pagination integration test
- Dockerfile for containerized deployment

---

## 1. How to run

### Option A – Using Maven (local JDK)

**Requirements**

- Java 21+
- Maven 3.8+

From the project root (where `pom.xml` is):

```bash
mvn spring-boot:run
