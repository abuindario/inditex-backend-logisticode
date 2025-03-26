package com.hackathon.inditex.Controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
	@Autowired
	CenterService centerService;
	
	@PostMapping("/api/centers")
	public ResponseEntity<Map<String, String>> createLogisticsCenter(@RequestBody CenterDTO centerDto) {		
		try {
			centerService.validateAndCreateLogisticsCenter(centerDto);
			return new ResponseEntity<>(setResponseMessage("Logistics center created successfully."), HttpStatus.CREATED);			
		} catch(IllegalArgumentException e) {
			return new ResponseEntity<>(setResponseMessage(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/api/centers")
	public ResponseEntity<List<Center>> readLogisticsCenters() {
		return ResponseEntity.ok(centerService.readLogisticsCenters());
	}
	
	@DeleteMapping("/api/centers/{id}")
	public ResponseEntity<Map<String, String>> deleteLogisticsCenterById(@PathVariable("id") int id) {
		centerService.deleteLogisticsCenterById(id);
		return ResponseEntity.ok(setResponseMessage("Logistics center deleted successfully."));
	}
	
	@PatchMapping("/api/centers/{id}")
	public ResponseEntity<Map<String, String>> updateLogisticsCenter(@PathVariable("id") int id, @RequestBody Map<String, Object> updates) {
		Optional<Center> centerOpt = centerService.findCenterById(id);
		if(centerOpt.isEmpty()) {
			return new ResponseEntity<>(setResponseMessage("Center not found."), HttpStatus.NOT_FOUND);			
		}
		
		Center center = centerService.updateCenter(centerOpt.get(), updates);
		
		if(updates.containsKey("coordinates") || updates.containsKey("latitude") || updates.containsKey("longitude")) {
			if(centerService.duplicatedCenterInCoordinates(center.getCoordinates())) {
				return new ResponseEntity<>(setResponseMessage("There is already a logistics center in that position."), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		if(centerService.exceedsMaxCapacity(center.getCurrentLoad(), center.getMaxCapacity())) {
			return new ResponseEntity<>(setResponseMessage("Current load cannot exceed max capacity."), HttpStatus.INTERNAL_SERVER_ERROR);		
		}
		
		centerService.saveCenter(center);
		
		return ResponseEntity.ok(setResponseMessage("Logistics center updated successfully."));
	}
	
	public static Map<String, String> setResponseMessage (String message) {
		Map<String, String> response = new HashMap<>();
		response.put("message", message);
		return response;
	}
	
}
