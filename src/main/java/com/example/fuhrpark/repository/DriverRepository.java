package com.example.fuhrpark.repository;

import com.example.fuhrpark.model.Driver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DriverRepository extends JpaRepository<Driver, UUID> {
    Page<Driver> findByActiveTrue(Pageable pageable);
    Optional<Driver> findByIdAndActiveTrue(UUID id);
    boolean existsByIdAndActiveTrue(UUID id);
    int countByActiveTrue();
}
