package com.example.travel.flight.tool;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class FlightToolsTest {
    private final FlightTools tools = new FlightTools();

    @Test void searchesAndBooksFlight() {
        var flights = tools.searchFlights("Hà Nội", "Đà Nẵng", "2026-09-10");
        assertThat(flights).hasSize(3);
        assertThat(tools.bookFlight(flights.getFirst().getFlightId(), "Nguyễn Văn An").getStatus()).isEqualTo("CONFIRMED");
    }

    @Test void rejectsInvalidDate() {
        assertThatIllegalArgumentException().isThrownBy(() -> tools.searchFlights("HAN", "DAD", "10-09-2026"));
    }
}
