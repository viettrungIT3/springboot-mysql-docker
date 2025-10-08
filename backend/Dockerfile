# syntax=docker/dockerfile:1.6

FROM eclipse-temurin:17-jdk AS build
WORKDIR /app

COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew

# Warm up Gradle cache and fetch dependencies with simple retry
RUN --mount=type=cache,target=/root/.gradle bash -lc 'for i in 1 2 3; do ./gradlew --no-daemon dependencies && break || (echo "Retry $i" && sleep 10); done'

COPY src src
# Build fat jar with retry and cached Gradle
RUN --mount=type=cache,target=/root/.gradle bash -lc 'for i in 1 2 3; do ./gradlew --no-daemon clean bootJar && break || (echo "Retry $i" && sleep 10); done'


FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

ENV JAVA_OPTS=""

COPY --from=build /app/build/libs/*.jar /app/app.jar

EXPOSE 8080

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar /app/app.jar"]

