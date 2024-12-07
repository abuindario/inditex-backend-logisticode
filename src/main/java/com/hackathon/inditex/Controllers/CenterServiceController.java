package com.hackathon.inditex.Controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
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
		String message="undefined";
		
		Center center = mapper.toCenter(centerDTO);
		
		if(center.getCurrentLoad() > center.getMaxCapacity()) {
			message = "Current load cannot exceed max capacity.";
		} else {
			message = centerServiceImpl.createNewCenter(center);
		}
		
		return message;
	}
}
