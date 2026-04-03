package com.example.fuhrpark.service;

import com.example.fuhrpark.dto.DriverRequestDto;
import com.example.fuhrpark.dto.DriverResponseDto;
import com.example.fuhrpark.model.Driver;
import com.example.fuhrpark.repository.DriverRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Slf4j
@Service
public class DriverService {
    private final DriverRepository driverRepository;

    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    public Page<DriverResponseDto> getDrivers(int page, int size) {
        return driverRepository.findByIsActiveTrue(PageRequest.of(page,size)).map(DriverResponseDto::toDto);
    }

    public DriverResponseDto getDriverById(UUID id) {
        return driverRepository.findById(id).map(DriverResponseDto::toDto).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Kierowca o podanym ID nie istnieje"));
    }

    public DriverResponseDto createDriver(DriverRequestDto driverRequestDto) {
        Driver driver = DriverRequestDto.toEntity(driverRequestDto);
        Driver savedDriver = driverRepository.save(driver);
        log.info("Driver created: id = {}, name = {} {}", savedDriver.getId(), savedDriver.getFirstName(), savedDriver.getLastName());
        return DriverResponseDto.toDto(savedDriver);
    }

    public DriverResponseDto updateDriver(UUID id, DriverRequestDto driverRequestDto) {
        Driver driver = driverRepository.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND, "Kierowca o podanym ID nie istnieje"));
        driver.setFirstName(driverRequestDto.firstName());
        driver.setLastName(driverRequestDto.lastName());
        Driver updatedDriver = driverRepository.save(driver);
        log.info("Driver updated: id = {}, name = {} {}", updatedDriver.getId(), updatedDriver.getFirstName(), updatedDriver.getLastName());
        return DriverResponseDto.toDto(updatedDriver);
    }

    public void deleteDriver(UUID id) {
        Driver driver = driverRepository.findById(id).orElseThrow(() ->
        {
            log.warn("Driver not found for deletion: id = {}", id);
            return new ResponseStatusException(HttpStatus.NOT_FOUND, "Kierowca o podanym ID nie istnieje");
        });
        driver.setActive(false);
        driverRepository.save(driver);
        log.info("Driver soft-deleted: id = {}", id);

    }
}
