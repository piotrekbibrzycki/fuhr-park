package com.example.fuhrpark.tracking.dto;

import com.example.fuhrpark.tracking.model.TruckLocation;

import java.time.Instant;
import java.util.UUID;

public record TruckLocationResponseDto(
        UUID truckId,
        String licensePlate,
        double latitude,
        double longitude,
        double speed,
        Instant reportedAt,
        Instant receivedAt
) {
    public static TruckLocationResponseDto toDto(TruckLocation location) {
        return new TruckLocationResponseDto(
                location.getTruck().getId(),
                location.getTruck().getLicensePlate(),
                location.getLatitude(),
                location.getLongitude(),
                location.getSpeed(),
                location.getReportedAt(),
                location.getReceivedAt()
        );
    }
}
