package com.hackathon.inditex.Controllers;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hackathon.inditex.DTO.OrderDTO;
import com.hackathon.inditex.Entities.Coordinates;
import com.hackathon.inditex.Entities.Order;
import com.hackathon.inditex.Services.OrderService;

@RestController
public class OrderServiceController {
	@Autowired
	OrderService orderService;

	@PostMapping("/api/orders")
	public ResponseEntity<OrderApiResponse> createOrder(@RequestBody OrderDTO orderDto) {
		Order order = orderService.createOrder(orderDto);
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(new OrderApiResponse(order.getId(), order.getCustomerId(), order.getSize(),
						order.getAssignedCenter(), order.getCoordinates(), order.getStatus(),
						"Order created successfully in PENDING status."));
	}
	
	@GetMapping("/api/orders")
	public ResponseEntity<List<Order>> readOrders() {
		return ResponseEntity.ok(orderService.readOrders());
	}
	
	@PostMapping("/api/orders/order-assignations")
	public ResponseEntity<Map<String, Object>> assignLogisticsCenterToOrders() {
		return ResponseEntity.ok(orderService.assignCentersToPendingOrders());
	}
}

record OrderApiResponse (Long id, Long customerId, String size, String assignedCenter, Coordinates coordinates, String status,
		String message) {}