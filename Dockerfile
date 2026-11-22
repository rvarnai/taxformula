# Stage 1: Build application
FROM eclipse-temurin:25-jdk AS build

WORKDIR /app

COPY . .

RUN chmod +x mvnw && ./mvnw --batch-mode clean verify

# Stage 2: Run application
FROM eclipse-temurin:25-jre

WORKDIR /app

# Create non-root user
RUN useradd --system --uid 10001 appuser

# Copy packaged Spring Boot application
COPY --from=build --chown=appuser:appuser /app/target/*.jar app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
