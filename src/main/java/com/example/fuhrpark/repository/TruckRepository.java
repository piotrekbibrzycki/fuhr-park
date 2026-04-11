package com.example.fuhrpark.repository;


import com.example.fuhrpark.model.Truck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;


public interface TruckRepository extends JpaRepository<Truck, UUID> {
    Page<Truck> findByActiveTrue(Pageable pageable);
    Optional<Truck> findByIdAndActiveTrue(UUID id);
    int countByActiveTrue();

}