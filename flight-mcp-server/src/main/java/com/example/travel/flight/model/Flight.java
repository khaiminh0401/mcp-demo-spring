package com.example.travel.flight.model;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class Flight {
    String flightId;
    String airline;
    BigDecimal price;
    String departureTime;
    String arrivalTime;
}
