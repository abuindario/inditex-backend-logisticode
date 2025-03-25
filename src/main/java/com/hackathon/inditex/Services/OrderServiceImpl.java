package com.hackathon.inditex.Services;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.hackathon.inditex.DTO.OrderDTO;
import com.hackathon.inditex.Entities.Center;
import com.hackathon.inditex.Entities.Coordinates;
import com.hackathon.inditex.Entities.Order;
import com.hackathon.inditex.Repositories.OrderRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CenterService centerService;

    public OrderServiceImpl(OrderRepository orderRepository, CenterService centerService) {
        this.orderRepository = orderRepository;
        this.centerService = centerService;
    }

	@Override
	@Transactional
	public Order createOrder(OrderDTO orderDto) {
		return orderRepository.save(populateOrderFromOrderDto(orderDto));
	}

	private Order populateOrderFromOrderDto(OrderDTO orderDto) {
		Order order = new Order();
		order.setCustomerId(orderDto.customerId());
		order.setSize(orderDto.size().toUpperCase());
		order.setStatus("PENDING");
		order.setAssignedCenter(null);
		order.setCoordinates(orderDto.coordinates());
		return order;
	}

	@Override
	public List<Order> readOrders() {
		return (List<Order>) orderRepository.findAll();
	}

	@Override
	@Transactional
	public Map<String, List<Map<String, Object>>> assignLogisticsCenterToOrders() {
		List<Map<String, Object>> processedOrdersList = new LinkedList<>();

		List<Order> pendingOrderList = readOrders().stream()
				.filter(o -> o.getStatus().equals("PENDING"))
				.sorted(Comparator.comparingLong(o -> o.getId()))
				.collect(Collectors.toCollection(LinkedList::new));

		OUTER: for (Order order : pendingOrderList) {
			Map<String, Object> processedOrdersMap = new LinkedHashMap<>();
			
			List<Center> centerListFilteredBySize = centerService.readLogisticsCenters().stream()
					.filter(c -> c.getCapacity().equals(order.getSize()))
					.collect(Collectors.toCollection(LinkedList::new));
			
			if(centerListFilteredBySize.size() == 0) {
				processedOrdersMap.put("distance", null); 
				processedOrdersMap.put("orderId", order.getId());
				processedOrdersMap.put("assignedLogisticsCenter", null);
				processedOrdersMap.put("message", "No available centers support the order type.");
				processedOrdersMap.put("status", "PENDING");
				processedOrdersList.add(processedOrdersMap);
				continue OUTER;
			}
			
			List<Center> availableCentersFilteredBySize = centerListFilteredBySize.stream()
					.filter(c -> c.getCurrentLoad() < c.getMaxCapacity())
					.collect(Collectors.toCollection(LinkedList::new));
			
			if(availableCentersFilteredBySize.size() == 0) {
				processedOrdersMap.put("distance", null); 
				processedOrdersMap.put("orderId", order.getId());
				processedOrdersMap.put("assignedLogisticsCenter", null);
				processedOrdersMap.put("message", "All centers are at maximum capacity.");
				processedOrdersMap.put("status", "PENDING");
				processedOrdersList.add(processedOrdersMap);
				continue OUTER;
			}
			
			Center assignedCenter = availableCentersFilteredBySize.stream()
					.min(Comparator.comparingDouble(c -> calculateDistance(c.getCoordinates(), order.getCoordinates())))
				    .orElseThrow(() -> new IllegalStateException("No available center found for the order."));
			
			order.setAssignedCenter(assignedCenter.getName()); 
			order.setStatus("ASSIGNED");

			assignedCenter.setCurrentLoad( assignedCenter.getCurrentLoad() +1 );
			  
			orderRepository.save(order); 
			centerService.saveCenter(assignedCenter);
			  
			processedOrdersMap.put("distance", calculateDistance(assignedCenter.getCoordinates(), order.getCoordinates())); 
			processedOrdersMap.put("orderId", order.getId());
			processedOrdersMap.put("assignedLogisticsCenter", assignedCenter.getName());
			processedOrdersMap.put("status", "ASSIGNED");
			  
			processedOrdersList.add(processedOrdersMap);
		}
		Map<String, List<Map<String, Object>>> response = new LinkedHashMap<>();
		response.put("processed-orders", processedOrdersList);
		return response;
	}

	private double calculateDistance(Coordinates centerCoordinates, Coordinates orderCoordinates) {
		double dLat = Math.toRadians((orderCoordinates.getLatitude() - centerCoordinates.getLatitude()));
		double dLong = Math.toRadians((orderCoordinates.getLongitude() - centerCoordinates.getLongitude()));

		double centerLat = Math.toRadians(centerCoordinates.getLatitude());
		double orderLat = Math.toRadians(orderCoordinates.getLatitude());

		double a = Math.pow(Math.sin(dLat / 2), 2)
				+ Math.cos(centerLat) * Math.cos(orderLat) * Math.pow(Math.sin(dLong / 2), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return 6371 * c;
	}

}
