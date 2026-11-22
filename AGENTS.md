# Agent Guide

## Project

`taxformula` is a Spring Boot 4 / Java 25 MVC application that calculates Austrian income-tax Excel formulas and renders them with Thymeleaf. H2 is the runtime database.

## Layout

- `src/main/java/at/rvarnai/taxformula/resource`: MVC controllers.
- `.../service`: application calculations and service interfaces.
- `.../backend`: data-access orchestration.
- `.../repository`: Spring Data JPA repositories.
- `.../model`: domain types and immutable/value objects.
- `.../exception`: application exceptions and global HTTP error handling.
- `src/main/resources/templates`: Thymeleaf views.
- `src/test/java`: Spring MVC tests.

## Commands

- Run: `./mvnw spring-boot:run`
- Test: `./mvnw test`
- Package: `./mvnw clean package`

## Conventions

- Use constructor injection for dependencies.
- Use Lombok's `@RequiredArgsConstructor`, `@Slf4j`, and focused data annotations to keep boilerplate low.
- Keep HTTP concerns in resource/controller classes and business calculations in services.
- Add focused `@WebMvcTest` coverage for controller behavior and global exception responses.
- Preserve RFC 9457-style `ProblemDetail` responses for expected API and validation failures.
- Run the smallest relevant Maven test first, then the full suite for shared changes.
