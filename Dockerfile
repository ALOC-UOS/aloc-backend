# Gradle 빌드를 위한 JDK 이미지 사용
FROM gradle:8.8.0-jdk17 AS build

WORKDIR /app

# 소스 코드 복사
COPY . .

# 프로젝트 빌드
RUN gradle build --no-daemon

# JDK 17 이미지를 기반으로 사용
FROM openjdk:17-jdk-slim
WORKDIR /app

# Gradle 빌드 출력물을 복사 (JAR 파일)
COPY --from=build /app/build/libs/*.jar app.jar

# application.yml 파일을 복사
#COPY src/main/resources/application.yml /app/config/application.yml
#COPY src/main/resources/application-jwt.yml /app/config/application-jwt.yml
RUN mkdir -p /config
RUN echo "${{ secrets.JWT_APPLICATION }}" | base64 -d > /app/config/application-jwt.yml
RUN echo "${{ secrets.APPLICATION }}" | base64 -d > /app/config/application.yml
# 애플리케이션 실행을 위한 포트 오픈 (Spring Boot 기본 포트)
EXPOSE 8080

# 애플리케이션 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
