package com.example.fuhrpark.service;

import com.example.fuhrpark.dto.TripRequestDto;
import com.example.fuhrpark.dto.TripResponseDto;
import com.example.fuhrpark.dto.TripStatusUpdateDto;
import com.example.fuhrpark.model.Driver;
import com.example.fuhrpark.model.Trip;
import com.example.fuhrpark.model.TripStatus;
import com.example.fuhrpark.model.Truck;
import com.example.fuhrpark.repository.DriverRepository;
import com.example.fuhrpark.repository.TripRepository;
import com.example.fuhrpark.repository.TruckRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class TripService {
    private final TripRepository tripRepository;
    private final TruckRepository truckRepository;
    private final DriverRepository driverRepository;

    private static final Map<TripStatus, List<TripStatus>> ALLOWED_TRANSITIONS = Map.of(
            TripStatus.PLANNED, List.of(TripStatus.IN_PROGRESS, TripStatus.CANCELED),
            TripStatus.IN_PROGRESS, List.of(TripStatus.COMPLETED, TripStatus.CANCELED),
            TripStatus.COMPLETED, List.of(),
            TripStatus.CANCELED, List.of()
    );

    public TripService(TripRepository tripRepository, TruckRepository truckRepository, DriverRepository driverRepository) {
        this.tripRepository = tripRepository;
        this.truckRepository = truckRepository;
        this.driverRepository = driverRepository;
    }

    public Page<TripResponseDto> getTrips(int page, int size) {
        return tripRepository.findAll(PageRequest.of(page, size)).map(TripResponseDto::toDto);
    }

    public TripResponseDto getTripById(UUID id) {
        return tripRepository.findById(id).map(TripResponseDto::toDto).orElseThrow(() -> {
            log.warn("Trip not found: id={}", id);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Trasa o podanym ID nie istnieje");
        });
    }

    public List<TripResponseDto> getTripsByDriver(UUID driverId) {

        if (!driverRepository.existsById(driverId)) {
            log.warn("Trips lookup failed - driver not found: driverId ={}", driverId);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Kierowca o podanym ID nie istnieje");
        }
        return tripRepository.findByDriverId(driverId).stream().map(TripResponseDto::toDto).toList();
    }

    public TripResponseDto createTrip(TripRequestDto tripRequestDto) {
        Truck truck = truckRepository.findById(tripRequestDto.truckId()).orElseThrow(() -> {
            log.warn("Trip creation failed - truck not found: truckId={}", tripRequestDto.truckId());
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Ciężarówka o podanym ID nie istnieje.");
        });
        Driver driver = driverRepository.findById(tripRequestDto.driverId()).orElseThrow(() -> {
            log.warn("Trip creation failed - driver not found: driverId = {}", tripRequestDto.driverId());
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Kierowca o podanym ID nie istnieje.");
        });

        List<TripStatus> activeStatuses = List.of(TripStatus.PLANNED, TripStatus.IN_PROGRESS);

        if (tripRepository.existsByTruckIdAndStatusIn(truck.getId(), activeStatuses)) {
            log.warn("Trip creation failed - truck already assigned: truckId={}", tripRequestDto.truckId());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ta ciężarówka jest już przypisana do innej, niezakończonej trasy");
        }
        if (tripRepository.existsByDriverIdAndStatusIn(driver.getId(), activeStatuses)) {
            log.warn("Trip creation failed - driver already assigned: driverId={}", tripRequestDto.driverId());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ten kierowca jest już przypisany do innej, niezakończonej trasy");
        }

        Trip trip = TripRequestDto.toEntity(tripRequestDto);
        trip.setTruck(truck);
        trip.setDriver(driver);
        trip.setStatus(TripStatus.PLANNED);
        Trip savedTrip = tripRepository.save(trip);
        log.info("Trip created: id={}, {} -> {}, truckId={}, driverId={}", savedTrip.getId(), savedTrip.getStartLocation(), savedTrip.getEndLocation(), truck.getId(), driver.getId());
        return TripResponseDto.toDto(savedTrip);
    }

    public TripResponseDto updateTrip(UUID id, TripRequestDto tripRequestDto) {
        Trip trip = tripRepository.findById(id).orElseThrow(() -> {
            log.warn("Trip update failed - trip not found: id={}", id);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Trasa o podanym ID nie istnieje");
        });
        Truck truck = truckRepository.findById(tripRequestDto.truckId()).orElseThrow(() -> {
            log.warn("Trip update failed - truck not found: truckId={}", tripRequestDto.truckId());
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Ciężarówka o podanym ID nie istnieje");
        });
        Driver driver = driverRepository.findById(tripRequestDto.driverId()).orElseThrow(() -> {
            log.warn("Trip update failed - driver not found: driverId={}", tripRequestDto.driverId());
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Kierowca o podanym ID nie istnieje");
        });

        if (trip.getStatus() == TripStatus.IN_PROGRESS) {
            log.warn("Trip update failed - trip in progress: id={}", id);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nie można edytować zakończonej trasy");
        }
        List<TripStatus> activeStatuses = List.of(TripStatus.PLANNED, TripStatus.IN_PROGRESS);
        if (tripRepository.existsByTruckIdAndStatusInAndIdNot(truck.getId(), activeStatuses, id)) {
            log.warn("Trip update failed - truck already assigned: truckId={}", truck.getId());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ta ciężarówka jest już przypisana do innej, niezakończonej trasy");
        }
        if (tripRepository.existsByDriverIdAndStatusInAndIdNot(driver.getId(), activeStatuses, id)) {
            log.warn("Trip update failed - driver already assigned: driverId={}", driver.getId());
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ten kierowca jest już przypisany do innej, niezakończonej trasy");
        }

        trip.setStartLocation(tripRequestDto.startLocation());
        trip.setEndLocation(tripRequestDto.endLocation());
        trip.setDistance(tripRequestDto.distance());
        trip.setStartDate(tripRequestDto.startDate());
        trip.setTruck(truck);
        trip.setDriver(driver);
        Trip savedTrip = tripRepository.save(trip);
        log.info("Trip updated: id={}, {} -> {}", id, savedTrip.getStartLocation(), savedTrip.getEndLocation());
        return TripResponseDto.toDto(savedTrip);

    }

    public void deleteTrip(UUID id) {
        Trip trip = tripRepository.findById(id).orElseThrow(() -> {
            log.warn("Trip cancellation failed - trip not found: id={}", id);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Trasa o podanym ID nie istnieje");
        });

        if (trip.getStatus() == TripStatus.COMPLETED) {
            log.warn("Trip cancellation failed - trip already completed: id={}", id);
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Nie można anulować zakończonej trasy");
        }
        trip.setStatus(TripStatus.CANCELED);
        tripRepository.save(trip);
        log.info("Trip canceled: id={}", id);

    }

    public TripResponseDto updateTripStatus(UUID id, TripStatusUpdateDto tripStatusUpdateDto) {
        Trip trip = tripRepository.findById(id).orElseThrow(() -> {
            log.warn("Trip status update failed - trip not found: id={}", id);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Trasa o podanym ID nie istnieje");
        });

        TripStatus currentStatus = trip.getStatus();
        TripStatus newStatus = tripStatusUpdateDto.status();

        if (!ALLOWED_TRANSITIONS.get(currentStatus).contains(newStatus)) {
            log.warn("Invalid status transition: tripId={}, {} -> {}", id, currentStatus, newStatus);
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Niedozwolone przejście ze statusu :" + currentStatus + "do " + newStatus);
        }

        trip.setStatus(newStatus);
        Trip savedTrip = tripRepository.save(trip);
        log.info("Trip status updated: id={}, {} -> {}", id, currentStatus, newStatus);
        return TripResponseDto.toDto(savedTrip);
    }

}
