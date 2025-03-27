package com.hackathon.inditex.Services;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hackathon.inditex.DTO.OrderDTO;
import com.hackathon.inditex.DTO.OrderOutputDTO;
import com.hackathon.inditex.DTO.ProcessedOrdersDTO;
import com.hackathon.inditex.Entities.Center;
import com.hackathon.inditex.Entities.Coordinates;
import com.hackathon.inditex.Entities.Order;
import com.hackathon.inditex.Repositories.OrderRepository;


@Service
public class OrderServiceImpl implements OrderService {
	private static final String NO_AVAILABLE_CENTER_FOUND_FOR_THE_ORDER = "No available center found for the order.";
	private static final String ALL_CENTERS_ARE_AT_MAXIMUM_CAPACITY = "All centers are at maximum capacity.";
	private static final String NO_AVAILABLE_CENTERS_SUPPORT_THE_ORDER_TYPE = "No available centers support the order type.";
	private static final String STATUS_PENDING = "PENDING";
	private static final String STATUS_ASSIGNED = "ASSIGNED";
	private static final double EARTH_RADIUS_KM = 6371.0;
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
		order.setStatus(STATUS_PENDING);
		order.setAssignedCenter(null);
		order.setCoordinates(orderDto.coordinates());
		return order;
	}

//	@Override
//	@Transactional(readOnly = true)
//	public List<Order> readOrders() {
//		return List.copyOf((List<Order>) orderRepository.findAll());
//	}
	
	@Override
	@Transactional(readOnly = true)
	public List<OrderOutputDTO> readOrders() {
		List<Order> orderList = (List<Order>) orderRepository.findAll();
		List<OrderOutputDTO> orderDtoList = new LinkedList<>();
		for(Order o : orderList)
			orderDtoList.add(new OrderOutputDTO(o.getId(), o.getCustomerId(), o.getSize(), o.getStatus(), o.getAssignedCenter(), o.getCoordinates()));
		return orderDtoList;
	}
	
	@Override
	@Transactional
	public ProcessedOrdersDTO assignCentersToPendingOrders() {
		ProcessedOrdersDTO processedOrdersDTO = new ProcessedOrdersDTO();
		for (Order order : getPendingOrders()) {
			List<Center> centersMatchingOrderSize = getCentersMatchingOrderSize(order);
			
			if (centersMatchingOrderSize.isEmpty()) {
				processedOrdersDTO.processOrder(null, order.getId(), null,
						NO_AVAILABLE_CENTERS_SUPPORT_THE_ORDER_TYPE, STATUS_PENDING);
				continue;
			}
			
			List<Center> availableCentersMatchingOrderSize = getAvailableCenters(centersMatchingOrderSize);
			
			if (availableCentersMatchingOrderSize.isEmpty()) {
				processedOrdersDTO.processOrder(null, order.getId(), null,
						ALL_CENTERS_ARE_AT_MAXIMUM_CAPACITY, STATUS_PENDING);
				continue;
			}
			
			Map.Entry<Center, Double> assignedCenterAndDistance = availableCentersMatchingOrderSize.stream()
				    .map(center -> Map.entry(center, calculateDistance(center.getCoordinates(), order.getCoordinates())))
				    .min(Comparator.comparingDouble(Map.Entry::getValue))
				    .orElseThrow(() -> new IllegalStateException(NO_AVAILABLE_CENTER_FOUND_FOR_THE_ORDER));
			
			Center assignedCenter = assignedCenterAndDistance.getKey();
			double distance = assignedCenterAndDistance.getValue();
			
			assignAndUpdateOrderAndCenter(order, assignedCenter);
			
			processedOrdersDTO.processOrder(distance, order.getId(),
					assignedCenter.getName(), "", STATUS_ASSIGNED);
		}
		return processedOrdersDTO;
	}

	private void assignAndUpdateOrderAndCenter(Order order, Center assignedCenter) {
		order.setAssignedCenter(assignedCenter.getName());
		order.setStatus(STATUS_ASSIGNED);
		assignedCenter.setCurrentLoad(assignedCenter.getCurrentLoad() + 1);
		
		orderRepository.save(order);
		centerService.saveCenter(assignedCenter);
	}

	private List<Center> getAvailableCenters(List<Center> centerList) {
		return centerList.stream()
							.filter(c -> c.getCurrentLoad() < c.getMaxCapacity())
							.toList();
	}

	private List<Center> getCentersMatchingOrderSize(Order order) {
		return centerService.readLogisticsCenters()
								.stream()
								.filter(c -> c.getCapacity().equals(order.getSize()))
								.toList();
	}

	@Transactional(readOnly = true)
	private List<Order> getPendingOrders() {
		return orderRepository.findAll().stream()
							.filter(o -> o.getStatus().equals(STATUS_PENDING))
							.sorted(Comparator.comparingLong(o -> o.getId()))
							.toList();
	}

	private double calculateDistance(Coordinates centerCoordinates, Coordinates orderCoordinates) {
		double dLat = Math.toRadians(orderCoordinates.getLatitude() - centerCoordinates.getLatitude());
		double dLong = Math.toRadians(orderCoordinates.getLongitude() - centerCoordinates.getLongitude());

		double centerLat = Math.toRadians(centerCoordinates.getLatitude());
		double orderLat = Math.toRadians(orderCoordinates.getLatitude());

		double a = Math.pow(Math.sin(dLat / 2), 2)
				+ Math.cos(centerLat) * Math.cos(orderLat) * Math.pow(Math.sin(dLong / 2), 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return EARTH_RADIUS_KM * c;
	}

}