package com.example.fuhrpark.tracking.controller;

import com.example.fuhrpark.tracking.dto.TruckLocationResponseDto;
import com.example.fuhrpark.tracking.service.TruckLocationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/trucks")
public class TruckLocationController {
    private final TruckLocationService truckLocationService;

    public TruckLocationController(TruckLocationService truckLocationService) {
        this.truckLocationService = truckLocationService;
    }

    @GetMapping("/{id}/location")
    public ResponseEntity<TruckLocationResponseDto> getTruckLocation(@PathVariable UUID id) {
        TruckLocationResponseDto location = truckLocationService.getLatestLocation(id);
        return ResponseEntity.ok(location);
    }
}
