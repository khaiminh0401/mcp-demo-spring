# syntax=docker/dockerfile:1.7

FROM maven:3.9.12-eclipse-temurin-21 AS builder

ARG MODULE
WORKDIR /workspace

COPY pom.xml ./
COPY flight-mcp-server/pom.xml flight-mcp-server/pom.xml
COPY hotel-mcp-server/pom.xml hotel-mcp-server/pom.xml
COPY cab-mcp-server/pom.xml cab-mcp-server/pom.xml
COPY travel-client-agent/pom.xml travel-client-agent/pom.xml

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -pl "${MODULE}" -am dependency:go-offline

COPY flight-mcp-server/src flight-mcp-server/src
COPY hotel-mcp-server/src hotel-mcp-server/src
COPY cab-mcp-server/src cab-mcp-server/src
COPY travel-client-agent/src travel-client-agent/src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -pl "${MODULE}" -am package -DskipTests

FROM eclipse-temurin:21-jre-jammy AS runtime

ARG MODULE

RUN apt-get update \
    && apt-get install --no-install-recommends -y curl \
    && rm -rf /var/lib/apt/lists/* \
    && groupadd --system spring \
    && useradd --system --gid spring --home-dir /app spring

WORKDIR /app
COPY --from=builder --chown=spring:spring /workspace/${MODULE}/target/${MODULE}-1.0.0-SNAPSHOT.jar app.jar

USER spring:spring

ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
