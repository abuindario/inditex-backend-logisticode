package com.hackathon.inditex.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CenterDTO {
	private String name; 
	private String capacity; 
	private String status; 
	private Integer maxCapacity;
	private Integer currentLoad;
	private CoordinatesDTO coordinates;
	
}
