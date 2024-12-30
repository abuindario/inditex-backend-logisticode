package com.hackathon.inditex.Controllers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackathon.inditex.DTO.Mapper;
import com.hackathon.inditex.Entities.Center;
import com.hackathon.inditex.Entities.Order;
import com.hackathon.inditex.Services.CenterServiceImpl;
import com.hackathon.inditex.Services.OrderServiceImpl;
import com.hackathon.inditex.handler.ResponseHandler;

@RestController
@RequestMapping("/api/orders/order-assignations")
public class CenterOrderAssignment {
	Mapper mapper = new Mapper();
	
	@Autowired
	CenterServiceImpl centerServiceImpl;
	
	@Autowired
	OrderServiceImpl orderServiceImpl;
	
	@PostMapping("")
	public ResponseEntity<Object> assignCenterToOrder() {
		List<Order> pendingOrderList = orderServiceImpl
										.findAll()
										.stream()
										.filter(order -> order.getStatus().equals("PENDING"))
										.collect(ArrayList::new, List::add, List::addAll);
		
		if(pendingOrderList.size() > 1) {
			pendingOrderList.sort((o1,o2) -> o1.getId().compareTo(o2.getId()));
		}
		
		List<Map<String,Object>> cumulativeResponse = new ArrayList<>();
		List<Center> allCenters = centerServiceImpl.findAll();

		pendingorders: for(int i=0; i<pendingOrderList.size(); i++) {

			Order currentOrder = pendingOrderList.get(i);

			List<Center> suitableCenters = allCenters.stream().filter(center -> center.getCapacity().equals(currentOrder.getSize())).collect(ArrayList::new, List::add, List::addAll);
			
			if(suitableCenters.size() == 0) {
				cumulativeResponse.add(ResponseHandler.generateResponse(null, currentOrder.getId(), currentOrder.getAssignedCenter(), 
						"No available centers support the order type.", currentOrder.getStatus()));
				continue pendingorders;
			}
		
			List<Center> availableCenters = suitableCenters.stream().filter(center -> center.getCurrentLoad() < center.getMaxCapacity()).collect(ArrayList::new, List::add, List::addAll);

			
			if(availableCenters.size() == 0) {
				cumulativeResponse.add(ResponseHandler.generateResponse(null, currentOrder.getId(), currentOrder.getAssignedCenter(), 
						"All centers are at maximum capacity.", currentOrder.getStatus()));
				continue pendingorders;
			}

			availableCenters.sort(
					new Comparator<Center>() {
						public int compare(Center c1, Center c2) {
							return (calculateDistance(c1.getCoordinates().getLatitude(),
									c1.getCoordinates().getLongitude(), 
									currentOrder.getCoordinates().getLatitude(),
									currentOrder.getCoordinates().getLongitude())
							- 
							calculateDistance(c2.getCoordinates().getLatitude(),
									c2.getCoordinates().getLongitude(), 
									currentOrder.getCoordinates().getLatitude(),
									currentOrder.getCoordinates().getLongitude()))
							<=0 ? 0:1;
						}
					});
			Center assignedCenter = availableCenters.get(0);
			Double distance = calculateDistance(assignedCenter.getCoordinates().getLatitude(), assignedCenter.getCoordinates().getLongitude(),
					currentOrder.getCoordinates().getLatitude(), currentOrder.getCoordinates().getLongitude());
			currentOrder.setAssignedCenter(assignedCenter.getName());
			currentOrder.setStatus("ASSIGNED");
			int initialLoad = assignedCenter.getCurrentLoad();
			assignedCenter.setCurrentLoad(++initialLoad);
			centerServiceImpl.save(assignedCenter);
			orderServiceImpl.save(currentOrder);	
			cumulativeResponse.add(ResponseHandler.generateResponse(distance, currentOrder.getId(), 
					currentOrder.getAssignedCenter(), currentOrder.getStatus()));
		}
		return ResponseHandler.generateResponse(cumulativeResponse, HttpStatus.OK);
	}
	
	private double calculateDistance(double centerLat, double centerLong, double orderLat, double orderLong) {

	    double dLat = Math.toRadians((orderLat - centerLat));
	    double dLong = Math.toRadians((orderLong - centerLong));

	    centerLat = Math.toRadians(centerLat);
	    orderLat = Math.toRadians(orderLat);

	    double a = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(centerLat) * Math.cos(orderLat) * Math.pow(Math.sin(dLong / 2), 2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

	    return 6371 * c;
	}
}
