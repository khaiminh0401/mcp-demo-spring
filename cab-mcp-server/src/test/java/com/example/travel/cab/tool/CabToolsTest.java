package com.example.travel.cab.tool;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class CabToolsTest {
    @Test void booksCab() {
        var ride = new CabTools().bookCab("Da Nang Airport", "Sala Hotel", "2026-09-10T12:15:00+07:00", "Nguyễn Văn An");
        assertThat(ride.getStatus()).isEqualTo("BOOKED");
        assertThat(ride.getRideId()).startsWith("RIDE-");
    }
}
