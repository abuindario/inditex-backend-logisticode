package com.hackathon.inditex.DTO;

import org.springframework.stereotype.Component;

import com.hackathon.inditex.Entities.Center;
import com.hackathon.inditex.Entities.Coordinates;
import com.hackathon.inditex.Entities.Order;

@Component
public class Mapper {

	public Center toCenter(CenterDTO centerDTO) {
		Center center = new Center();
		center.setName(centerDTO.getName());
		center.setCapacity(centerDTO.getCapacity());
		center.setStatus(centerDTO.getStatus());
		center.setMaxCapacity(centerDTO.getMaxCapacity());
		center.setCurrentLoad(centerDTO.getCurrentLoad());
		center.setCoordinates(toCoordinates(centerDTO.getCoordinates()));
		return center;
	}
	
	public Coordinates toCoordinates(CoordinatesDTO coordinatesDTO) {
		Coordinates coordinates = new Coordinates();
		coordinates.setLatitude(coordinatesDTO.getLatitude());
		coordinates.setLongitude(coordinatesDTO.getLongitude());
		return coordinates;
	}
	
	public Order toOrder(OrderDTO orderDTO) {
		Order order = new Order();
		order.setAssignedCenter(orderDTO.getAssignedCenter());
		order.setCoordinates(toCoordinates(orderDTO.getCoordinates()));
		order.setCustomerId(orderDTO.getCustomerId());
		order.setSize(orderDTO.getSize().toUpperCase());
		order.setStatus("PENDING");
		return order;
	}

}
