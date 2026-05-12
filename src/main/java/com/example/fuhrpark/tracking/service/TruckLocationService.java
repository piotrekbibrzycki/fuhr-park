package com.example.fuhrpark.tracking.service;

import com.example.fuhrpark.model.Truck;
import com.example.fuhrpark.repository.TruckRepository;
import com.example.fuhrpark.tracking.dto.TruckLocationMessage;
import com.example.fuhrpark.tracking.dto.TruckLocationResponseDto;
import com.example.fuhrpark.tracking.model.TruckLocation;
import com.example.fuhrpark.tracking.repository.TruckLocationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
public class TruckLocationService {
    private final TruckRepository truckRepository;
    private final TruckLocationRepository truckLocationRepository;

    public TruckLocationService(
            TruckRepository truckRepository,
            TruckLocationRepository truckLocationRepository
    ) {
        this.truckRepository = truckRepository;
        this.truckLocationRepository = truckLocationRepository;
    }

    public TruckLocationResponseDto updateLocation(TruckLocationMessage message) {
        validateMessage(message);

        Truck truck = truckRepository.findByIdAndActiveTrue(message.truckId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Truck not found or inactive: " + message.truckId()
                ));

        TruckLocation location = truckLocationRepository.findByTruckId(message.truckId())
                .orElseGet(TruckLocation::new);

        location.setTruck(truck);
        location.setLatitude(message.lat());
        location.setLongitude(message.lon());
        location.setSpeed(message.speed());
        location.setReportedAt(message.timestamp());
        location.setReceivedAt(Instant.now());

        TruckLocation savedLocation = truckLocationRepository.save(location);

        log.info(
                "Truck location updated: truckId={}, lat={}, lon={}, speed={}",
                message.truckId(),
                message.lat(),
                message.lon(),
                message.speed()
        );

        return TruckLocationResponseDto.toDto(savedLocation);
    }

    public TruckLocationResponseDto getLatestLocation(UUID truckId) {
        TruckLocation location = truckLocationRepository.findByTruckId(truckId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Location not found for truck: " + truckId
                ));

        return TruckLocationResponseDto.toDto(location);
    }

    private void validateMessage(TruckLocationMessage message) {
        if (message == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Location message must not be null");
        }
        if (message.truckId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "truckId must not be null");
        }
        if (message.lat() < -90 || message.lat() > 90) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "lat must be between -90 and 90");
        }
        if (message.lon() < -180 || message.lon() > 180) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "lon must be between -180 and 180");
        }
        if (message.speed() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "speed must not be negative");
        }
        if (message.timestamp() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "timestamp must not be null");
        }
        if (message.timestamp().isAfter(Instant.now().plusSeconds(60))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "timestamp must not be in the future");
        }
    }
}
