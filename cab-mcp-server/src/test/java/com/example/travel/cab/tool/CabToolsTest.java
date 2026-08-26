package com.example.travel.cab.tool;

import com.example.travel.cab.repository.CabRideRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import static org.assertj.core.api.Assertions.*;

@JdbcTest
@Import({CabRideRepository.class, CabTools.class})
class CabToolsTest {
    @Autowired CabTools tools;
    @Autowired CabRideRepository repository;

    @Test void booksCab() {
        var ride = tools.bookCab("Da Nang Airport", "Sala Hotel", "2026-09-10T12:15:00+07:00", "Nguyễn Văn An");
        assertThat(ride.getStatus()).isEqualTo("BOOKED");
        assertThat(ride.getRideId()).startsWith("RIDE-");
        assertThat(repository.rideCount()).isEqualTo(1);
    }
}
