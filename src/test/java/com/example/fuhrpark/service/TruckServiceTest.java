package com.example.fuhrpark.service;

import com.example.fuhrpark.dto.TruckRequestDto;
import com.example.fuhrpark.dto.TruckResponseDto;
import com.example.fuhrpark.model.Truck;
import com.example.fuhrpark.repository.TruckRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TruckServiceTest {

    @Mock
    private TruckRepository truckRepository;

    @InjectMocks
    private TruckService truckService;

    @Test
    void shouldCreateTruckSuccessfully() {
        TruckRequestDto request = new TruckRequestDto("KR 12345", "Volvo", 18.0);

        Truck savedTruck = new Truck();
        savedTruck.setId(UUID.randomUUID());
        savedTruck.setLicensePlate("KR 12345");
        savedTruck.setBrand("Volvo");
        savedTruck.setCapacity(18.0);
        savedTruck.setActive(true);

        when(truckRepository.save(any(Truck.class))).thenReturn(savedTruck);

        TruckResponseDto result = truckService.createTruck(request);

        assertNotNull(result.id());
        assertEquals("KR 12345", result.licensePlate());
        assertEquals("Volvo", result.brand());
        assertEquals(18.0, result.capacity());
        verify(truckRepository, times(1)).save(any(Truck.class));
    }

    @Test
    void shouldFindTruckByIdSuccessfully() {
        UUID truckId = UUID.randomUUID();
        Truck truck = new Truck();
        truck.setId(truckId);
        truck.setLicensePlate("KR 12345");
        truck.setBrand("Volvo");
        truck.setCapacity(18.0);

        when(truckRepository.findById(truckId)).thenReturn(Optional.of(truck));

        TruckResponseDto result = truckService.getTruckById(truckId);

        assertNotNull(result);
        assertEquals(truckId, result.id());
        assertEquals("Volvo", result.brand());
        verify(truckRepository, times(1)).findById(truckId);
    }

    @Test
    void shouldReturnActiveTrucksPage() {
        Truck truck = new Truck();
        truck.setId(UUID.randomUUID());
        truck.setLicensePlate("KR 12345");
        truck.setBrand("Volvo");
        truck.setCapacity(18.0);
        truck.setActive(true);

        Page<Truck> truckPage = new PageImpl<>(List.of(truck));

        when(truckRepository.findByIsActiveTrue(any(PageRequest.class))).thenReturn(truckPage);

        Page<TruckResponseDto> result = truckService.getTrucks(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Volvo", result.getContent().getFirst().brand());
        verify(truckRepository, times(1)).findByIsActiveTrue(any(PageRequest.class));
    }

    @Test
    void shouldUpdateTruckSuccessfully() {
        UUID truckId = UUID.randomUUID();

        Truck existingTruck = new Truck();
        existingTruck.setId(truckId);
        existingTruck.setLicensePlate("KR 12345");
        existingTruck.setBrand("Volvo");
        existingTruck.setCapacity(18.0);

        TruckRequestDto updateRequest = new TruckRequestDto("WA 99999", "MAN", 24.0);

        Truck updatedTruck = new Truck();
        updatedTruck.setId(truckId);
        updatedTruck.setLicensePlate("WA 99999");
        updatedTruck.setBrand("MAN");
        updatedTruck.setCapacity(24.0);
        updatedTruck.setActive(true);

        when(truckRepository.findById(truckId)).thenReturn(Optional.of(existingTruck));
        when(truckRepository.save(any(Truck.class))).thenReturn(updatedTruck);

        TruckResponseDto result = truckService.updateTruck(truckId, updateRequest);

        assertNotNull(result);
        assertEquals("WA 99999", result.licensePlate());
        assertEquals("MAN", result.brand());
        assertEquals(24.0, result.capacity());
    }

    @Test
    void shouldThrowExceptionWhenTruckNotFound() {
        UUID truckId = UUID.randomUUID();
        when(truckRepository.findById(truckId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> truckService.getTruckById(truckId));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentTruck() {
        UUID truckId = UUID.randomUUID();
        TruckRequestDto request = new TruckRequestDto("KR 12345", "Volvo", 18.0);
        when(truckRepository.findById(truckId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> truckService.updateTruck(truckId, request));
    }

    @Test
    void shouldSoftDeleteTruck() {
        UUID truckId = UUID.randomUUID();
        Truck truck = new Truck();
        truck.setId(truckId);
        truck.setActive(true);

        when(truckRepository.findById(truckId)).thenReturn(Optional.of(truck));
        when(truckRepository.save(any(Truck.class))).thenReturn(truck);

        truckService.deleteTruck(truckId);

        verify(truckRepository).save(argThat(t -> !t.isActive()));
    }
}