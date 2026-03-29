package com.example.fuhrpark.controller;

import com.example.fuhrpark.dto.TripRequestDto;
import com.example.fuhrpark.dto.TripResponseDto;
import com.example.fuhrpark.dto.TripStatusUpdateDto;
import com.example.fuhrpark.service.TripService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
public class TripController {
    private final TripService tripService;

    public TripController(TripService tripService) {
        this.tripService = tripService;
    }

    @GetMapping
    public ResponseEntity<Page<TripResponseDto>> getTrips(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "10") int size) {
        Page<TripResponseDto> trips = tripService.getTrips(page,size);
        return ResponseEntity.ok().body(trips);

    }

    @PostMapping
    public ResponseEntity<TripResponseDto> createTrip(@Valid @RequestBody TripRequestDto tripRequestDto) {
        TripResponseDto tripResponseDto = tripService.createTrip(tripRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(tripResponseDto);


    }

    @PutMapping("/{id}")
    public ResponseEntity<TripResponseDto> updateTrip(@PathVariable UUID id, @Valid @RequestBody TripRequestDto tripRequestDto) {
        TripResponseDto tripResponseDto = tripService.updateTrip(id, tripRequestDto);
        return ResponseEntity.ok().body(tripResponseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrip(@PathVariable UUID id) {
        tripService.deleteTrip(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<TripResponseDto> updateTripStatus(@PathVariable UUID id, @Valid @RequestBody TripStatusUpdateDto tripStatusUpdateDto) {
        TripResponseDto updatedTrip = tripService.updateTripStatus(id, tripStatusUpdateDto);
        return ResponseEntity.ok(updatedTrip);


    }


}
