package com.example.fuhrpark.controller;


import com.example.fuhrpark.dto.DriverRequestDto;
import com.example.fuhrpark.dto.DriverResponseDto;
import com.example.fuhrpark.dto.TripResponseDto;
import com.example.fuhrpark.service.DriverService;
import com.example.fuhrpark.service.TripService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/drivers")
public class DriverController {
    private final DriverService driverService;
    private final TripService tripService;

    public DriverController(DriverService driverService, TripService tripService) {
        this.driverService = driverService;
        this.tripService = tripService;

    }

    @GetMapping
    public ResponseEntity<Page<DriverResponseDto>> getDrivers(@RequestParam (defaultValue = "0") int page,
                                                              @RequestParam (defaultValue = "10") int size) {
        Page<DriverResponseDto> drivers = driverService.getDrivers(page,size);
        return ResponseEntity.ok().body(drivers);

    }

    @GetMapping("/{id}/trips")
    public ResponseEntity<List<TripResponseDto>> getTripsByDriver(@PathVariable UUID id) {
        List<TripResponseDto> trips = tripService.getTripsByDriver(id);
        return ResponseEntity.ok().body(trips);
    }

    @PostMapping
    public ResponseEntity<DriverResponseDto> createDriver(@Valid @RequestBody DriverRequestDto driverRequestDto) {

        DriverResponseDto driverResponseDto = driverService.createDriver(driverRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(driverResponseDto);

    }

    @PutMapping("/{id}")
    public ResponseEntity<DriverResponseDto> updateDriver(@PathVariable UUID id, @Valid @RequestBody DriverRequestDto driverRequestDto) {
        DriverResponseDto driverResponseDto = driverService.updateDriver(id, driverRequestDto);
        return ResponseEntity.ok().body(driverResponseDto);

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable UUID id) {
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }


}
