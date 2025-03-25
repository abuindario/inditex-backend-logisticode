### Build the project
FROM maven:3.9.9-amazoncorretto-21-al2023 AS build

WORKDIR /app

COPY mvnw* ./
COPY pom.xml ./
COPY .mvn .mvn
COPY src ./src

RUN ./mvnw clean package -Dmaven.test.skip=true

### Run the application
FROM amazoncorretto:21.0.6-al2023-headless

WORKDIR /app

COPY --from=build /app/target/inditex-0.0.1-SNAPSHOT.jar /app/inditex.jar

EXPOSE 3000

ENTRYPOINT ["java", "-jar", "/app/inditex.jar"]
