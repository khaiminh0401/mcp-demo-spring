package com.example.travel.hotel.model;

import lombok.Builder;
import lombok.Value;

@Value @Builder
public class HotelBooking { String reservationCode; String status; }
