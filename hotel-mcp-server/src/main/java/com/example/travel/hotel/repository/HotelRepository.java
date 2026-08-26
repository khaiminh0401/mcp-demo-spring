package com.example.travel.hotel.repository;

import com.example.travel.hotel.model.Hotel;
import com.example.travel.hotel.model.HotelBooking;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class HotelRepository {
    private final JdbcClient jdbc;

    public HotelRepository(JdbcClient jdbc) { this.jdbc = jdbc; }

    public List<Hotel> search(String locationCode) {
        return jdbc.sql("""
                SELECT hotel_id, name, price_per_night, rating FROM hotels
                WHERE location_code = :location ORDER BY price_per_night
                """).param("location", locationCode)
                .query((rs, rowNum) -> Hotel.builder().hotelId(rs.getString("hotel_id"))
                        .name(rs.getString("name")).pricePerNight(rs.getBigDecimal("price_per_night"))
                        .rating(rs.getDouble("rating")).build()).list();
    }

    public Optional<Hotel> findById(String hotelId) {
        return jdbc.sql("SELECT hotel_id, name, price_per_night, rating FROM hotels WHERE hotel_id = :id")
                .param("id", hotelId).query((rs, rowNum) -> Hotel.builder().hotelId(rs.getString("hotel_id"))
                        .name(rs.getString("name")).pricePerNight(rs.getBigDecimal("price_per_night"))
                        .rating(rs.getDouble("rating")).build()).optional();
    }

    public HotelBooking saveReservation(String code, String hotelId, String guestName, LocalDate checkIn,
                                        int nights, BigDecimal totalAmount) {
        jdbc.sql("""
                INSERT INTO hotel_reservations(reservation_code, hotel_id, guest_name, check_in_date,
                  nights, status, total_amount)
                VALUES (:code, :hotelId, :guestName, :checkIn, :nights, 'CONFIRMED', :totalAmount)
                """).param("code", code).param("hotelId", hotelId).param("guestName", guestName)
                .param("checkIn", checkIn).param("nights", nights).param("totalAmount", totalAmount).update();
        return HotelBooking.builder().reservationCode(code).status("CONFIRMED").build();
    }

    public int reservationCount() {
        return jdbc.sql("SELECT COUNT(*) FROM hotel_reservations").query(Integer.class).single();
    }
}
