package com.example.fuhrpark.repository;

import com.example.fuhrpark.model.Driver;
import com.example.fuhrpark.model.TripStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.UUID;

public interface DriverRepository extends JpaRepository<Driver, UUID> {
    Page<Driver> findByIsActiveTrue(Pageable pageable);
    int countByIsActiveTrue();
}
