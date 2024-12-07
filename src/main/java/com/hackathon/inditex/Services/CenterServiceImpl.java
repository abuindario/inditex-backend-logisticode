package com.hackathon.inditex.Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hackathon.inditex.Entities.Center;
import com.hackathon.inditex.Repositories.CenterRepository;

@Service
public class CenterServiceImpl implements CenterService {
	@Autowired
	CenterRepository centerRepository;
	
	@Override
	public List<Center> findAll() {
		return (List<Center>) centerRepository.findAll();
	}

	@Override
	public String createNewCenter(Center center) {
		centerRepository.save(center);
		return "Logistics center created successfully.";
	}

}
