package com.hackathon.inditex.Controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

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
		try {
			Center center = centerService.findCenterById(id).orElseThrow(() -> new NoSuchElementException("Center not found."));
			center = centerService.updateCenter(center , updates);
			centerService.saveCenter(center);
			return ResponseEntity.ok(setResponseMessage("Logistics center updated successfully."));
		} catch(NoSuchElementException e) {
			return new ResponseEntity<>(setResponseMessage(e.getMessage()), HttpStatus.NOT_FOUND);
		} catch(IllegalArgumentException e) {
			return new ResponseEntity<>(setResponseMessage(e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	public static Map<String, String> setResponseMessage (String message) {
		Map<String, String> response = new HashMap<>();
		response.put("message", message);
		return response;
	}
	
}
