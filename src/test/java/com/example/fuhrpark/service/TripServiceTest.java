package com.example.fuhrpark.service;

import com.example.fuhrpark.dto.TripRequestDto;
import com.example.fuhrpark.dto.TripResponseDto;
import com.example.fuhrpark.dto.TripStatusUpdateDto;
import com.example.fuhrpark.model.*;
import com.example.fuhrpark.repository.DriverRepository;
import com.example.fuhrpark.repository.TripRepository;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {

    @Mock
    private TripRepository tripRepository;

    @Mock
    private TruckRepository truckRepository;

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private TripService tripService;

    private Truck createTruck() {
        Truck truck = new Truck();
        truck.setId(UUID.randomUUID());
        truck.setLicensePlate("KR 12345");
        truck.setBrand("Volvo");
        truck.setCapacity(18.0);
        truck.setActive(true);
        return truck;
    }

    private Driver createDriver() {
        Driver driver = new Driver();
        driver.setId(UUID.randomUUID());
        driver.setFirstName("Jan");
        driver.setLastName("Kowalski");
        driver.setActive(true);
        return driver;
    }

    private Trip createTrip(Truck truck, Driver driver, TripStatus status) {
        Trip trip = new Trip();
        trip.setId(UUID.randomUUID());
        trip.setStartLocation("Kraków");
        trip.setEndLocation("Warszawa");
        trip.setDistance(300.0);
        trip.setStartDate(LocalDate.now());
        trip.setStatus(status);
        trip.setTruck(truck);
        trip.setDriver(driver);
        return trip;
    }

    @Test
    void shouldCreateTripSuccessfully() {
        Truck truck = createTruck();
        Driver driver = createDriver();

        TripRequestDto request = new TripRequestDto(
                "Kraków", "Warszawa", 300.0, LocalDate.now(),
                truck.getId(), driver.getId()
        );

        Trip savedTrip = createTrip(truck, driver, TripStatus.PLANNED);

        when(truckRepository.findByIdAndActiveTrue(truck.getId())).thenReturn(Optional.of(truck));
        when(driverRepository.findByIdAndActiveTrue(driver.getId())).thenReturn(Optional.of(driver));
        when(tripRepository.existsByTruckIdAndStatusIn(eq(truck.getId()), any())).thenReturn(false);
        when(tripRepository.existsByDriverIdAndStatusIn(eq(driver.getId()), any())).thenReturn(false);
        when(tripRepository.save(any(Trip.class))).thenReturn(savedTrip);

        TripResponseDto result = tripService.createTrip(request);

        assertNotNull(result);
        assertEquals("Kraków", result.startLocation());
        assertEquals("Warszawa", result.endLocation());
        assertEquals(TripStatus.PLANNED, result.status());
    }

    @Test
    void shouldFindTripByIdSuccessfully() {
        Truck truck = createTruck();
        Driver driver = createDriver();
        Trip trip = createTrip(truck, driver, TripStatus.PLANNED);

        when(tripRepository.findById(trip.getId())).thenReturn(Optional.of(trip));

        TripResponseDto result = tripService.getTripById(trip.getId());

        assertNotNull(result);
        assertEquals(trip.getId(), result.id());
        assertEquals("Kraków", result.startLocation());
    }

    @Test
    void shouldReturnTripsPage() {
        Truck truck = createTruck();
        Driver driver = createDriver();
        Trip trip = createTrip(truck, driver, TripStatus.PLANNED);

        Page<Trip> tripPage = new PageImpl<>(List.of(trip));
        when(tripRepository.findAll(any(PageRequest.class))).thenReturn(tripPage);

        Page<TripResponseDto> result = tripService.getTrips(0, 10);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldGetTripsByDriver() {
        Truck truck = createTruck();
        Driver driver = createDriver();
        Trip trip = createTrip(truck, driver, TripStatus.PLANNED);

        when(driverRepository.existsByIdAndActiveTrue(driver.getId())).thenReturn(true);
        when(tripRepository.findByDriverId(driver.getId())).thenReturn(List.of(trip));

        List<TripResponseDto> result = tripService.getTripsByDriver(driver.getId());

        assertEquals(1, result.size());
        assertEquals("Kraków", result.getFirst().startLocation());
    }

    @Test
    void shouldThrowWhenTripNotFound() {
        UUID tripId = UUID.randomUUID();
        when(tripRepository.findById(tripId)).thenReturn(Optional.empty());

        assertThrows(ResponseStatusException.class, () -> tripService.getTripById(tripId));
    }

    @Test
    void shouldThrowWhenTruckAlreadyAssigned() {
        Truck truck = createTruck();
        Driver driver = createDriver();

        TripRequestDto request = new TripRequestDto(
                "Kraków", "Warszawa", 300.0, LocalDate.now(),
                truck.getId(), driver.getId()
        );

        when(truckRepository.findByIdAndActiveTrue(truck.getId())).thenReturn(Optional.of(truck));
        when(driverRepository.findByIdAndActiveTrue(driver.getId())).thenReturn(Optional.of(driver));
        when(tripRepository.existsByTruckIdAndStatusIn(eq(truck.getId()), any())).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> tripService.createTrip(request));
    }

    @Test
    void shouldThrowWhenDriverAlreadyAssigned() {
        Truck truck = createTruck();
        Driver driver = createDriver();

        TripRequestDto request = new TripRequestDto(
                "Kraków", "Warszawa", 300.0, LocalDate.now(),
                truck.getId(), driver.getId()
        );

        when(truckRepository.findByIdAndActiveTrue(truck.getId())).thenReturn(Optional.of(truck));
        when(driverRepository.findByIdAndActiveTrue(driver.getId())).thenReturn(Optional.of(driver));
        when(tripRepository.existsByTruckIdAndStatusIn(eq(truck.getId()), any())).thenReturn(false);
        when(tripRepository.existsByDriverIdAndStatusIn(eq(driver.getId()), any())).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> tripService.createTrip(request));
    }

    @Test
    void shouldUpdateTripStatusSuccessfully() {
        Truck truck = createTruck();
        Driver driver = createDriver();
        Trip trip = createTrip(truck, driver, TripStatus.PLANNED);

        TripStatusUpdateDto statusUpdate = new TripStatusUpdateDto(TripStatus.IN_PROGRESS);

        when(tripRepository.findById(trip.getId())).thenReturn(Optional.of(trip));
        when(tripRepository.save(any(Trip.class))).thenReturn(trip);

        TripResponseDto result = tripService.updateTripStatus(trip.getId(), statusUpdate);

        assertNotNull(result);
        verify(tripRepository).save(argThat(t -> t.getStatus() == TripStatus.IN_PROGRESS));
    }

    @Test
    void shouldThrowOnInvalidStatusTransition() {
        Truck truck = createTruck();
        Driver driver = createDriver();
        Trip trip = createTrip(truck, driver, TripStatus.COMPLETED);

        TripStatusUpdateDto statusUpdate = new TripStatusUpdateDto(TripStatus.IN_PROGRESS);

        when(tripRepository.findById(trip.getId())).thenReturn(Optional.of(trip));

        assertThrows(ResponseStatusException.class,
                () -> tripService.updateTripStatus(trip.getId(), statusUpdate));
    }

    @Test
    void shouldCancelTrip() {
        Truck truck = createTruck();
        Driver driver = createDriver();
        Trip trip = createTrip(truck, driver, TripStatus.PLANNED);

        when(tripRepository.findById(trip.getId())).thenReturn(Optional.of(trip));
        when(tripRepository.save(any(Trip.class))).thenReturn(trip);

        tripService.deleteTrip(trip.getId());

        verify(tripRepository).save(argThat(t -> t.getStatus() == TripStatus.CANCELED));
    }

    @Test
    void shouldThrowWhenCancellingCompletedTrip() {
        Truck truck = createTruck();
        Driver driver = createDriver();
        Trip trip = createTrip(truck, driver, TripStatus.COMPLETED);

        when(tripRepository.findById(trip.getId())).thenReturn(Optional.of(trip));

        assertThrows(ResponseStatusException.class, () -> tripService.deleteTrip(trip.getId()));
    }

    @Test
    void shouldThrowWhenGettingTripsByNonExistentDriver() {
        UUID driverId = UUID.randomUUID();
        when(driverRepository.existsByIdAndActiveTrue(driverId)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> tripService.getTripsByDriver(driverId));
    }
}