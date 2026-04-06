package com.example.fuhrpark.service;

import com.example.fuhrpark.dto.FleetStatsDto;
import com.example.fuhrpark.model.Trip;
import com.example.fuhrpark.model.TripStatus;
import com.example.fuhrpark.repository.DriverRepository;
import com.example.fuhrpark.repository.TripRepository;
import com.example.fuhrpark.repository.TruckRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FleetStatsService {

    private final DriverRepository driverRepository;
    private final TruckRepository truckRepository;
    private final TripRepository tripRepository;


    public FleetStatsService(DriverRepository driverRepository, TruckRepository truckRepository, TripRepository tripRepository) {
        this.driverRepository = driverRepository;
        this.truckRepository = truckRepository;
        this.tripRepository = tripRepository;
    }


    public FleetStatsDto getStats() {
        int activeDrivers = driverRepository.countByIsActiveTrue();
        int activeTrucks = truckRepository.countByIsActiveTrue();

        List<Trip> allTrips = tripRepository.findAll();

        Map<String, Long> tripsByStatus = allTrips.stream().collect(Collectors.groupingBy(trip -> trip.getStatus().name(), Collectors.counting()));
        double totalDistance = allTrips.stream().filter(trip -> trip.getStatus() == TripStatus.COMPLETED).mapToDouble(Trip::getDistance).sum();
        List<Trip> completedTrips = allTrips.stream().filter(trip -> trip.getStatus() == TripStatus.COMPLETED).toList();

        String topDriverName = "none";
        long topDriverTrips = 0;
        if (!completedTrips.isEmpty()) {
            var topDriver = completedTrips.stream().collect(Collectors.groupingBy(trip -> trip.getDriver().getId(), Collectors.counting())).entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null);

            if (topDriver != null) {
                topDriverTrips = topDriver.getValue();
                topDriverName = completedTrips.stream().filter(trip -> trip.getDriver().getId().equals(topDriver.getKey())).findFirst().map(trip -> trip.getDriver().getFirstName() + " " + trip.getDriver().getLastName()).orElse("none");
            }
        }

        String topTruckPlate = "none";
        long topTruckTrips = 0;
        if (!completedTrips.isEmpty()) {

            var topTruck = completedTrips.stream().collect(Collectors.groupingBy(trip -> trip.getTruck().getLicensePlate(), Collectors.counting())).entrySet().stream().max(Map.Entry.comparingByValue()).orElse(null);


            if (topTruck != null) {
                topTruckPlate = topTruck.getKey();
                topTruckTrips = topTruck.getValue();

            }
        }

        return new FleetStatsDto(activeDrivers, activeTrucks, tripsByStatus, totalDistance, topDriverName, topDriverTrips, topTruckPlate, topTruckTrips);

    }

}
