package com.hackathon.inditex.Controllers;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hackathon.inditex.DTO.OrderDTO;
import com.hackathon.inditex.Entities.Order;
import com.hackathon.inditex.Services.OrderService;

@RestController
public class OrderServiceController {
	@Autowired
	OrderService orderService;

	@PostMapping("/api/orders")
	public ResponseEntity<?> createOrder(@RequestBody OrderDTO orderDto) {
		Order order = orderService.createOrder(orderDto);
		Map<String, Object> response = populateResponse(order);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	private Map<String, Object> populateResponse(Order order) {
		Map<String, Object> response = new LinkedHashMap<>();
		response.put("orderId", order.getId());
		response.put("customerId", order.getCustomerId());
		response.put("size", order.getSize());
		response.put("assignedLogisticsCenter", order.getAssignedCenter());
		response.put("coordinates", order.getCoordinates());
		response.put("status", order.getStatus());
		response.put("message", "Order created successfully in PENDING status.");
		return response;
	}
	
	@GetMapping("/api/orders")
	public ResponseEntity<?> readOrders() {
		return ResponseEntity.ok(orderService.readOrders());
	}
	
	@PostMapping("/api/orders/order-assignations")
	public ResponseEntity<?> assignLogisticsCenterToOrders() {
		return ResponseEntity.ok(orderService.assignCentersToPendingOrders());
	}
}
