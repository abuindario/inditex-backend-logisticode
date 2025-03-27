package com.hackathon.inditex.Services;

import java.util.List;

import com.hackathon.inditex.DTO.OrderDTO;
import com.hackathon.inditex.DTO.OrderOutputDTO;
import com.hackathon.inditex.DTO.ProcessedOrdersDTO;
import com.hackathon.inditex.Entities.Order;

public interface OrderService {

	Order createOrder(OrderDTO orderDto);

	List<OrderOutputDTO> readOrders();

	ProcessedOrdersDTO assignCentersToPendingOrders();

}
