package com.hackathon.inditex.DTO;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProcessedOrdersDTO {
	@JsonProperty("processed-orders")
	private List<Map<String, Object>> processedOrders;
	
	public ProcessedOrdersDTO() {
		this.processedOrders = new LinkedList<>();
	}
	
	public void processOrder(Double distance, Long orderId, String centerName,
			String message, String status) {
		Map<String, Object> processedOrdersMap = new LinkedHashMap<>();
		processedOrdersMap.put("distance", distance);
		processedOrdersMap.put("orderId", orderId);
		processedOrdersMap.put("assignedLogisticsCenter", centerName);
		if (!message.isBlank())
			processedOrdersMap.put("message", message);
		processedOrdersMap.put("status", status);
		processedOrders.add(processedOrdersMap);
	}
}
