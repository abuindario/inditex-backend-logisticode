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
import com.hackathon.inditex.Services.OrderServiceImpl;

@RestController
@RequestMapping("/api/orders")
public class OrderServiceController {
	@Autowired
	OrderServiceImpl orderServiceImpl;

	public Mapper mapper = new Mapper();

	// Remove this method before submitting the code !!
	@DeleteMapping("/all")
	public void deleteAllOrders() {
		List<Order> allOrders = orderServiceImpl.findAll();
		allOrders.stream().forEach(order -> orderServiceImpl.remove(order));
	}
	
	// 100 points
	@GetMapping("")
	public ResponseEntity<List<Order>> readOrders() {
		return new ResponseEntity<List<Order>>(orderServiceImpl.findAll(), HttpStatus.OK);
	}

	// 0 points
	@PostMapping("")
	public ResponseEntity<Object> createNewOrder(@RequestBody OrderDTO orderDTO) {
		Order order = mapper.toOrder(orderDTO);
		List<String> orderSize = List.of("B", "M", "S");
		HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
		if(orderSize.contains(order.getSize()) && order.getCustomerId() != null && (order.getCoordinates().getLatitude() != null && order.getCoordinates().getLongitude() != null)) {
			orderServiceImpl.save(order);
			status = HttpStatus.CREATED;
		}
		String message = "Order created successfully in " + order.getStatus() +" status.";
		return ResponseHandler.generateResponse(order.getId(), order.getCustomerId(), order.getSize(), order.getAssignedCenter(),
				order.getCoordinates(), order.getStatus(), message, status);
	}
	
}
