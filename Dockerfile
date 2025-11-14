# 1단계: Gradle로 빌드
FROM gradle:8-jdk21 AS builder
WORKDIR /app
COPY . .
RUN gradle clean bootJar --no-daemon

# 2단계: 빌드된 JAR 실행 (Temurin JRE 사용)
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
