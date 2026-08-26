package com.example.travel.hotel.tool;

import com.example.travel.hotel.repository.HotelRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import static org.assertj.core.api.Assertions.*;

@JdbcTest
@Import({HotelRepository.class, HotelTools.class})
class HotelToolsTest {
    @Autowired HotelTools tools;
    @Autowired HotelRepository repository;

    @Test void searchesAndBooksHotel() {
        var hotels = tools.searchHotels("Đà Nẵng", "2026-09-10", 3);
        assertThat(hotels).hasSize(3);
        assertThat(tools.bookHotel(hotels.getFirst().getHotelId(), "Nguyễn Văn An", "2026-09-10", 3).getStatus()).isEqualTo("CONFIRMED");
        assertThat(repository.reservationCount()).isEqualTo(1);
    }

    @Test void rejectsZeroNights() {
        assertThatIllegalArgumentException().isThrownBy(() -> tools.searchHotels("Đà Nẵng", "2026-09-10", 0));
    }
}
