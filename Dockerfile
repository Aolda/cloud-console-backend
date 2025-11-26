FROM eclipse-temurin:21-jre AS runtime-prebuilt
WORKDIR /app
COPY /build/libs/server-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
