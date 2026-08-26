package com.example.travel.flight.tool;

import com.example.travel.flight.repository.FlightRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import static org.assertj.core.api.Assertions.*;

@JdbcTest
@Import({FlightRepository.class, FlightTools.class})
class FlightToolsTest {
    @Autowired FlightTools tools;
    @Autowired FlightRepository repository;

    @Test void searchesAndBooksFlight() {
        var flights = tools.searchFlights("Hà Nội", "Đà Nẵng", "2026-09-10");
        assertThat(flights).hasSize(3);
        assertThat(tools.bookFlight(flights.getFirst().getFlightId(), "Nguyễn Văn An").getStatus()).isEqualTo("CONFIRMED");
        assertThat(repository.bookingCount()).isEqualTo(1);
    }

    @Test void rejectsInvalidDate() {
        assertThatIllegalArgumentException().isThrownBy(() -> tools.searchFlights("HAN", "DAD", "10-09-2026"));
    }
}
