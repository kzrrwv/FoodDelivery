FROM eclipse-temurin:21-jre-jammy
COPY target/project-0.0.1-SNAPSHOT.jar app.jar
LABEL authors="Thunderobot"

ENTRYPOINT ["java", "-jar", "app.jar"]
