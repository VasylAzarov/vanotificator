package com.example.vanotificator.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "city_seq")
    @SequenceGenerator(
            name = "city_seq",
            sequenceName = "city_seq")
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private Integer timezone;

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Coordinates> coordinates = new ArrayList<>();

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Forecast> forecasts = new ArrayList<>();
}
