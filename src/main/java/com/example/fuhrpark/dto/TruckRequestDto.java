package com.example.fuhrpark.dto;

import com.example.fuhrpark.model.Truck;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record TruckRequestDto(
        @NotBlank(message = "Tablica rejestracyjna nie może być pusta")
        String licensePlate,
        @NotBlank (message = "Marka nie może być pusta")
        String brand,
        @NotNull(message = "Pojemność nie może być pusta")
        @Positive(message = "Pojemnośc musi być większa od zera")
        Double capacity) {

    public static Truck toEntity(TruckRequestDto truckRequestDto) {
        Truck truck = new Truck();
        truck.setLicensePlate(truckRequestDto.licensePlate());
        truck.setBrand(truckRequestDto.brand());
        truck.setCapacity(truckRequestDto.capacity());
        return truck;
    }

}
