# 1단계: 빌드
FROM gradle:jdk-21-and-23-graal-jammy AS builder

WORKDIR /app

# gradlew와 gradle 디렉토리까지 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x gradlew
RUN ./gradlew build -x test --no-daemon
RUN rm -rf /app/build/libs/*-plain.jar

# 2단계: 실행
FROM container-registry.oracle.com/graalvm/jdk:23

WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]