package com.hackathon.inditex.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDTO {
	private Long id;

	private Long customerId;

	private String size;

	private String status;

	private String assignedCenter;

	private CoordinatesDTO coordinates;
}
