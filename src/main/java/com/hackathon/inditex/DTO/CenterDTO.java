package com.hackathon.inditex.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class CenterDTO {
	private String name; 
	private String capacity; 
	private String status; 
	private Integer maxCapacity;
	private Integer currentLoad;
	private CoordinatesDTO coordinates;
}
