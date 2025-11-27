package com.example.vanotificator.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class CityLocaleName {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "city_local_name_seq")
    @SequenceGenerator(
            name = "city_local_name_seq",
            sequenceName = "city_local_name_seq")
    private Long id;

    @Column(nullable = false)
    private String locale;

    @Column(nullable = false)
    private String localeName;

    @ManyToOne
    private City city;
}
