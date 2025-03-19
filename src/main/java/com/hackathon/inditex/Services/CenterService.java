package com.hackathon.inditex.Services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.hackathon.inditex.DTO.CenterDTO;
import com.hackathon.inditex.Entities.Center;
import com.hackathon.inditex.Entities.Coordinates;

public interface CenterService {

	Center createLogisticsCenter(CenterDTO centerDto);

	boolean existsCenterInCoordinates(Coordinates coordiantes);

	boolean exceedsMaxCapacity(CenterDTO centerDto);

	List<Center> readLogisticsCenters();

	void deleteLogisticsCenterById(int id);

	Center updateCenter(Center center, Map<String, Object> updates);

	Optional<Center> findCenterById(int id);

	boolean exceedsMaxCapacity(Center center);
	
	void saveCenter(Center center);

	boolean duplicatedCenterInCoordinates(Coordinates coordinates);
	
}
