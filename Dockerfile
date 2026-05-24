# ---- Build Stage ----
FROM maven:3.9-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
# Download dependencies first (cached layer)
RUN mvn dependency:go-offline -B
COPY src ./src
# Build the JAR, skip tests (tests use H2 which is test-scoped)
RUN mvn clean package -DskipTests -B

# ---- Run Stage ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Render sets the PORT env variable
EXPOSE ${PORT:-8080}

ENTRYPOINT ["java", "-jar", "app.jar"]
