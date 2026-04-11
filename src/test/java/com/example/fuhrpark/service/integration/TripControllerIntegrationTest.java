package com.example.fuhrpark.service.integration;

import com.example.fuhrpark.model.Driver;
import com.example.fuhrpark.model.Trip;
import com.example.fuhrpark.model.TripStatus;
import com.example.fuhrpark.model.Truck;
import com.example.fuhrpark.repository.DriverRepository;
import com.example.fuhrpark.repository.TripRepository;
import com.example.fuhrpark.repository.TruckRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class TripControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DriverRepository driverRepository;

    @Autowired
    private TruckRepository truckRepository;

    @Autowired
    private TripRepository tripRepository;

    @Test
    void shouldReturnTripsFilteredByStatus() throws Exception {
        Driver testDriver = new Driver();
        testDriver.setFirstName("Test");
        testDriver.setLastName("Driver");
        testDriver.setActive(true);
        testDriver = driverRepository.save(testDriver);

        Truck testTruck = new Truck();
        testTruck.setLicensePlate("TEST-123");
        testTruck.setBrand("Volvo");
        testTruck.setCapacity(18.0);
        testTruck.setActive(true);
        testTruck = truckRepository.save(testTruck);

        Trip testTrip = new Trip();
        testTrip.setStartLocation("Cracow");
        testTrip.setEndLocation("Warsaw");
        testTrip.setDistance(300.0);
        testTrip.setStartDate(LocalDate.now());
        testTrip.setStatus(TripStatus.PLANNED);
        testTrip.setDriver(testDriver);
        testTrip.setTruck(testTruck);
        tripRepository.save(testTrip);

        mockMvc.perform(get("/trips")
                        .param("status", "PLANNED")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("PLANNED"))
                .andExpect(jsonPath("$.content[0].startLocation").value("Cracow"));
    }
}
