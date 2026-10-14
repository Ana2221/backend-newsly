FROM maven:3.9-eclipse-temurin-17 AS build

WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp package -DskipTests

FROM eclipse-temurin:17-jre-jammy

WORKDIR /app
RUN groupadd --system app \
    && useradd --system --gid app --home-dir /app --shell /usr/sbin/nologin app \
    && mkdir -p /app/uploads \
    && chown -R app:app /app

COPY --from=build --chown=app:app /app/target/*.jar /app/app.jar

USER app
EXPOSE 8080
VOLUME ["/app/uploads"]

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
