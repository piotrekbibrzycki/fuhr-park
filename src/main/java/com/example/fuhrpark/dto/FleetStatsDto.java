package com.example.fuhrpark.dto;

import java.util.Map;

public record FleetStatsDto(int activeDrivers,
                            int activeTrucks,
                            Map<String, Long> tripsByStatus,
                            double totalDistanceCompleted,
                            String topDriverName,
                            long topDriverTrips,
                            String topTruckPlate,
                            long topTruckTrips)
{}
