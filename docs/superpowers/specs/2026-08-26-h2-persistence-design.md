# H2 Persistence for MCP Services — Design Specification

## Objective

Replace transient Java collections with inspectable, file-backed mock databases
while preserving service ownership boundaries. Each MCP server owns its schema
and persists search inventory or booking history independently.

## Database Topology

Each service uses Spring JDBC and H2:

- flight: `flights` and `flight_bookings`;
- hotel: `hotels` and `hotel_reservations`;
- cab: `cab_rides`.

The services do not share tables, files, credentials, or repositories. Tool
classes delegate storage operations to a focused repository backed by
`JdbcTemplate`. Domain objects remain the MCP response contract.

## Schema and Seed Data

Each server provides `schema.sql` and `data.sql`. DDL is idempotent and seed
rows use H2 `MERGE` statements so restarts do not duplicate inventory. Search
tools query inventory tables. Booking tools validate selected inventory where
applicable and insert a booking/reservation/ride record before returning the
persisted result.

Generated confirmation IDs remain deterministic enough for demonstrations but
include a database-generated primary key or unique identifier so repeated
bookings remain independently inspectable.

## Configuration and Inspection

Default application configuration uses file URLs below `./data/<service>` for
direct Maven runs. H2 Console is enabled at `/h2-console` on ports 8081, 8082,
and 8083. The development credentials are user `sa` with an empty password.
The README documents each JDBC URL.

Docker Compose overrides each JDBC URL to `/data/<database>` and mounts one
named volume per service. Restarting or recreating containers retains data;
`docker compose down -v` explicitly removes it.

H2 Console is a demo-only inspection surface. It is not suitable for an
internet-exposed or production deployment.

## Tests and Acceptance Criteria

Repository/tool tests use isolated in-memory H2 databases initialized from the
same SQL resources. Tests verify inventory queries and confirm that booking
tool calls create database rows.

Acceptance requires:

- the Maven reactor tests pass;
- all Docker images build and all services become healthy;
- the agent discovers all MCP tools;
- a direct tool booking persists a queryable row;
- H2 Console is reachable for each server;
- Compose volumes retain records across container restart.
