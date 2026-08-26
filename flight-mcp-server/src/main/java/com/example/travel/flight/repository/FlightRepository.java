package com.example.travel.flight.repository;

import com.example.travel.flight.model.Flight;
import com.example.travel.flight.model.FlightBooking;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public class FlightRepository {
    private final JdbcClient jdbc;

    public FlightRepository(JdbcClient jdbc) { this.jdbc = jdbc; }

    public List<Flight> search(String originCode, String destinationCode, LocalDate date) {
        return jdbc.sql("""
                SELECT flight_id, airline, price, departure_time, arrival_time
                FROM flights
                WHERE origin_code = :origin AND destination_code = :destination AND departure_date = :date
                ORDER BY price
                """).param("origin", originCode).param("destination", destinationCode).param("date", date)
                .query((rs, rowNum) -> Flight.builder()
                        .flightId(rs.getString("flight_id")).airline(rs.getString("airline"))
                        .price(rs.getBigDecimal("price"))
                        .departureTime(rs.getObject("departure_time", java.time.OffsetDateTime.class).toString())
                        .arrivalTime(rs.getObject("arrival_time", java.time.OffsetDateTime.class).toString()).build())
                .list();
    }

    public Optional<Flight> findById(String flightId) {
        return jdbc.sql("SELECT flight_id, airline, price, departure_time, arrival_time FROM flights WHERE flight_id = :id")
                .param("id", flightId)
                .query((rs, rowNum) -> Flight.builder().flightId(rs.getString("flight_id"))
                        .airline(rs.getString("airline")).price(rs.getBigDecimal("price"))
                        .departureTime(rs.getObject("departure_time", java.time.OffsetDateTime.class).toString())
                        .arrivalTime(rs.getObject("arrival_time", java.time.OffsetDateTime.class).toString()).build())
                .optional();
    }

    public FlightBooking saveBooking(String reference, String flightId, String passengerName, java.math.BigDecimal amount) {
        jdbc.sql("""
                INSERT INTO flight_bookings(booking_reference, flight_id, passenger_name, status, total_amount)
                VALUES (:reference, :flightId, :passengerName, 'CONFIRMED', :amount)
                """).param("reference", reference).param("flightId", flightId)
                .param("passengerName", passengerName).param("amount", amount).update();
        return FlightBooking.builder().bookingReference(reference).status("CONFIRMED").totalAmount(amount).build();
    }

    public int bookingCount() {
        return jdbc.sql("SELECT COUNT(*) FROM flight_bookings").query(Integer.class).single();
    }
}
