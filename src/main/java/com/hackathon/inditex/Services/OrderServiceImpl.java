package com.hackathon.inditex.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hackathon.inditex.DTO.OrderDTO;
import com.hackathon.inditex.Entities.Order;
import com.hackathon.inditex.Repositories.OrderRepository;

@Service
public class OrderServiceImpl implements OrderService {
	@Autowired
	OrderRepository orderRepository;
	
	@Override
	public Order createOrder(OrderDTO orderDto) {
		Order order = new Order();
		order.setCustomerId(orderDto.customerId());
//		if(orderDto.size().equalsIgnoreCase("B") || 
//				orderDto.size().equalsIgnoreCase("M") ||
//				orderDto.size().equalsIgnoreCase("S")) {
			order.setSize(orderDto.size().toUpperCase());
//		} else {
//			throw new IllegalArgumentException("Invalid order size, it must be B, M or S.");
//		}
		order.setStatus("PENDING");
		order.setAssignedCenter(null);
		order.setCoordinates(orderDto.coordinates());
		
		orderRepository.save(order);
		
		return order;
	}

	@Override
	public List<Order> readOrders() {
		return (List<Order>) orderRepository.findAll();
	}

}
