package com.example.travel.hotel.model;

import lombok.Builder;
import lombok.Value;
import java.math.BigDecimal;

@Value @Builder
public class Hotel { String hotelId; String name; BigDecimal pricePerNight; double rating; }
