package com.example.fuhrpark.tracking.service;

import com.example.fuhrpark.model.Truck;
import com.example.fuhrpark.repository.TruckRepository;
import com.example.fuhrpark.tracking.dto.TruckLocationMessage;
import com.example.fuhrpark.tracking.dto.TruckLocationResponseDto;
import com.example.fuhrpark.tracking.model.TruckLocation;
import com.example.fuhrpark.tracking.repository.TruckLocationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TruckLocationServiceTest {
    @Mock
    private TruckRepository truckRepository;

    @Mock
    private TruckLocationRepository truckLocationRepository;

    @InjectMocks
    private TruckLocationService truckLocationService;

    @Test
    void shouldCreateFirstLocationForTruck() {
        UUID truckId = UUID.randomUUID();
        Truck truck = createTruck(truckId, "KR 12345");

        TruckLocationMessage message = new TruckLocationMessage(
                truckId,
                52.2297,
                21.0122,
                72.5,
                Instant.parse("2026-05-12T10:30:00Z")
        );

        when(truckRepository.findByIdAndActiveTrue(truckId)).thenReturn(Optional.of(truck));
        when(truckLocationRepository.findByTruckId(truckId)).thenReturn(Optional.empty());
        when(truckLocationRepository.save(any(TruckLocation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TruckLocationResponseDto result = truckLocationService.updateLocation(message);

        assertEquals(truckId, result.truckId());
        assertEquals("KR 12345", result.licensePlate());
        assertEquals(52.2297, result.latitude());
        assertEquals(21.0122, result.longitude());
        assertEquals(72.5, result.speed());
        assertEquals(Instant.parse("2026-05-12T10:30:00Z"), result.reportedAt());
        assertNotNull(result.receivedAt());

        verify(truckLocationRepository).save(any(TruckLocation.class));
    }

    @Test
    void shouldUpdateExistingLocationForTruck() {
        UUID truckId = UUID.randomUUID();
        Truck truck = createTruck(truckId, "KR 12345");

        TruckLocation existingLocation = new TruckLocation();
        existingLocation.setTruck(truck);
        existingLocation.setLatitude(50.0);
        existingLocation.setLongitude(20.0);
        existingLocation.setSpeed(40.0);
        existingLocation.setReportedAt(Instant.parse("2026-05-12T10:00:00Z"));
        existingLocation.setReceivedAt(Instant.parse("2026-05-12T10:00:01Z"));

        TruckLocationMessage message = new TruckLocationMessage(
                truckId,
                53.0,
                22.0,
                80.0,
                Instant.parse("2026-05-12T10:30:00Z")
        );

        when(truckRepository.findByIdAndActiveTrue(truckId)).thenReturn(Optional.of(truck));
        when(truckLocationRepository.findByTruckId(truckId)).thenReturn(Optional.of(existingLocation));
        when(truckLocationRepository.save(any(TruckLocation.class))).thenAnswer(invocation -> invocation.getArgument(0));

        TruckLocationResponseDto result = truckLocationService.updateLocation(message);

        assertEquals(53.0, result.latitude());
        assertEquals(22.0, result.longitude());
        assertEquals(80.0, result.speed());
        assertEquals(Instant.parse("2026-05-12T10:30:00Z"), result.reportedAt());

        verify(truckLocationRepository).save(existingLocation);
    }

    @Test
    void shouldRejectInvalidLatitude() {
        TruckLocationMessage message = new TruckLocationMessage(
                UUID.randomUUID(),
                100.0,
                21.0122,
                72.5,
                Instant.parse("2026-05-12T10:30:00Z")
        );

        assertThrows(ResponseStatusException.class, () -> truckLocationService.updateLocation(message));
        verifyNoInteractions(truckRepository);
        verifyNoInteractions(truckLocationRepository);
    }

    @Test
    void shouldRejectUnknownTruck() {
        UUID truckId = UUID.randomUUID();

        TruckLocationMessage message = new TruckLocationMessage(
                truckId,
                52.2297,
                21.0122,
                72.5,
                Instant.parse("2026-05-12T10:30:00Z")
        );

        when(truckRepository.findByIdAndActiveTrue(truckId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> truckLocationService.updateLocation(message));
        verify(truckLocationRepository, never()).save(any());
    }

    private Truck createTruck(UUID truckId, String licensePlate) {
        Truck truck = new Truck();
        truck.setId(truckId);
        truck.setLicensePlate(licensePlate);
        truck.setBrand("Volvo");
        truck.setCapacity(18.0);
        truck.setActive(true);
        return truck;
    }
}
