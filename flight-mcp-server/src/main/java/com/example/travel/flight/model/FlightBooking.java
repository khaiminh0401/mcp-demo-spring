package com.example.travel.flight.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class FlightBooking {
    String bookingReference;
    String status;
    BigDecimal totalAmount;
}
