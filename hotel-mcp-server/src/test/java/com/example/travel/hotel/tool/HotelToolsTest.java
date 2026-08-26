package com.example.travel.hotel.tool;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class HotelToolsTest {
    private final HotelTools tools = new HotelTools();

    @Test void searchesAndBooksHotel() {
        var hotels = tools.searchHotels("Đà Nẵng", "2026-09-10", 3);
        assertThat(hotels).hasSize(3);
        assertThat(tools.bookHotel(hotels.getFirst().getHotelId(), "Nguyễn Văn An", "2026-09-10", 3).getStatus()).isEqualTo("CONFIRMED");
    }

    @Test void rejectsZeroNights() {
        assertThatIllegalArgumentException().isThrownBy(() -> tools.searchHotels("Đà Nẵng", "2026-09-10", 0));
    }
}
