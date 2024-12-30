package com.hackathon.inditex.handler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.hackathon.inditex.Entities.Coordinates;

public class ResponseHandler {
	
	public static ResponseEntity<Object> generateResponse(List<Map<String, Object>> list, HttpStatus statusCode) {
		Map<String, List<Map<String,Object>>> map = new LinkedHashMap<>();
		map.put("processed-orders", list);
		return new ResponseEntity<Object>(map, statusCode);
	}

	public static Map<String, Object> generateResponse(Double distance, Long orderId, 
			String assignedLogisticsCenter, String message, String status) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("distance", distance);
		map.put("orderId", orderId);
		map.put("assignedLogisticsCenter", assignedLogisticsCenter);
		map.put("message", message);
		map.put("status", status);
		return map;
	}
	
	public static Map<String, Object> generateResponse(Double distance, Long orderId, 
			String assignedLogisticsCenter, String status) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("distance", distance);
		map.put("orderId", orderId);
		map.put("assignedLogisticsCenter", assignedLogisticsCenter);
		map.put("status", status);
		return map;
	}
	
	public static ResponseEntity<Object> generateResponse(String message, HttpStatus statusCode) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("message", message);
		return new ResponseEntity<Object>(map, statusCode);
	}
	public static ResponseEntity<Object> generateResponse(Long orderId, Long customerId, String size,
			String assignedLogisticsCenter, Coordinates coordinates, String status, String message, HttpStatus statusCode) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("orderId", orderId);
		map.put("customerId", customerId);
		map.put("size", size);
		map.put("assignedLogisticsCenter", assignedLogisticsCenter);
		map.put("coordinates", coordinates);
		map.put("status", status);
		map.put("message", message);
		return new ResponseEntity<Object>(map, statusCode);
	}
}
