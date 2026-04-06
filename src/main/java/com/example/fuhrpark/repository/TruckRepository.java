package com.example.fuhrpark.repository;


import com.example.fuhrpark.model.Driver;
import com.example.fuhrpark.model.Truck;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


public interface TruckRepository extends JpaRepository<Truck, UUID> {
    Page<Truck> findByIsActiveTrue(Pageable pageable);

    int countByIsActiveTrue();

}