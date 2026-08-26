# Travel Booking Agent with Spring AI MCP

Demo Java 21 gồm một agent dùng Gemini và ba MCP server độc lập kết nối qua SSE.

## Kiến trúc

| Module | Port | MCP tools |
|---|---:|---|
| `flight-mcp-server` | 8081 | `searchFlights`, `bookFlight` |
| `hotel-mcp-server` | 8082 | `searchHotels`, `bookHotel` |
| `cab-mcp-server` | 8083 | `bookCab` |
| `travel-client-agent` | 8080 | REST orchestrator + Gemini |

PoC giữ Spring Boot 3.4 và dùng Spring AI OpenAI client với endpoint OpenAI-compatible chính thức của Gemini. Không có khóa API nào được lưu trong mã nguồn.

## Yêu cầu

- JDK 21
- Maven 3.9+
- Gemini Developer API key từ Google AI Studio

## Build và test

```bash
mvn clean verify
```

## Chạy ứng dụng

Mở bốn terminal tại thư mục gốc:

```bash
mvn -pl flight-mcp-server spring-boot:run
```

```bash
mvn -pl hotel-mcp-server spring-boot:run
```

```bash
mvn -pl cab-mcp-server spring-boot:run
```

```bash
export GEMINI_API_KEY="your-gemini-api-key"
mvn -pl travel-client-agent spring-boot:run
```

Sau `mvn clean package`, có thể thay các lệnh Maven bằng:

```bash
java -jar flight-mcp-server/target/flight-mcp-server-1.0.0-SNAPSHOT.jar
java -jar hotel-mcp-server/target/hotel-mcp-server-1.0.0-SNAPSHOT.jar
java -jar cab-mcp-server/target/cab-mcp-server-1.0.0-SNAPSHOT.jar
GEMINI_API_KEY="your-gemini-api-key" java -jar travel-client-agent/target/travel-client-agent-1.0.0-SNAPSHOT.jar
```

Model mặc định là `gemini-2.5-flash`. Có thể đổi bằng `GEMINI_MODEL`. URL các server có thể đổi bằng `FLIGHT_MCP_URL`, `HOTEL_MCP_URL`, và `CAB_MCP_URL`.

## Gọi agent

```bash
curl -X POST http://localhost:8080/api/travel/plan \
  -H 'Content-Type: application/json' \
  -d '{
    "prompt": "Tôi muốn đi du lịch từ Hà Nội vào Đà Nẵng ngày 2026-09-10 trong 3 ngày, hãy đặt vé máy bay, khách sạn và xe đưa đón từ sân bay về khách sạn giúp tôi",
    "user": "Nguyễn Văn An"
  }'
```

Agent sẽ discover năm MCP tools và tự thực hiện chuỗi tìm kiếm/đặt chỗ. Vì đây là mock PoC, dữ liệu và mã xác nhận chỉ dùng để minh họa.

## Chạy toàn bộ bằng Docker Compose

Tạo file môi trường local và điền Gemini Developer API key:

```bash
cp .env.example .env
```

```dotenv
GEMINI_API_KEY=your-real-gemini-api-key
GEMINI_MODEL=gemini-2.5-flash
```

Build và khởi động toàn bộ stack:

```bash
docker compose up --build
```

Compose đợi ba MCP server healthy trước khi khởi động `travel-agent`. Kiểm tra trạng thái và log:

```bash
docker compose ps
docker compose logs -f travel-agent
curl http://localhost:8080/actuator/health
```

Sau khi client healthy, dùng cURL ở phần **Gọi agent** phía trên. Dừng và xóa container/network:

```bash
docker compose down
```

Nếu muốn xóa cả các image local do project tạo:

```bash
docker compose down --rmi local
```

## Lưu ý bảo mật

Các endpoint MCP SSE không có authentication. Chỉ chạy trên máy local hoặc mạng demo tin cậy; cần đặt Spring Security/API gateway phía trước trước khi triển khai thật. SSE legacy được giữ theo yêu cầu demo; hệ thống mới nên cân nhắc MCP Streamable HTTP.
