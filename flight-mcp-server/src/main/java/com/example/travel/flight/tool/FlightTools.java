package com.example.travel.flight.tool;

import com.example.travel.flight.model.Flight;
import com.example.travel.flight.model.FlightBooking;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Component
public class FlightTools {
    private static final Map<String, BigDecimal> PRICES = Map.of(
            "VN-DAD-101", new BigDecimal("1850000"),
            "VJ-DAD-203", new BigDecimal("1420000"),
            "QH-DAD-305", new BigDecimal("1630000"));

    @Tool(description = "Search available flights for a route and travel date. Use this before bookFlight. "
            + "Tìm chuyến bay theo điểm đi, điểm đến và ngày khởi hành.")
    public List<Flight> searchFlights(
            @ToolParam(description = "Departure city or airport, for example Hà Nội or HAN") String from,
            @ToolParam(description = "Destination city or airport, for example Đà Nẵng or DAD") String to,
            @ToolParam(description = "Departure date in ISO-8601 format yyyy-MM-dd") String date) {
        requireText(from, "from");
        requireText(to, "to");
        parseDate(date);
        String destination = normalizeDestination(to);
        return List.of(
                flight("VN-" + destination + "-101", "Vietnam Airlines", "1850000", date, "07:10", "08:35"),
                flight("VJ-" + destination + "-203", "VietJet Air", "1420000", date, "10:20", "11:45"),
                flight("QH-" + destination + "-305", "Bamboo Airways", "1630000", date, "15:30", "16:55"));
    }

    @Tool(description = "Book one flight returned by searchFlights for the named passenger. "
            + "Đặt chuyến bay đã chọn và trả về mã xác nhận.")
    public FlightBooking bookFlight(
            @ToolParam(description = "Exact flightId returned by searchFlights") String flightId,
            @ToolParam(description = "Passenger full name exactly as provided by the user") String passengerName) {
        requireText(flightId, "flightId");
        requireText(passengerName, "passengerName");
        BigDecimal price = PRICES.entrySet().stream()
                .filter(entry -> flightId.toUpperCase(Locale.ROOT).startsWith(entry.getKey().substring(0, 2)))
                .map(Map.Entry::getValue).findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown flightId: " + flightId));
        return FlightBooking.builder()
                .bookingReference("FLT-" + Integer.toHexString((flightId + passengerName).hashCode()).toUpperCase(Locale.ROOT))
                .status("CONFIRMED").totalAmount(price).build();
    }

    private Flight flight(String id, String airline, String price, String date, String departure, String arrival) {
        return Flight.builder().flightId(id).airline(airline).price(new BigDecimal(price))
                .departureTime(date + "T" + departure + ":00+07:00")
                .arrivalTime(date + "T" + arrival + ":00+07:00").build();
    }

    private String normalizeDestination(String destination) {
        String value = destination.trim().toUpperCase(Locale.ROOT);
        return value.contains("ĐÀ NẴNG") || value.contains("DA NANG") ? "DAD" : value.replaceAll("[^A-Z]", "").substring(0, Math.min(3, value.replaceAll("[^A-Z]", "").length()));
    }

    private void parseDate(String date) {
        try { LocalDate.parse(date); } catch (DateTimeParseException | NullPointerException ex) {
            throw new IllegalArgumentException("date must use yyyy-MM-dd", ex);
        }
    }

    private void requireText(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " must not be blank");
    }
}
