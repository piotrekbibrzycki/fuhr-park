package com.example.fuhrpark.tracking.dto;

import java.time.Instant;
import java.util.UUID;

public record TruckLocationMessage(
        UUID truckId,
        double lat,
        double lon,
        double speed,
        Instant timestamp
) {
}
