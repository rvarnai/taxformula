# Tax Formula

Tax Formula generates an Excel formula for Austrian income tax using the configured tax levels for 2025, 2026, or 2027.

## Requirements

- Java 25
- No separate database installation (the app uses an in-memory H2 database)

## Run locally

```sh
./mvnw spring-boot:run
```

Open [http://localhost:8080/formula](http://localhost:8080/formula).

## Verify and package

```sh
./mvnw test
./mvnw clean package
java -jar target/taxformula-0.0.1-SNAPSHOT.jar
```

## Error responses

Invalid years and missing resources return an RFC 9457 `application/problem+json` response. A typical not-found response is:

```json
{
  "type": "about:blank",
  "title": "Resource not found",
  "status": 404,
  "detail": "Selected year 2099 not found!",
  "instance": "/formula"
}
```
