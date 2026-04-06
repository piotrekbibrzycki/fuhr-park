package com.example.fuhrpark.controller;

import com.example.fuhrpark.dto.FleetStatsDto;
import com.example.fuhrpark.service.FleetStatsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stats")
public class FleetStatsController {

    private final FleetStatsService fleetStatsService;


    public FleetStatsController(FleetStatsService fleetStatsService) {
        this.fleetStatsService = fleetStatsService;
    }

    @GetMapping
    public ResponseEntity<FleetStatsDto> getStats() {
        return ResponseEntity.ok(fleetStatsService.getStats());
    }
}
