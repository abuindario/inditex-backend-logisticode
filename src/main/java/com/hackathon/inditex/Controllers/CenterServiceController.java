package com.hackathon.inditex.Controllers;

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
	public ResponseEntity<ApiResponse> createLogisticsCenter(@RequestBody CenterDTO centerDto) {		
		try {
			centerService.validateAndSaveLogisticsCenter(centerDto);
			return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse("Logistics center created successfully."));
		} catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage()));
		}
	}
	
	@GetMapping("/api/centers")
	public ResponseEntity<List<Center>> readLogisticsCenters() {
		return ResponseEntity.ok(centerService.readLogisticsCenters());
	}
	
	@DeleteMapping("/api/centers/{id}")
	public ResponseEntity<ApiResponse> deleteLogisticsCenterById(@PathVariable("id") int id) {
		centerService.deleteLogisticsCenterById(id);
		return ResponseEntity.ok(new ApiResponse("Logistics center deleted successfully."));
	}
	
	@PatchMapping("/api/centers/{id}")
	public ResponseEntity<ApiResponse> updateLogisticsCenter(@PathVariable("id") int id, @RequestBody Map<String, Object> updates) {
		try {
			Center center = centerService.findCenterById(id).orElseThrow(() -> new NoSuchElementException("Center not found."));
			centerService.updateAndSaveCenter(center, updates);
			return ResponseEntity.ok(new ApiResponse("Logistics center updated successfully."));
		} catch(NoSuchElementException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse(e.getMessage()));
		} catch(IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage()));
		}
	}
}

record ApiResponse(String message) {}
