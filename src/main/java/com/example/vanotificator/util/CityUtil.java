package com.example.vanotificator.util;

public class CityUtil {

    public static double roundCoordinate(double coordinate) {
        return Math.round(coordinate * 1e2) / 1e2;
    }
}
