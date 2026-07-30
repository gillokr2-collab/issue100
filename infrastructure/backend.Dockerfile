FROM gradle:8.14-jdk21-alpine AS build
WORKDIR /workspace
COPY settings.gradle build.gradle ./
COPY backend ./backend
RUN gradle :backend:bootJar --no-daemon
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/backend/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
