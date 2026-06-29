FROM eclipse-temurin:25-jre-alpine
LABEL authors="sahajdeep"

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]