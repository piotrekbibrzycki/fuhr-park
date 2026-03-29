package com.example.fuhrpark.controller;

import com.example.fuhrpark.dto.TruckRequestDto;
import com.example.fuhrpark.dto.TruckResponseDto;
import com.example.fuhrpark.service.TruckService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@RestController
@RequestMapping("/trucks")
public class TruckController {
    private final TruckService truckService;

    public TruckController(TruckService truckService) {
        this.truckService = truckService;
    }

    @GetMapping
    public ResponseEntity<Page<TruckResponseDto>> getTrucks(@RequestParam(defaultValue = "0") int page,
                                                            @RequestParam(defaultValue = "10") int size) {
        Page<TruckResponseDto> trucks = truckService.getTrucks(page,size);
        return ResponseEntity.ok().body(trucks);
    }

    @PostMapping
    public ResponseEntity<TruckResponseDto> createTruck(@Valid @RequestBody TruckRequestDto truckRequestDto) {
        TruckResponseDto truckResponseDto = truckService.createTruck(truckRequestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(truckResponseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TruckResponseDto> updateTruck(@PathVariable UUID id, @Valid @RequestBody TruckRequestDto truckRequestDto) {
        TruckResponseDto updatedTruck = truckService.updateTruck(id, truckRequestDto);
        return ResponseEntity.ok().body(updatedTruck);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTruck(@PathVariable UUID id) {
        truckService.deleteTruck(id);
        return ResponseEntity.noContent().build();
    }


}
