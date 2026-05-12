package com.example.fuhrpark.tracking.model;

import com.example.fuhrpark.model.Truck;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class TruckLocation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(optional = false)
    @JoinColumn(name = "truck_id", nullable = false, unique = true)
    private Truck truck;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private double longitude;

    @Column(nullable = false)
    private double speed;

    @Column(nullable = false)
    private Instant reportedAt;

    @Column(nullable = false)
    private Instant receivedAt;

}
