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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackathon.inditex.DTO.CenterDTO;
import com.hackathon.inditex.DTO.Mapper;
import com.hackathon.inditex.Entities.Center;
import com.hackathon.inditex.Entities.Coordinates;
import com.hackathon.inditex.Handlers.ResponseHandler;
import com.hackathon.inditex.Services.CenterServiceImpl;

@RestController
@RequestMapping("/api/centers")
public class CenterServiceController {
	public Mapper mapper = new Mapper();

	@Autowired
	CenterServiceImpl centerServiceImpl;

	// 57 points
	@GetMapping("")
	public ResponseEntity<List<Center>> readLogisticsCenters() {
		return new ResponseEntity<List<Center>>(centerServiceImpl.findAll(), HttpStatus.OK);
	}

	// 57 points
	@PostMapping("")
	public ResponseEntity<Object> createNewLogisticsCenter(@RequestBody CenterDTO centerDTO) {
		Center center = mapper.toCenter(centerDTO);
		if (readLogisticsCenters().getBody().stream()
				.anyMatch(e -> e.getCoordinates().getLatitude().equals(center.getCoordinates().getLatitude())
						&& e.getCoordinates().getLongitude().equals(center.getCoordinates().getLongitude()))) {
			return ResponseHandler.generateResponse("There is already a logistics center in that position.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (center.getCurrentLoad() > center.getMaxCapacity()) {
			return ResponseHandler.generateResponse("Current load cannot exceed max capacity.",
					HttpStatus.INTERNAL_SERVER_ERROR);
		} else if (!center.getCapacity().toUpperCase().chars().allMatch(c -> c == 'B' || c == 'M' || c == 'S')) {
			return ResponseHandler.generateResponse("Invalid center capacity.", HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			centerServiceImpl.save(center);
			return ResponseHandler.generateResponse("Logistics center created successfully.", HttpStatus.CREATED);
		}
	}

	// 0 points
	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteLogisticsCenter(@PathVariable("id") Long id) {
		if(centerServiceImpl.existsById(id)) {
			centerServiceImpl.deleteById(id);
			return ResponseHandler.generateResponse("Logistics center deleted successfully.", HttpStatus.OK);
		}
		return ResponseHandler.generateResponse("No logistics center found with the given ID.", HttpStatus.OK);
	}

	// 57 points
	@PatchMapping("/{id}")
	public ResponseEntity<Object> updateDetailsLogisticsCenter(@PathVariable("id") Long id,
			@RequestBody Map<String, Object> updates) {
		Optional<Center> optionalCenter = centerServiceImpl.findById(id);
		if (optionalCenter.isPresent()) {
			Center current = optionalCenter.get();
			Coordinates currentCoordinates = current.getCoordinates();
			updates.entrySet().forEach(entry -> {
				if (entry.getValue() != null) {
					Object newValue = entry.getValue();
					switch (entry.getKey()) {
					case "name":
						current.setName(newValue.toString());
						break;
					case "capacity":
						current.setCapacity(newValue.toString());
						break;
					case "status":
						current.setStatus(newValue.toString());
						break;
					case "maxCapacity":
						current.setMaxCapacity(Integer.valueOf(newValue.toString()));
						break;
					case "currentLoad":
						current.setCurrentLoad(Integer.valueOf(newValue.toString()));
						break;
					case "longitude":
						currentCoordinates.setLongitude(Double.valueOf(newValue.toString()));
						break;
					case "latitude":
						currentCoordinates.setLatitude(Double.valueOf(newValue.toString()));
						break;
					case "coordinates":
						Map<String, Double> upd = (Map<String, Double>) newValue;
						upd.entrySet().forEach(coor -> {
							if (coor.getKey().toString().equals("longitude")) {
								currentCoordinates.setLongitude(coor.getValue());
							} else if (coor.getKey().toString().equals("latitude")) {
								currentCoordinates.setLatitude(coor.getValue());
							}
						});
						break;
					}
				}
			});
			current.setCoordinates(currentCoordinates);
			if (current.getCurrentLoad() > current.getMaxCapacity()) {
				return ResponseHandler.generateResponse("Current load cannot exceed max capacity.",
						HttpStatus.INTERNAL_SERVER_ERROR);
			} else if (!current.getCapacity().toUpperCase().chars().allMatch(c -> c == 'B' || c == 'M' || c == 'S')) {
				return ResponseHandler.generateResponse("Invalid center capacity.", HttpStatus.INTERNAL_SERVER_ERROR);
			} 
			else {
				centerServiceImpl.save(current);
				return ResponseHandler.generateResponse("Logistics center updated successfully.", HttpStatus.OK);
			}
		} else {
			return ResponseHandler.generateResponse("Center not found.", HttpStatus.NOT_FOUND);
		}
	}
}
