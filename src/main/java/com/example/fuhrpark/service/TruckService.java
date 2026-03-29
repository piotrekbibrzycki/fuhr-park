package com.example.fuhrpark.service;

import com.example.fuhrpark.dto.TruckRequestDto;
import com.example.fuhrpark.dto.TruckResponseDto;
import com.example.fuhrpark.model.Truck;
import com.example.fuhrpark.repository.TruckRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TruckService {
    private final TruckRepository truckRepository;

    public TruckService(TruckRepository truckRepository) {
        this.truckRepository = truckRepository;
    }

    public Page<TruckResponseDto> getTrucks(int page, int size) {
        return truckRepository.findByIsActiveTrue(PageRequest.of(page,size)).map(TruckResponseDto::toDto);
    }

    public TruckResponseDto createTruck(TruckRequestDto truckRequestDto) {
        Truck truck = TruckRequestDto.toEntity(truckRequestDto);
        Truck savedTruck = truckRepository.save(truck);
        return TruckResponseDto.toDto(savedTruck);
    }

    public TruckResponseDto updateTruck(UUID id, TruckRequestDto truckRequestDto) {
        Truck truck = truckRepository.findById(id).orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ciężarówka o podanym ID nie istnieje."));
        truck.setLicensePlate(truckRequestDto.licensePlate());
        truck.setBrand(truckRequestDto.brand());
        truck.setCapacity(truckRequestDto.capacity());

        Truck savedTruck = truckRepository.save(truck);
        return TruckResponseDto.toDto(savedTruck);
    }

    public void deleteTruck(UUID id) {
        Truck truck = truckRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ciężarówka o podanym ID nie istnieje."));
        truck.setActive(false);
        truckRepository.save(truck);

    }
}
