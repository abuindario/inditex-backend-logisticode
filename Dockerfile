### Build the project
FROM openjdk:21-jdk AS build

WORKDIR /app

COPY mvnw* ./
COPY pom.xml ./
COPY .mvn .mvn
COPY src ./src

RUN ./mvnw clean package -DskipTests

### Run the application
FROM openjdk:21-jdk

WORKDIR /app

COPY --from=build /app/target/inditex-0.0.1-SNAPSHOT.jar /app/inditex.jar

EXPOSE 3000

ENTRYPOINT ["java", "-jar", "/app/inditex.jar"]
