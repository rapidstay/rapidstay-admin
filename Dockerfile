# --- Build stage ---
FROM gradle:8.5-jdk21 AS builder
WORKDIR /app
COPY ../gradlew ../gradle ../settings.gradle ../build.gradle ./
COPY ../common ../common
COPY ../admin ../admin
RUN ./gradlew :admin:bootJar -x test

# --- Run stage ---
FROM eclipse-temurin:21-jdk
WORKDIR /app
COPY --from=builder /app/admin/build/libs/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
