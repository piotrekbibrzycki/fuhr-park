package com.example.fuhrpark.tracking.controller;

import com.example.fuhrpark.model.Truck;
import com.example.fuhrpark.repository.TruckRepository;
import com.example.fuhrpark.tracking.model.TruckLocation;
import com.example.fuhrpark.tracking.repository.TruckLocationRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class TruckLocationControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TruckRepository truckRepository;

    @Autowired
    private TruckLocationRepository truckLocationRepository;

    @Test
    void shouldReturnLatestTruckLocation() throws Exception {
        Truck truck = new Truck();
        truck.setLicensePlate("KR 12345");
        truck.setBrand("Volvo");
        truck.setCapacity(18.0);
        truck.setActive(true);
        truck = truckRepository.save(truck);

        TruckLocation location = new TruckLocation();
        location.setTruck(truck);
        location.setLatitude(52.2297);
        location.setLongitude(21.0122);
        location.setSpeed(72.5);
        location.setReportedAt(Instant.parse("2026-05-12T10:30:00Z"));
        location.setReceivedAt(Instant.parse("2026-05-12T10:30:03Z"));
        truckLocationRepository.save(location);

        mockMvc.perform(get("/trucks/{id}/location", truck.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.truckId").value(truck.getId().toString()))
                .andExpect(jsonPath("$.licensePlate").value("KR 12345"))
                .andExpect(jsonPath("$.latitude").value(52.2297))
                .andExpect(jsonPath("$.longitude").value(21.0122))
                .andExpect(jsonPath("$.speed").value(72.5))
                .andExpect(jsonPath("$.reportedAt").value("2026-05-12T10:30:00Z"))
                .andExpect(jsonPath("$.receivedAt").value("2026-05-12T10:30:03Z"));
    }

    @Test
    void shouldReturnNotFoundWhenLocationDoesNotExist() throws Exception {
        Truck truck = new Truck();
        truck.setLicensePlate("KR 99999");
        truck.setBrand("MAN");
        truck.setCapacity(24.0);
        truck.setActive(true);
        truck = truckRepository.save(truck);

        mockMvc.perform(get("/trucks/{id}/location", truck.getId()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").exists());
    }
}
