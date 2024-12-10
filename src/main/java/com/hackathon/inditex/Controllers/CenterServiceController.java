package com.hackathon.inditex.Controllers;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hackathon.inditex.DTO.CenterDTO;
import com.hackathon.inditex.DTO.Mapper;
import com.hackathon.inditex.DTO.ResponseMessage;
import com.hackathon.inditex.Entities.Center;
import com.hackathon.inditex.Entities.Coordinates;
import com.hackathon.inditex.Services.CenterServiceImpl;

@RestController
@RequestMapping("/api/centers")
public class CenterServiceController {
	public Mapper mapper = new Mapper();

	@Autowired
	CenterServiceImpl centerServiceImpl;

//	@GetMapping("/")
//	public ResponseEntity<List<Center>> readLogisticsCenters() {
//		return new ResponseEntity<>(centerServiceImpl.findAll(), HttpStatus.OK);
//	}
//
//	@PostMapping("/")
//	public ResponseEntity<ResponseMessage> createNewLogisticsCenter(@RequestBody CenterDTO centerDTO) {
//		Center center = mapper.toCenter(centerDTO);
//		if (readLogisticsCenters().getBody().stream()
//				.anyMatch(e -> e.getCoordinates().getLatitude().equals(center.getCoordinates().getLatitude())
//						&& e.getCoordinates().getLongitude().equals(center.getCoordinates().getLongitude()))) {
//			return new ResponseEntity<>(new ResponseMessage("There is already a logistics center in that position."),
//					HttpStatus.INTERNAL_SERVER_ERROR);
//		} else if (center.getCurrentLoad() > center.getMaxCapacity()) {
//			return new ResponseEntity<>(new ResponseMessage("Current load cannot exceed max capacity."),HttpStatus.INTERNAL_SERVER_ERROR);
//		} else if (!center.getCapacity().toString().toUpperCase().chars().distinct()
//				.anyMatch(c -> c == 'B' || c == 'M' || c == 'S')) {
//			return new ResponseEntity<>(new ResponseMessage("Invalid center capacity."), HttpStatus.INTERNAL_SERVER_ERROR);
//		} else {
//			centerServiceImpl.save(center);
//			return new ResponseEntity<>(new ResponseMessage("Logistics center created successfully."), HttpStatus.CREATED);
//		}
//	}

//	@DeleteMapping("/{id}")
//	public ResponseEntity<ResponseMessage> deleteLogisticsCenter(@PathVariable Long id) {
//		/*if (centerServiceImpl.findById(id).isPresent()) {
//			centerServiceImpl.deleteById(id);*/
//			return new ResponseEntity<>(new ResponseMessage("Logistics center deleted successfully."), HttpStatus.OK);
//		/*} else {
//			return new ResponseEntity<>(new ResponseMessage("Center not found."), HttpStatus.NOT_FOUND);
//		}*/
//	}

	@PatchMapping("/{id}")
	public ResponseEntity<ResponseMessage> updateDetailsLogisticsCenter(@PathVariable Long id,
			@RequestBody Map<String, Object> updates) {
		if (centerServiceImpl.findById(id).isPresent()) {
			Center current = centerServiceImpl.findById(id).get();
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
							if(coor.getKey().toString().equals("longitude")) {
								currentCoordinates.setLongitude(coor.getValue());
							} else if(coor.getKey().toString().equals("latitude") ) {
								currentCoordinates.setLatitude(coor.getValue());
							}
						} );
						break;
					}
				}
			});
			current.setCoordinates(currentCoordinates);
			if (current.getCurrentLoad() > current.getMaxCapacity()) {
				return new ResponseEntity<>(new ResponseMessage("Current load cannot exceed max capacity."), HttpStatus.INTERNAL_SERVER_ERROR);
			} else if (!current.getCapacity().toString().toUpperCase().chars().distinct()
					.anyMatch(c -> c == 'B' || c == 'M' || c == 'S')) {
				return new ResponseEntity<>(new ResponseMessage("Invalid center capacity."), HttpStatus.INTERNAL_SERVER_ERROR);
			} else {
				centerServiceImpl.save(current);
				return new ResponseEntity<>(new ResponseMessage("Logistics center updated successfully."), HttpStatus.OK);
			}
		} else {
			return new ResponseEntity<>(new ResponseMessage("Center not found."), HttpStatus.NOT_FOUND);
		}
	}
}
