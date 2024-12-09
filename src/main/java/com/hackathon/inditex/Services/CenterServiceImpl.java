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
	public void save (Center center) {
		centerRepository.save(center);
	}

	@Override
	public void deleteById (Long id) {
		centerRepository.deleteById(id);
	}
	
	@Override
	public Optional<Center> findById(Long id) {
		return centerRepository.findById(id);
	}

}
