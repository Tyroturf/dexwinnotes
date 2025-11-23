
# Build Stage

FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy Maven descriptor & pre-download dependencies
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Copy source code and build the application
COPY src ./src
RUN mvn -q -DskipTests package



# Runtime Stage

FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy the built artifact from the builder stage
COPY --from=build /app/target/notes-app-0.0.1-SNAPSHOT.jar app.jar

# Expose the application port
EXPOSE 8080

# Run the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
