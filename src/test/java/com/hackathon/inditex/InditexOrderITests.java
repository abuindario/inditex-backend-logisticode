package com.hackathon.inditex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.junit5.DBUnitExtension;
import com.hackathon.inditex.Controllers.OrderServiceController;
import com.hackathon.inditex.DTO.CoordinatesDTO;
import com.hackathon.inditex.DTO.OrderDTO;
import com.hackathon.inditex.Entities.Coordinates;
import com.hackathon.inditex.Entities.Order;

@SpringBootTest
@ExtendWith(DBUnitExtension.class)
@DataSet("orders.yml")
class InditexOrderITests {
	@Autowired
	OrderServiceController orderServiceController;
	
	@Test 
	void shouldReadOrders() {
		// GIVEN
		Order expectedOrder = createExpectedOrder();
		
		// WHEN 
		ResponseEntity<List<Order>> actualResponse = orderServiceController.readOrders();
		
		// THEN
		assertNotNull(actualResponse, "actualResponse wasn't expected to be null");
		assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
		assertNotNull(actualResponse.getBody(), "actualResponse.getBody() wasn't expected to be null");
		List<Order> orderList = actualResponse.getBody();
		assertNotNull(orderList, "orderList wasn't expected to be null");
		Order actualOrder = orderList.get(0);
		assertNotNull(actualOrder, "actualOrder wasn't expected to be null");
		assertEquals(expectedOrder.getId(), actualOrder.getId());
		assertEquals(expectedOrder.getCustomerId(), actualOrder.getCustomerId());
		assertEquals(expectedOrder.getSize(), actualOrder.getSize());
		assertEquals(expectedOrder.getAssignedCenter(), actualOrder.getAssignedCenter());
		assertEquals(expectedOrder.getStatus(), actualOrder.getStatus());
		assertEquals(expectedOrder.getCoordinates().getLatitude(), actualOrder.getCoordinates().getLatitude());
		assertEquals(expectedOrder.getCoordinates().getLongitude(), actualOrder.getCoordinates().getLongitude());
	}

	private Order createExpectedOrder() {
		Order expectedOrder = new Order();
		expectedOrder.setId(Long.valueOf(10));
		expectedOrder.setCustomerId(Long.valueOf(1));
		expectedOrder.setSize("B");
		expectedOrder.setAssignedCenter("Center Name");
		expectedOrder.setCoordinates(new Coordinates(40.7128, -74.0060));
		expectedOrder.setStatus("PENDING");
		return expectedOrder;
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@ExpectedDataSet("ordersExpected_afterPost.yml")
	void shouldPostOrder() {
		// GIVEN
		OrderDTO orderDto = populateOrderDto();
		
		// WHEN
		ResponseEntity<?> actualResponse = orderServiceController.createNewOrder(orderDto);
		
		// THEN
		assertNotNull(actualResponse, "actualResponse wasn't expected to be null");
		assertEquals(HttpStatus.CREATED, actualResponse.getStatusCode());
		Map<String, Object> actualResponseMap = (Map<String, Object>) actualResponse.getBody();
		assertNotNull(actualResponseMap, "actualResponse.getBody() wasn't expected to be null");
		assertEquals(Long.valueOf(1), actualResponseMap.get("orderId"));
		assertEquals(orderDto.getCustomerId(), actualResponseMap.get("customerId"));
		assertEquals(orderDto.getSize().toUpperCase(), actualResponseMap.get("size"));
		assertNull(actualResponseMap.get("assignedLogisticsCenter"), "assignedLogisticsCenter was expected to be null");
		assertEquals(new Coordinates(orderDto.getCoordinates().getLatitude(), orderDto.getCoordinates().getLongitude()), actualResponseMap.get("coordinates"));
		assertEquals("PENDING", actualResponseMap.get("status"));
		assertEquals("Order created successfully in PENDING status.", actualResponseMap.get("message"));
	}

	private OrderDTO populateOrderDto() {
		OrderDTO orderDto = new OrderDTO();
		orderDto.setSize("b");
		orderDto.setAssignedCenter(null);
		orderDto.setCustomerId(Long.valueOf(123));
		CoordinatesDTO coordinatesDto = new CoordinatesDTO();
		coordinatesDto.setLatitude(40.7128);
		coordinatesDto.setLongitude(-74.0060);
		orderDto.setCoordinates(coordinatesDto);
		return orderDto;
	}
	
	@SuppressWarnings("unchecked")
	@ParameterizedTest
	@ValueSource(strings= {"B ", " B", " B ", "BAS", ""," "})
	void shouldFailPostOrder_invalidSize(String size) {
		// GIVEN
		OrderDTO orderDto = populateOrderDto();
		orderDto.setSize(size);
		
		// WHEN
		ResponseEntity<?> actualResponse = orderServiceController.createNewOrder(orderDto);
		
		// THEN
		assertNotNull(actualResponse);
		assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
		assertNotNull(actualResponse.getBody());
		Map<String, Object> actualResponseMap = (Map<String, Object>) actualResponse.getBody();
		assertEquals("Invalid order size, it must be a combination of B, M, S.", actualResponseMap.get("message"));
	}
	
	@SuppressWarnings("unchecked")
	@Test
	void shouldFailPostOrder_nullParameters() {
		// GIVEN
		OrderDTO orderDto = populateOrderDto();
		orderDto.setCustomerId(null);
		
		// WHEN
		ResponseEntity<?> actualResponse = orderServiceController.createNewOrder(orderDto);
		
		// THEN
		assertNotNull(actualResponse);
		assertEquals(HttpStatus.BAD_REQUEST, actualResponse.getStatusCode());
		assertNotNull(actualResponse.getBody());
		Map<String, Object> actualResponseMap = (Map<String, Object>) actualResponse.getBody();
		assertEquals("Order parameters must not be null", actualResponseMap.get("message"));
	}
}