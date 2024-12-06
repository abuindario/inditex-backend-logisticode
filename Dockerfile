FROM amazoncorretto:21.0.4-alpine3.18

COPY target/inditex-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 3000

ENTRYPOINT [ "java", "-jar", "app.jar" ]