FROM openjdk:23-ea-17-jdk
WORKDIR /app
COPY target/orders-microservice-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8084
ENTRYPOINT ["java", "-jar", "app.jar"]