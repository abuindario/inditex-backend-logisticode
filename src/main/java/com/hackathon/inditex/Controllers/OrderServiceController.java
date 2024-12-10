package com.hackathon.inditex.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackathon.inditex.DTO.Mapper;
import com.hackathon.inditex.DTO.OrderDTO;
import com.hackathon.inditex.DTO.ResponseMessage;
import com.hackathon.inditex.Entities.Order;
import com.hackathon.inditex.Services.OrderServiceImpl;

@RestController
@RequestMapping("/api/orders")
public class OrderServiceController {
	@Autowired OrderServiceImpl orderServiceImpl;
	
	public Mapper mapper = new Mapper();

//	@GetMapping("")
//	public ResponseEntity<List<Order>> readOrders() {
//		return new ResponseEntity<>(orderServiceImpl.findAll(), HttpStatus.OK);
//	}
	
	@PostMapping("") 
	public ResponseEntity<?> createNewOrder(@RequestBody OrderDTO orderDTO) {
		Order order = mapper.toOrder(orderDTO);
		if(order.getSize().length() == 1 && ( (order.getSize().toUpperCase().charAt(0) == 'B') || (order.getSize().charAt(0) == 'M') 
				|| (order.getSize().charAt(0) == 'S' ))) {
			order.setStatus("PENDING");
			orderServiceImpl.save(order);
			return new ResponseEntity<>(mapper.toResponseOrderMessage(order), HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(new ResponseMessage("Cannot create order."), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
