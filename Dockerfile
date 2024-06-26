# JDK 17 이미지를 기반으로 사용
FROM openjdk:17-jdk-slim AS build

# 작업 디렉토리 설정
WORKDIR /app

# Gradle 빌드 출력물을 복사 (JAR 파일)
COPY build/libs/*.jar app.jar

# application.yml 파일을 복사
COPY src/main/resources/application.yml /app/config/application.yml
COPY src/main/resources/application-jwt.yml /app/config/application-jwt.yml

# 애플리케이션 실행을 위한 포트 오픈 (Spring Boot 기본 포트)
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
