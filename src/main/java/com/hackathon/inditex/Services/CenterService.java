package com.hackathon.inditex.Services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.hackathon.inditex.DTO.CenterDTO;
import com.hackathon.inditex.Entities.Center;

public interface CenterService {

	Center validateAndCreateLogisticsCenter(CenterDTO centerDto);

	List<Center> readLogisticsCenters();

	void deleteLogisticsCenterById(int id);

	Center updateCenter(Center center, Map<String, Object> updates);

	Optional<Center> findCenterById(int id);
	
	void saveCenter(Center center);
	
}
