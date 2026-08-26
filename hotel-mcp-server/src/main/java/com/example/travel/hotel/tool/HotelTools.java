package com.example.travel.hotel.tool;

import com.example.travel.hotel.model.Hotel;
import com.example.travel.hotel.model.HotelBooking;
import com.example.travel.hotel.repository.HotelRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Component
public class HotelTools {
    private final HotelRepository repository;

    public HotelTools(HotelRepository repository) { this.repository = repository; }

    @Tool(description = "Search hotels at a destination for a check-in date and number of nights. "
            + "Tìm khách sạn phù hợp trước khi gọi bookHotel.")
    public List<Hotel> searchHotels(
            @ToolParam(description = "City or area where the guest will stay") String location,
            @ToolParam(description = "Check-in date in ISO-8601 format yyyy-MM-dd") String checkInDate,
            @ToolParam(description = "Positive number of nights, not number of calendar days") int nights) {
        requireText(location, "location"); validateDate(checkInDate); validateNights(nights);
        return repository.search(normalizeLocation(location));
    }

    @Tool(description = "Book a hotel returned by searchHotels. Đặt phòng cho khách và trả về mã xác nhận.")
    public HotelBooking bookHotel(
            @ToolParam(description = "Exact hotelId returned by searchHotels") String hotelId,
            @ToolParam(description = "Guest full name exactly as provided by the user") String guestName,
            @ToolParam(description = "Check-in date in ISO-8601 format yyyy-MM-dd") String checkInDate,
            @ToolParam(description = "Positive number of nights") int nights) {
        requireText(hotelId, "hotelId"); requireText(guestName, "guestName");
        LocalDate date = validateDate(checkInDate); validateNights(nights);
        Hotel hotel = repository.findById(hotelId.toUpperCase(Locale.ROOT))
                .orElseThrow(() -> new IllegalArgumentException("Unknown hotelId: " + hotelId));
        String code = "HTL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
        return repository.saveReservation(code, hotel.getHotelId(), guestName, date, nights,
                hotel.getPricePerNight().multiply(java.math.BigDecimal.valueOf(nights)));
    }

    private String normalizeLocation(String location) {
        String value = location.trim().toUpperCase(Locale.ROOT);
        return value.equals("DAD") || value.contains("ĐÀ NẴNG") || value.contains("DA NANG") ? "DAD" : value;
    }
    private LocalDate validateDate(String date) {
        try { return LocalDate.parse(date); } catch (DateTimeParseException | NullPointerException ex) {
            throw new IllegalArgumentException("checkInDate must use yyyy-MM-dd", ex);
        }
    }
    private void validateNights(int nights) { if (nights < 1) throw new IllegalArgumentException("nights must be positive"); }
    private void requireText(String value, String field) { if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " must not be blank"); }
}
