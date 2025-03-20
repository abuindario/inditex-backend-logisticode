package com.hackathon.inditex.DTO;

import com.hackathon.inditex.Entities.Coordinates;

public record OrderDTO(Long customerId, String size, Coordinates coordinates) {

}
