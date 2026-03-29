package com.example.fuhrpark.dto;

import com.example.fuhrpark.model.Driver;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DriverRequestDto(
        @NotBlank(message = "Imię nie może być puste")
        @Size(min = 2, message = "Imię musi mieć co najmniej 2 litery")
        String firstName,
        @NotBlank(message = "Nazwisko nie może być puste")
        @Size(min = 2, message = "Nazwisko musi mieć co najmniej 2 litery")
        String lastName) {

    public static Driver toEntity(DriverRequestDto driverRequestDto) {
        Driver driver = new Driver();
        driver.setFirstName(driverRequestDto.firstName());
        driver.setLastName(driverRequestDto.lastName());
        return driver;
    }
}
