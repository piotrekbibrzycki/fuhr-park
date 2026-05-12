package com.example.fuhrpark.tracking.repository;

import com.example.fuhrpark.tracking.model.TruckLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface TruckLocationRepository extends JpaRepository<TruckLocation, UUID> {
    Optional<TruckLocation> findByTruckId(UUID truckId);
}
