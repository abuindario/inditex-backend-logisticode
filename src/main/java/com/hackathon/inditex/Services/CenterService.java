package com.hackathon.inditex.Services;

import java.util.List;

import com.hackathon.inditex.Entities.Center;

public interface CenterService {

	public List<Center> findAll();

	public String createNewCenter(Center center);
}
