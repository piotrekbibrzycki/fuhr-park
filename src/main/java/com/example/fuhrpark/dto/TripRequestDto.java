package com.example.fuhrpark.dto;

import com.example.fuhrpark.model.Trip;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;
import java.util.UUID;

public record TripRequestDto(
        @NotBlank(message = "Początkowa lokalizacja nie może być pusta")
        String startLocation,
        @NotBlank(message = "Końcowa lokalizacja nie może być pusta")
        String endLocation,
        @NotNull(message = "Dystans nie może być pusty")
        @Positive(message = "Dystans musi być większy od 0")
        Double distance,
        @NotNull(message = "Początkowa data nie może być pusta")
        LocalDate startDate,
        @NotNull(message = "ID ciężarówki nie może być puste")
        UUID truckId,
        @NotNull(message = "ID kierowcy nie może być puste")
        UUID driverId) {

    public static Trip toEntity(TripRequestDto tripRequestDto) {
        Trip trip = new Trip();
        trip.setStartLocation(tripRequestDto.startLocation());
        trip.setEndLocation(tripRequestDto.endLocation());
        trip.setDistance(tripRequestDto.distance());
        trip.setStartDate(tripRequestDto.startDate());
        return trip;
    }

}
