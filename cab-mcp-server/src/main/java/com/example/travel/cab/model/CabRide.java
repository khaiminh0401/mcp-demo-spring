package com.example.travel.cab.model;

import lombok.Builder;
import lombok.Value;
import java.math.BigDecimal;

@Value @Builder
public class CabRide {
    String rideId; String driverName; String licensePlate; BigDecimal estimatedPrice; String status;
}
