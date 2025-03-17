package com.hackathon.inditex.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackathon.inditex.DTO.Mapper;
import com.hackathon.inditex.DTO.OrderDTO;
import com.hackathon.inditex.Entities.Order;
import com.hackathon.inditex.Handlers.ResponseHandler;
import com.hackathon.inditex.Services.OrderService;

@RestController
@RequestMapping("/api/orders")
public class OrderServiceController {
	@Autowired
	OrderService orderService;

	public Mapper mapper = new Mapper();

	// Remove this method before submitting the code !!
	@DeleteMapping("/all")
	public void deleteAllOrders() {
		List<Order> allOrders = orderService.findAll();
		allOrders.stream().forEach(order -> orderService.remove(order));
	}
	
	// 100 points
	@GetMapping("")
	public ResponseEntity<List<Order>> readOrders() {
		return new ResponseEntity<List<Order>>(orderService.findAll(), HttpStatus.OK);
	}

	// 0 points
	@PostMapping("")
	public ResponseEntity<Object> createNewOrder(@RequestBody OrderDTO orderDTO) {
		Order order = mapper.toOrder(orderDTO);
		if(order.getSize() == null || !List.of("B", "M", "S").contains(order.getSize())) {
			return ResponseHandler.generateResponse("Invalid order size, it must be a combination of B, M, S.", HttpStatus.BAD_REQUEST);
		}
		if(order.getCustomerId() != null && (order.getCoordinates().getLatitude() != null && order.getCoordinates().getLongitude() != null)) {
			orderService.save(order);
			return ResponseHandler.generateResponse(order.getId(), order.getCustomerId(), order.getSize(), order.getAssignedCenter(),
					order.getCoordinates(), order.getStatus(), "Order created successfully in " + order.getStatus() +" status.", HttpStatus.CREATED);
		} else {
			return ResponseHandler.generateResponse("Order parameters must not be null", HttpStatus.BAD_REQUEST);
		}
	}
	
}
