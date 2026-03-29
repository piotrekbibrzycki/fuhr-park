package com.example.fuhrpark.dto;

import com.example.fuhrpark.model.TripStatus;
import jakarta.validation.constraints.NotNull;

public record TripStatusUpdateDto(
        @NotNull(message = "Status nie może być pusty")
        TripStatus status
) {
}
