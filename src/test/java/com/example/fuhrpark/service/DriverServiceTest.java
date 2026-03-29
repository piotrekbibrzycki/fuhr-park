package com.example.fuhrpark.service;

import com.example.fuhrpark.dto.DriverRequestDto;
import com.example.fuhrpark.dto.DriverResponseDto;
import com.example.fuhrpark.model.Driver;
import com.example.fuhrpark.repository.DriverRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DriverServiceTest {

    @Mock
    private DriverRepository driverRepository;

    @InjectMocks
    private DriverService driverService;

    @Test
    void shouldCreateDriverSuccessfully() {
        DriverRequestDto requestDto = new DriverRequestDto("Jan", "Kowalski");

        Driver savedDriver = new Driver();
        savedDriver.setId(UUID.randomUUID());
        savedDriver.setFirstName("Jan");
        savedDriver.setLastName("Kowalski");
        savedDriver.setActive(true);

        when(driverRepository.save(any(Driver.class))).thenReturn(savedDriver);

        DriverResponseDto result = driverService.createDriver(requestDto);

        assertNotNull(result.driverId());
        assertEquals("Jan", result.firstName());
        assertEquals("Kowalski", result.lastName());

        verify(driverRepository, times(1)).save(any(Driver.class));
    }

    @Test
    void shouldFindDriverSuccessfully() {
        UUID driverId = UUID.randomUUID();
        Driver driver = new Driver();
        driver.setId(driverId);
        driver.setFirstName("Jan");
        driver.setLastName("Kowalski");

        when(driverRepository.findById(driverId)).thenReturn(Optional.of(driver));

        DriverResponseDto result = driverService.getDriverById(driverId);

        assertNotNull(result);
        assertEquals(driverId, result.driverId());
        assertEquals("Jan",result.firstName());
        verify(driverRepository, times(1)).findById(driverId);

    }

    @Test
    void shouldReturnActiveDriversPage() {

        int page = 0;
        int size = 10;

        Driver driver = new Driver();
        driver.setId(UUID.randomUUID());
        driver.setFirstName("Jan");
        driver.setLastName("Kowalski");
        driver.setActive(true);

        Page<Driver> driverPage = new PageImpl<>(List.of(driver));

        when(driverRepository.findByIsActiveTrue(any(PageRequest.class))).thenReturn(driverPage);

        Page<DriverResponseDto> result = driverService.getDrivers(page,size);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Jan", result.getContent().getFirst().firstName());

        verify(driverRepository, times(1)).findByIsActiveTrue(any(PageRequest.class));
    }

    @Test
    void shouldUpdateDriverSuccesfully() {
        UUID driverId = UUID.randomUUID();

        Driver existingDriver = new Driver();
        existingDriver.setId(driverId);
        existingDriver.setFirstName("oldName");
        existingDriver.setLastName("oldSurname");

        DriverRequestDto updateRequest = new DriverRequestDto("newName", "newSurname");

        Driver updatedDriver = new Driver();
        updatedDriver.setId(driverId);
        updatedDriver.setFirstName("newName");
        updatedDriver.setLastName("newSurname");
        updatedDriver.setActive(true);

        when(driverRepository.findById(driverId)).thenReturn(Optional.of(existingDriver));
        when(driverRepository.save(any(Driver.class))).thenReturn(updatedDriver);

        DriverResponseDto result = driverService.updateDriver(driverId, updateRequest);

        assertNotNull(result);
        assertEquals("newName", result.firstName());
        assertEquals("newSurname", result.lastName());
    }
}