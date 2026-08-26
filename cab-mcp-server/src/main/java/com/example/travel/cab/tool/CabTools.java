package com.example.travel.cab.tool;

import com.example.travel.cab.model.CabRide;
import com.example.travel.cab.repository.CabRideRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

@Component
public class CabTools {
    private final CabRideRepository repository;

    public CabTools(CabRideRepository repository) { this.repository = repository; }

    @Tool(description = "Book an airport or city cab transfer for a passenger. "
            + "Đặt xe đưa đón giữa hai địa điểm vào thời gian chỉ định.")
    public CabRide bookCab(
            @ToolParam(description = "Exact pickup place, such as Đà Nẵng International Airport") String pickupLocation,
            @ToolParam(description = "Exact destination, preferably the selected hotel name") String dropoffLocation,
            @ToolParam(description = "Pickup local date and time in ISO-8601 format, for example 2026-09-10T12:00:00+07:00") String pickupTime,
            @ToolParam(description = "Passenger full name exactly as provided by the user") String passengerName) {
        requireText(pickupLocation, "pickupLocation"); requireText(dropoffLocation, "dropoffLocation");
        requireText(pickupTime, "pickupTime"); requireText(passengerName, "passengerName");
        CabRide ride = CabRide.builder()
                .rideId("RIDE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT))
                .driverName("Trần Minh Quân").licensePlate("43A-567.89")
                .estimatedPrice(new BigDecimal("185000")).status("BOOKED").build();
        return repository.save(ride, pickupLocation, dropoffLocation, pickupTime, passengerName);
    }
    private void requireText(String value, String field) { if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " must not be blank"); }
}
