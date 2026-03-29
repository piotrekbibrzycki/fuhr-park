package com.example.fuhrpark.repository;

import com.example.fuhrpark.model.Trip;
import com.example.fuhrpark.model.TripStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TripRepository extends JpaRepository<Trip, UUID> {

    List<Trip> findByDriverId(UUID driverId);
    boolean existsByDriverIdAndStatusIn(UUID id, List<TripStatus> statuses);
    boolean existsByTruckIdAndStatusIn(UUID id, List<TripStatus> statuses);
    boolean existsByDriverIdAndStatusInAndIdNot(UUID driverId, List<TripStatus> statuses, UUID tripId);
    boolean existsByTruckIdAndStatusInAndIdNot(UUID truckId, List<TripStatus> statuses, UUID tripId);
}
