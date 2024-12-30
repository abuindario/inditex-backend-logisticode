package com.hackathon.inditex.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hackathon.inditex.Entities.Order;
import com.hackathon.inditex.Repositories.OrderRepository;

@Service
public class OrderServiceImpl implements OrderService{
	@Autowired OrderRepository orderRepository;

	@Override
	public List<Order> findAll() {
		return (List<Order>) orderRepository.findAll();
	}

	@Override
	public void save(Order order) {
		orderRepository.save(order);
	}

	@Override
	public void remove(Order order) {
		orderRepository.delete(order);
	}
	
	
}
