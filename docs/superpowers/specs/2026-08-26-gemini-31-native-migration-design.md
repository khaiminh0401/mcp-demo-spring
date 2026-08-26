# Gemini 3.1 Native Client Migration — Design Specification

## Objective

Run the travel agent with `gemini-3.1-flash-lite` and preserve Gemini thought
signatures across sequential MCP tool calls. Replace the OpenAI-compatible
adapter, which loses provider-specific signature metadata, with Spring AI's
native Google GenAI integration.

## Platform Baseline

The Maven reactor moves from Spring Boot 3.4.12 / Spring AI 1.0.3 to Spring Boot
3.5.16 / Spring AI 1.1.6. Java remains at version 21. This is the smallest stable
upgrade line that provides `spring-ai-starter-model-google-genai` while avoiding
the broader Spring Boot 4 and Spring AI 2 migration.

All four modules use the same upgraded BOMs. MCP remains on the Spring MVC SSE
transport to preserve the requested architecture. Existing H2 schemas and
database volumes remain compatible.

## Client Migration

`travel-client-agent` removes `spring-ai-starter-model-openai` and adds
`spring-ai-starter-model-google-genai`. OpenAI base URL and completions-path
properties are removed. Configuration uses:

```yaml
spring.ai.google.genai.api-key: ${GEMINI_API_KEY}
spring.ai.google.genai.chat.model: ${GEMINI_MODEL:gemini-3.1-flash-lite}
spring.ai.google.genai.chat.include-thoughts: true
```

The exact property nesting will be verified against the 1.1.6 configuration
metadata during implementation. `ChatClient` continues to receive the dynamic
MCP `ToolCallbackProvider`; the native model owns signature extraction and
passback within the tool execution loop.

## API and Benchmark Metadata

`TravelAgentService` measures elapsed wall-clock time around the complete agent
turn. A null or blank final model response is treated as an upstream failure
instead of returning HTTP 200 with `result: null`.

The successful REST response contains:

- `user`;
- `model`;
- `durationMs`;
- `result`.

The model name is injected from configuration so benchmark output identifies
the actual requested model. No automatic retry is performed because retrying a
partially completed booking workflow could create duplicate reservations.

## Docker and Documentation

`.env.example`, Compose defaults, and application defaults change to
`gemini-3.1-flash-lite`. The Docker build continues to compile the complete
reactor and the runtime topology is unchanged. The README explains native
Google GenAI configuration and benchmark fields.

## Verification

Acceptance requires:

- the Maven reactor compiles and all tests pass on Java 21;
- all four Docker images build and become healthy;
- the client completes MCP handshakes with all three servers;
- a live request performs flight, hotel, and cab tools without a missing
  `thought_signature` error;
- the final response is nonblank and reports model plus duration;
- corresponding H2 booking rows are persisted;
- no API key appears in source, logs, or committed files.
