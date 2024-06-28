FROM amazoncorretto:17
ARG JAR_FILE=target/training_microservice-0.0.1-SNAPSHOT.jar
WORKDIR /app
COPY ${JAR_FILE} report-service.jar
ENV SPRING_PROFILES_ACTIVE=default,dev,integration
ENTRYPOINT ["java","-jar","report-service.jar"]