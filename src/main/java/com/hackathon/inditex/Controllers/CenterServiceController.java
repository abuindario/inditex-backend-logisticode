package com.hackathon.inditex.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.hackathon.inditex.Services.CenterServiceImpl;

@RestController
@RequestMapping("/api/centers")
public class CenterServiceController {
	public Mapper mapper = new Mapper();	

	@Autowired CenterServiceImpl centerServiceImpl;
	
	@GetMapping("/")
    public List<Center> readLogisticsCenters() {
		return centerServiceImpl.findAll();
    }
	
	@PostMapping("/")
	public String createNewLogisticsCenter(@RequestBody CenterDTO centerDTO) {
		String message="API action undefined";
		
		Center center = mapper.toCenter(centerDTO);
		
		if(readLogisticsCenters().stream().anyMatch( e -> e.getCoordinates().getLatitude().equals(centerDTO.getCoordinates().getLatitude())
													 && e.getCoordinates().getLongitude().equals(centerDTO.getCoordinates().getLongitude())
													 )) {
			message = "There is already a logistics center in that position.";
		} else if(center.getCurrentLoad() > center.getMaxCapacity()) {
			message = "Current load cannot exceed max capacity.";
		} else {
			message = centerServiceImpl.create(center);
		}
		
		return message;
	}
	
	@DeleteMapping("/{id}")
	public String deleteLogisticsCenter(@PathVariable Long id) {
		return centerServiceImpl.deleteById(id);
	}
	
	@PatchMapping("/{id}")
	public String updateDetailsLogisticsCenter(@PathVariable Long id, @RequestBody CenterDTO centerDTO) {
		String message = "undefined";
		if(centerServiceImpl.findById(id).isPresent()) {
			centerServiceImpl.updateCenter(mapper.toCenter(centerDTO));
			message = "Logistics center updated successfully.";
		} else {
			message = "Center not found.";
		}
		return message;
	}
}
