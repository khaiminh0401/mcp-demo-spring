# Docker Local Runtime — Design Specification

## Objective

Package the four Travel Booking MCP modules so a developer can build and run
the complete local environment with one Docker Compose command. No Gemini API
secret is stored in source control or baked into an image.

## Image Design

A single root `Dockerfile` is parameterized by the `MODULE` build argument.
Docker Compose invokes it four times to produce one image per Maven module.

The build stage uses Maven with JDK 21, copies the reactor POMs before source
files to preserve dependency-layer caching, and runs a reactor package build.
The runtime stage uses a small Java 21 JRE, installs only the utility needed by
the container healthcheck, copies the selected executable Spring Boot jar, and
runs as an unprivileged user. JVM container-awareness remains enabled and the
maximum RAM percentage is configurable.

## Compose Topology

`docker-compose.yml` defines these services:

- `flight` on container/host port 8081;
- `hotel` on container/host port 8082;
- `cab` on container/host port 8083;
- `travel-agent` on container/host port 8080.

All services share Compose's default private network. The travel agent receives
`FLIGHT_MCP_URL=http://flight:8081`, `HOTEL_MCP_URL=http://hotel:8082`, and
`CAB_MCP_URL=http://cab:8083`; it never uses host-loopback addresses for
container-to-container traffic.

Each service has an Actuator healthcheck. `travel-agent` uses long-form
`depends_on` with `condition: service_healthy` for all three MCP servers. It
receives `GEMINI_API_KEY` from Compose variable interpolation and optionally
receives `GEMINI_MODEL`. The API key is not passed to the three MCP servers.

## Developer Workflow

The repository contains a committed `.env.example` with a placeholder and
continues to ignore `.env`. The documented workflow is:

```bash
cp .env.example .env
# edit .env and set GEMINI_API_KEY
docker compose up --build
```

The README also documents health checks, the existing cURL request, log
inspection, shutdown, and volume/image cleanup without deleting user files.

## Verification

Acceptance requires:

- `docker compose config` succeeds with a placeholder key;
- all four images build successfully;
- the three MCP services become healthy;
- the travel agent becomes healthy after discovering all three MCP servers;
- the REST validation endpoint is reachable from the host;
- the Compose stack shuts down cleanly.

A live Gemini booking request is optional because verification environments may
not have a valid API key. Existing Maven tests remain green.
