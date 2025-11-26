FROM eclipse-temurin:21-jre AS runtime-prebuilt
WORKDIR /app
ARG APP_JAR=app.jar
COPY ${APP_JAR} app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
ㅎ