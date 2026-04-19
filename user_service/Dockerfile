FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/user-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-Dspring.profiles.active=docker", "-Dspring.config.import=optional:configserver:http://config-server:8888", "-jar", "app.jar"]