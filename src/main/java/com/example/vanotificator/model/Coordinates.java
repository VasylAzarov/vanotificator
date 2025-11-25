package com.example.vanotificator.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Coordinates {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "coordinates_seq")
    @SequenceGenerator(
            name = "coordinates_seq",
            sequenceName = "coordinates_seq")
    private Long id;

    @Column(nullable = false)
    private Double lon;

    @Column(nullable = false)
    private Double lat;

    @ManyToOne
    private City city;
}
