package com.example.fuhrpark.dto;

import com.example.fuhrpark.model.Truck;

import java.util.UUID;

public record TruckResponseDto(UUID id, String licensePlate, String brand, Double capacity, boolean isActive) {


    public static TruckResponseDto toDto(Truck truck) {
        return new TruckResponseDto(truck.getId(), truck.getLicensePlate(), truck.getBrand(),
                truck.getCapacity(), truck.isActive());
    }

}
