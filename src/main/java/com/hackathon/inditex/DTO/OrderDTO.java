package com.hackathon.inditex.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDTO {
	private Long customerId;
	private String size;
	private String assignedCenter;
	private CoordinatesDTO coordinates;
}
