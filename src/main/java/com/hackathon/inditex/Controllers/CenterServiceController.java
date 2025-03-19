package com.hackathon.inditex.Controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.hackathon.inditex.DTO.CenterDTO;
import com.hackathon.inditex.Entities.Center;
import com.hackathon.inditex.Services.CenterService;

@RestController
public class CenterServiceController {
	private Map<String, String> response = new HashMap<>();

	@Autowired
	CenterService centerService;
	
	@PostMapping("/api/centers")
	public ResponseEntity<Map<String, String>> createLogisticsCenter(@RequestBody CenterDTO centerDto) {		
		// Check for another center in that position
		if(centerService.existsCenterInCoordinates(centerDto.coordinates())) {
			response.put("message", "There is already a logistics center in that position.");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		// Verify currentLoad doesn't exceed maxCapacity
		if(centerService.exceedsMaxCapacity(centerDto)) {
			response.put("message", "Current load cannot exceed max capacity.");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		// Create logistics center
		centerService.createLogisticsCenter(centerDto);
		
		response.put("message", "Logistics center created successfully.");
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	@GetMapping("/api/centers")
	public ResponseEntity<List<Center>> readLogisticsCenters() {
		return ResponseEntity.ok(centerService.readLogisticsCenters());
	}
	
	@DeleteMapping("/api/centers/{id}")
	public ResponseEntity<Map<String, String>> deleteLogisticsCenterById(@PathVariable("id") int id) {
		centerService.deleteLogisticsCenterById(id);
		response.put("message", "Logistics center deleted successfully.");
		return ResponseEntity.ok(response);
	}
	
	@PatchMapping("/api/centers/{id}")
	public ResponseEntity<Map<String, String>> updateLogisticsCenter(@PathVariable("id") int id, @RequestBody Map<String, Object> updates) {
		// Find center, if exists
		if(centerService.findCenterById(id).isEmpty()) {
			response.put("message", "Center not found.");
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);			
		}
		
		Center center = centerService.findCenterById(id).get();
		center = centerService.updateCenter(center, updates);
		
		// Check for another center in that position
		if(updates.containsKey("coordinates") || updates.containsKey("latitude") || updates.containsKey("longitude")) {
			if(centerService.duplicatedCenterInCoordinates(center.getCoordinates())) {
				response.put("message", "There is already a logistics center in that position.");
				return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		// Verify currentLoad doesn't exceed maxCapacity
		if(centerService.exceedsMaxCapacity(center)) {
			response.put("message", "Current load cannot exceed max capacity.");
			return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);		
		}
		
		// Save center to database
		centerService.saveCenter(center);
		
		response.put("message", "Logistics center updated successfully.");
		return ResponseEntity.ok(response);
	}
}
