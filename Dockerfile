### Build the project
FROM maven:3.9.9-amazoncorretto-21-al2023 AS build

WORKDIR /app

COPY pom.xml mvnw* ./
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw dependency:go-offline

COPY src ./src
RUN ./mvnw clean package -Dmaven.test.skip=true

### Run the application
FROM amazoncorretto:21-alpine AS runtime

WORKDIR /app

# Add a non-root user for better security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy the application jar
COPY --from=build /app/target/inditex-*.jar /app/inditex.jar

# Metadata for the image
LABEL version="1.0"
LABEL description="Docker image for Inditex project"

# Expose the application port
EXPOSE 3000

# Optimize JVM for production
ENTRYPOINT ["java", "-jar", "/app/inditex.jar"]