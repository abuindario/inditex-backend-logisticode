package com.hackathon.inditex.Services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hackathon.inditex.DTO.CenterDTO;
import com.hackathon.inditex.Entities.Center;
import com.hackathon.inditex.Entities.Coordinates;
import com.hackathon.inditex.Repositories.CenterRepository;

@Service
public class CenterServiceImpl implements CenterService {
	private final CenterRepository centerRepository;
	
	public CenterServiceImpl(CenterRepository centerRepository) {
		this.centerRepository = centerRepository;
	}

	@Override
	@Transactional(readOnly = true)
	public List<Center> readLogisticsCenters() {
		return List.copyOf((List<Center>) centerRepository.findAll());
	}

	@Override
	@Transactional
	public void deleteLogisticsCenterById(int id) {
		centerRepository.deleteById(Long.valueOf(id));
	}
	
	@Override
	@Transactional(readOnly = true)
	public Optional<Center> findCenterById(int id) {
		return centerRepository.findById(Long.valueOf(id));
	}
	
	@Override
	public void validateAndSaveLogisticsCenter(CenterDTO centerDto) {
		validateAndSaveCenter(mapCenterDtoToCenter(centerDto));
	}
	
	@Override
	public void updateAndSaveCenter(Center center, Map<String, Object> updates) {
	    updates.forEach((key, value) -> updateCenterField(center, key, value));
	    validateAndSaveCenter(center);
	}
	
	@Override
	@Transactional
	public void saveCenter(Center center) {
		centerRepository.save(center);
	}
	
	private void validateAndSaveCenter(Center center) {
		validateCenter(center);
		saveCenter(center);
	}
	
	private void validateCenter(Center center) {
		if(duplicatedCenterInCoordinates(center))
	    	throw new IllegalArgumentException("There is already a logistics center in that position.");
	    if(center.getCurrentLoad() > center.getMaxCapacity())
	    	throw new IllegalArgumentException("Current load cannot exceed max capacity.");
	}
	
	public boolean duplicatedCenterInCoordinates(Center center) {
		return readLogisticsCenters().stream()
				.filter(c -> c.getId() != center.getId())
				.anyMatch(c -> matchesCoordinates(c.getCoordinates(), center.getCoordinates()));
	}
	
	private boolean matchesCoordinates(Coordinates a, Coordinates b) {
		return a.getLatitude().equals(b.getLatitude()) && 
		a.getLongitude().equals(b.getLongitude());
	}

	private Center mapCenterDtoToCenter(CenterDTO centerDto) {
		Center center = new Center();
		center.setName(centerDto.name());
		center.setCapacity(centerDto.capacity());
		center.setStatus(centerDto.status());
		center.setCurrentLoad(centerDto.currentLoad());
		center.setMaxCapacity(centerDto.maxCapacity());
		center.setCoordinates(centerDto.coordinates());
		return center;
	}
	
	private void updateCenterField(Center center, String key, Object value) {
	    if (value != null) {
	        switch (key) {
	            case "name": center.setName(value.toString()); break;
	            case "capacity": center.setCapacity(value.toString()); break;
	            case "status": center.setStatus(value.toString()); break;
	            case "maxCapacity": center.setMaxCapacity(Integer.parseInt(value.toString())); break;
	            case "currentLoad": center.setCurrentLoad(Integer.parseInt(value.toString())); break;
	            case "longitude": center.getCoordinates().setLongitude(Double.parseDouble(value.toString())); break;
	            case "latitude": center.getCoordinates().setLatitude(Double.parseDouble(value.toString())); break;
	        }
	    }
	}
}
