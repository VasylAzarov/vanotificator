package com.example.vanotificator.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Data
public class Forecast {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "forecast_seq")
    @SequenceGenerator(
            name = "forecast_seq",
            sequenceName = "forecast_seq")
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private LocalTime time;

    @Column(nullable = false)
    private DayOfWeek dayOfWeek;

    @Column(nullable = false)
    private Double temperature;

    @Column(nullable = false)
    private Double precipitation;

    @Column(nullable = false)
    private Double pop;

    @Column(nullable = false)
    private Double windSpeed;

    @Column(nullable = false)
    private Integer clouds;

    @ManyToOne
    private City city;
}
