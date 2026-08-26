package com.example.travel.flight.tool;

import com.example.travel.flight.model.Flight;
import com.example.travel.flight.model.FlightBooking;
import com.example.travel.flight.repository.FlightRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.List;
import java.util.UUID;

@Component
public class FlightTools {
    private final FlightRepository repository;

    public FlightTools(FlightRepository repository) { this.repository = repository; }

    @Tool(description = "Search available flights for a route and travel date. Use this before bookFlight. "
            + "Tìm chuyến bay theo điểm đi, điểm đến và ngày khởi hành.")
    public List<Flight> searchFlights(
            @ToolParam(description = "Departure city or airport, for example Hà Nội or HAN") String from,
            @ToolParam(description = "Destination city or airport, for example Đà Nẵng or DAD") String to,
            @ToolParam(description = "Departure date in ISO-8601 format yyyy-MM-dd") String date) {
        requireText(from, "from");
        requireText(to, "to");
        return repository.search(normalizeAirport(from), normalizeAirport(to), parseDate(date));
    }

    @Tool(description = "Book one flight returned by searchFlights for the named passenger. "
            + "Đặt chuyến bay đã chọn và trả về mã xác nhận.")
    public FlightBooking bookFlight(
            @ToolParam(description = "Exact flightId returned by searchFlights") String flightId,
            @ToolParam(description = "Passenger full name exactly as provided by the user") String passengerName) {
        requireText(flightId, "flightId");
        requireText(passengerName, "passengerName");
        Flight flight = repository.findById(flightId)
                .orElseThrow(() -> new IllegalArgumentException("Unknown flightId: " + flightId));
        String reference = "FLT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        return repository.saveBooking(reference, flightId, passengerName, flight.getPrice());
    }

    private String normalizeAirport(String value) {
        String normalized = value.trim().toUpperCase(Locale.ROOT);
        if (normalized.equals("HAN") || normalized.contains("HÀ NỘI") || normalized.contains("HA NOI")) return "HAN";
        if (normalized.equals("DAD") || normalized.contains("ĐÀ NẴNG") || normalized.contains("DA NANG")) return "DAD";
        return normalized;
    }

    private LocalDate parseDate(String date) {
        try { return LocalDate.parse(date); } catch (DateTimeParseException | NullPointerException ex) {
            throw new IllegalArgumentException("date must use yyyy-MM-dd", ex);
        }
    }

    private void requireText(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " must not be blank");
    }
}
