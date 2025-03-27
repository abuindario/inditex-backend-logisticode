package com.hackathon.inditex.DTO;

import com.hackathon.inditex.Entities.Coordinates;

public record OrderOutputDTO(Long id, Long customerId, String size, String status, String assignedCenter, Coordinates coordinates) {

}
