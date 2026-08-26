# Travel Booking MCP Demo — Design Specification

## 1. Objective

Build a copy-paste-ready proof of concept consisting of one Spring AI travel
orchestrator and three independently deployable MCP servers. The orchestrator
uses Gemini to interpret a Vietnamese or English travel request, discovers the
remote MCP tools, and autonomously chains flight, hotel, and cab operations.

The demo targets Java 21 and Spring Boot 3.4.x. It is intended for local
development and architecture demonstrations, not production deployment.

## 2. Compatibility Decision

Spring AI's native Google GenAI starter belongs to a newer dependency line than
the requested Spring Boot 3.4 baseline. To preserve the requested runtime, the
client will use the Spring AI OpenAI chat-model starter against Gemini's
OpenAI-compatible API. Authentication uses the `GEMINI_API_KEY` environment
variable. The configured model will default to `gemini-2.5-flash` and remain
overridable through an environment variable.

The MCP dependencies use their stable artifact names:

- `spring-ai-starter-mcp-server-webmvc`
- `spring-ai-starter-mcp-client`
- `spring-ai-starter-model-openai`

SSE is used explicitly because it is part of the requested architecture, even
though newer MCP releases favor Streamable HTTP.

## 3. Repository and Module Structure

The repository contains a root Maven aggregator and four independent Spring
Boot modules:

```text
mcp-demo-spring/
├── pom.xml
├── flight-mcp-server/
├── hotel-mcp-server/
├── cab-mcp-server/
└── travel-client-agent/
```

Each child module has its own `pom.xml`, application entry point,
`application.yml`, domain records/DTOs, and tests. Each can be built and run
independently. The root POM only centralizes versions and enables a single
reactor build.

## 4. MCP Servers

All servers use Spring MVC and expose the legacy MCP SSE transport on localhost.
Tools are Spring components whose public methods carry `@Tool`; every input is
annotated with an English semantic `@ToolParam` description that also accounts
for Vietnamese user phrasing. A `MethodToolCallbackProvider` bean registers the
component with MCP auto-configuration.

### 4.1 Flight server — port 8081

Tools:

- `searchFlights(from, to, date)` validates an ISO date and returns several
  deterministic mock flights with `flightId`, `airline`, `price`,
  `departureTime`, and `arrivalTime`.
- `bookFlight(flightId, passengerName)` validates the requested mock ID and
  returns `bookingReference`, `status = CONFIRMED`, and `totalAmount`.

### 4.2 Hotel server — port 8082

Tools:

- `searchHotels(location, checkInDate, nights)` validates the ISO date and a
  positive night count, then returns deterministic hotels containing `hotelId`,
  `name`, `pricePerNight`, and `rating`.
- `bookHotel(hotelId, guestName, checkInDate, nights)` validates the selected
  mock ID and returns `reservationCode` and `status = CONFIRMED`.

### 4.3 Cab server — port 8083

Tool:

- `bookCab(pickupLocation, dropoffLocation, pickupTime, passengerName)` returns
  `rideId`, `driverName`, `licensePlate`, `estimatedPrice`, and
  `status = BOOKED`.

The mock repositories remain in memory. Stable identifiers and prices make
demonstrations and assertions repeatable; no database is introduced.

## 5. Client Agent — port 8080

The MCP client auto-configuration creates synchronous SSE connections named
`flight`, `hotel`, and `cab`, pointing to `http://localhost:8081`, `:8082`, and
`:8083`. Tool callback integration is enabled so the callbacks discovered from
all connections are exposed through one `ToolCallbackProvider`.

`McpClientConfig` creates the application `ChatClient` from a
`ChatClient.Builder`, applies a travel-agent system prompt, and registers the
dynamic callbacks from `ToolCallbackProvider`. The system prompt instructs the
model to:

1. search before booking;
2. select sensible options when the user does not state a preference;
3. reuse returned IDs and locations exactly;
4. book flight, hotel, and airport transfer when requested;
5. report all confirmation identifiers and totals;
6. never invent a successful booking when a tool reports failure.

Spring AI's tool-calling loop handles the autonomous multi-step sequence within
one ChatClient request.

## 6. HTTP Contract

`POST /api/travel/plan`

Request:

```json
{
  "prompt": "Tôi muốn đi du lịch ...",
  "user": "Nguyễn Văn An"
}
```

Both properties are required and non-blank. The controller adds the user name
as trusted request context and passes the travel prompt to the agent. The JSON
response contains the user and the final agent response. It does not expose
model-provider implementation classes.

Validation errors return HTTP 400. Model, MCP connectivity, and tool execution
failures return a structured HTTP 502 response without leaking API keys or
stack traces.

## 7. Configuration

Server YAML files define their ports, application names, synchronous MCP mode,
SSE protocol, and server metadata. The client YAML defines all three named SSE
connections, request timeout, synchronous client type, callback enablement,
Gemini-compatible base URL, API key placeholder, model, and low temperature.

No secret is committed. Startup requires:

```bash
export GEMINI_API_KEY="your-key"
```

The README will document optional overrides for model name and service URLs.

## 8. Testing and Verification

Each server has direct unit tests covering successful tool results and important
input validation. The client has MVC tests for request validation and response
shape without contacting Gemini. The full Maven reactor must compile and pass
all tests on Java 21.

A documented manual smoke test starts the three servers, starts the client with
`GEMINI_API_KEY`, and calls the REST endpoint using cURL. Live Gemini invocation
is not part of the automated test suite because it requires credentials and
network access.

## 9. Operational Boundaries

- Data is mock-only and disappears when a service restarts.
- There is no payment, cancellation, inventory locking, or transaction spanning
  services.
- The agent can partially complete a workflow if a later tool fails; its final
  response must clearly disclose that state.
- MCP endpoints are unauthenticated and must not be exposed outside a trusted
  local/demo network without an authentication layer.
- SSE is retained to meet the PoC requirement; a production successor should
  evaluate authenticated Streamable HTTP.

## 10. Acceptance Criteria

- The repository reactor builds successfully with Java 21.
- Each of the four modules starts independently on its assigned port.
- The client discovers all five tools from the three SSE MCP servers.
- A Vietnamese trip request can trigger search and booking calls across all
  three services and produce a final answer containing booking confirmations.
- The README includes exact build, startup, environment, and cURL instructions.
