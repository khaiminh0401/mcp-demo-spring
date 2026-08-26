package com.example.travel.hotel.tool;

import com.example.travel.hotel.model.Hotel;
import com.example.travel.hotel.model.HotelBooking;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Component
public class HotelTools {
    private static final Set<String> HOTEL_IDS = Set.of("DAD-HYATT", "DAD-NOVOTEL", "DAD-SALA");

    @Tool(description = "Search hotels at a destination for a check-in date and number of nights. "
            + "Tìm khách sạn phù hợp trước khi gọi bookHotel.")
    public List<Hotel> searchHotels(
            @ToolParam(description = "City or area where the guest will stay") String location,
            @ToolParam(description = "Check-in date in ISO-8601 format yyyy-MM-dd") String checkInDate,
            @ToolParam(description = "Positive number of nights, not number of calendar days") int nights) {
        requireText(location, "location"); validateDate(checkInDate); validateNights(nights);
        return List.of(
                hotel("DAD-HYATT", "Hyatt Regency Danang Resort", "3200000", 4.7),
                hotel("DAD-NOVOTEL", "Novotel Danang Premier Han River", "2350000", 4.6),
                hotel("DAD-SALA", "Sala Danang Beach Hotel", "1450000", 4.5));
    }

    @Tool(description = "Book a hotel returned by searchHotels. Đặt phòng cho khách và trả về mã xác nhận.")
    public HotelBooking bookHotel(
            @ToolParam(description = "Exact hotelId returned by searchHotels") String hotelId,
            @ToolParam(description = "Guest full name exactly as provided by the user") String guestName,
            @ToolParam(description = "Check-in date in ISO-8601 format yyyy-MM-dd") String checkInDate,
            @ToolParam(description = "Positive number of nights") int nights) {
        requireText(hotelId, "hotelId"); requireText(guestName, "guestName");
        validateDate(checkInDate); validateNights(nights);
        if (!HOTEL_IDS.contains(hotelId.toUpperCase(Locale.ROOT))) throw new IllegalArgumentException("Unknown hotelId: " + hotelId);
        return HotelBooking.builder()
                .reservationCode("HTL-" + Integer.toHexString((hotelId + guestName + checkInDate).hashCode()).toUpperCase(Locale.ROOT))
                .status("CONFIRMED").build();
    }

    private Hotel hotel(String id, String name, String price, double rating) {
        return Hotel.builder().hotelId(id).name(name).pricePerNight(new BigDecimal(price)).rating(rating).build();
    }
    private void validateDate(String date) {
        try { LocalDate.parse(date); } catch (DateTimeParseException | NullPointerException ex) {
            throw new IllegalArgumentException("checkInDate must use yyyy-MM-dd", ex);
        }
    }
    private void validateNights(int nights) { if (nights < 1) throw new IllegalArgumentException("nights must be positive"); }
    private void requireText(String value, String field) { if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " must not be blank"); }
}
