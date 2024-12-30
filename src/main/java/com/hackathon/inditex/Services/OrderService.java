package com.hackathon.inditex.Services;

import java.util.List;

import com.hackathon.inditex.Entities.Order;

public interface OrderService {

	public List<Order> findAll();
	
	public void save(Order order);
	
	public void remove(Order order);
}
