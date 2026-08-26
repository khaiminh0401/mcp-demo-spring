package com.example.travel.cab.repository;

import com.example.travel.cab.model.CabRide;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

@Repository
public class CabRideRepository {
    private final JdbcClient jdbc;

    public CabRideRepository(JdbcClient jdbc) { this.jdbc = jdbc; }

    public CabRide save(CabRide ride, String pickup, String dropoff, String pickupTime, String passengerName) {
        jdbc.sql("""
                INSERT INTO cab_rides(ride_id, pickup_location, dropoff_location, pickup_time, passenger_name,
                  driver_name, license_plate, estimated_price, status)
                VALUES (:rideId, :pickup, :dropoff, :pickupTime, :passengerName, :driverName,
                  :licensePlate, :estimatedPrice, :status)
                """).param("rideId", ride.getRideId()).param("pickup", pickup).param("dropoff", dropoff)
                .param("pickupTime", pickupTime).param("passengerName", passengerName)
                .param("driverName", ride.getDriverName()).param("licensePlate", ride.getLicensePlate())
                .param("estimatedPrice", ride.getEstimatedPrice()).param("status", ride.getStatus()).update();
        return ride;
    }

    public int rideCount() { return jdbc.sql("SELECT COUNT(*) FROM cab_rides").query(Integer.class).single(); }
}
