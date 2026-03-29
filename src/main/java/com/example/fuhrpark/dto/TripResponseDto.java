package com.example.fuhrpark.dto;

import com.example.fuhrpark.model.Trip;
import com.example.fuhrpark.model.TripStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record TripResponseDto(UUID id, String startLocation, String endLocation, Double distance, LocalDate startDate, TripStatus status, UUID truckId, UUID driverId, LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static TripResponseDto toDto(Trip trip) {
        UUID currentTruckId = (trip.getTruck() != null) ? trip.getTruck().getId() : null;
        UUID currentDriverId = (trip.getDriver() != null) ? trip.getDriver().getId() : null;

        return new TripResponseDto(trip.getId(),
                trip.getStartLocation(),
                trip.getEndLocation(),
                trip.getDistance(),
                trip.getStartDate(),
                trip.getStatus(),
                currentTruckId,
                currentDriverId,
                trip.getCreatedAt(),
                trip.getUpdatedAt());


    }

}
