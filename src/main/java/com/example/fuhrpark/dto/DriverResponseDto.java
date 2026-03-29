package com.example.fuhrpark.dto;

import com.example.fuhrpark.model.Driver;

import java.util.UUID;

public record DriverResponseDto(UUID driverId, String firstName, String lastName, boolean isActive) {

    public static DriverResponseDto toDto(Driver driver) {
        return new DriverResponseDto(driver.getId(), driver.getFirstName(), driver.getLastName(), driver.isActive());
    }
}
