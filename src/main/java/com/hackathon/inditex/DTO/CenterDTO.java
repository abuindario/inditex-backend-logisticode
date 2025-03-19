package com.hackathon.inditex.DTO;

import com.hackathon.inditex.Entities.Coordinates;

public record CenterDTO(String name, String capacity, String status, int maxCapacity, int currentLoad, Coordinates coordinates) {

}
