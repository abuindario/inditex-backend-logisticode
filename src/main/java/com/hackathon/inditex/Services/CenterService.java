package com.hackathon.inditex.Services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.hackathon.inditex.DTO.CenterDTO;
import com.hackathon.inditex.Entities.Center;

public interface CenterService {

	void validateAndSaveLogisticsCenter(CenterDTO centerDto);
	
	void updateAndSaveCenter(Center center, Map<String, Object> updates);
	
	void saveCenter(Center center);

	List<Center> readLogisticsCenters();

	Optional<Center> findCenterById(int id);

	void deleteLogisticsCenterById(int id);
	
}
