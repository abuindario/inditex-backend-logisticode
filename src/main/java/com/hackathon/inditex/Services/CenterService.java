package com.hackathon.inditex.Services;

import java.util.List;
import java.util.Optional;

import com.hackathon.inditex.Entities.Center;

public interface CenterService {

	public List<Center> findAll();

	public String create(Center center);
	
	public String deleteById(Long id);
	
	public Optional<Center> findById(Long id);
	
	public void updateCenter(Center center);
}
