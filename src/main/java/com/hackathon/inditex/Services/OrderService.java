package com.hackathon.inditex.Services;

import java.util.List;
import java.util.Map;

import com.hackathon.inditex.DTO.OrderDTO;
import com.hackathon.inditex.Entities.Order;

public interface OrderService {

	Order createOrder(OrderDTO orderDto);

	List<Order> readOrders();

	Map<String, Object> assignCentersToPendingOrders();

}
