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
import com.hackathon.inditex.DTO.ResponseOrderMessage;
import com.hackathon.inditex.Entities.Order;
import com.hackathon.inditex.Services.OrderServiceImpl;

@RestController
@RequestMapping("/api/orders")
public class OrderServiceController {
	@Autowired
	OrderServiceImpl orderServiceImpl;

	public Mapper mapper = new Mapper();

	@GetMapping("")
	public ResponseEntity<List<Order>> readOrders() {
		return new ResponseEntity<>(orderServiceImpl.findAll(), HttpStatus.OK);
	}

	@PostMapping("")
	public ResponseEntity<ResponseOrderMessage> createNewOrder(@RequestBody OrderDTO orderDTO) {
		Order order = mapper.toOrder(orderDTO);
		List<String> orderSize = List.of("B", "M", "S");
		HttpStatus status = HttpStatus.BAD_REQUEST;
		if(orderSize.contains(order.getSize())) {
			orderServiceImpl.save(order);
			status = HttpStatus.CREATED;
		}
		return new ResponseEntity<>(mapper.toResponseOrderMessage(order), status);

	}
}
