package com.hackathon.inditex.Services;

import java.util.List;
import java.util.Optional;

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
	public String create (Center center) {
		centerRepository.save(center);
		return "Logistics center created successfully.";
	}

	@Override
	public String deleteById (Long id) {
		centerRepository.deleteById(id);
		return "Logistics center deleted successfully.";
	}
	
	@Override
	public Optional<Center> findById(Long id) {
		return centerRepository.findById(id);
	}

	@Override
	public void updateCenter(Center center) {
		centerRepository.save(center);
	}

}
