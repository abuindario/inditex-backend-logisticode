package com.hackathon.inditex.Services;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.hackathon.inditex.DTO.CenterDTO;
import com.hackathon.inditex.Entities.Center;
import com.hackathon.inditex.Entities.Coordinates;
import com.hackathon.inditex.Repositories.CenterRepository;

@Service
public class CenterServiceImpl implements CenterService {
	private CenterRepository centerRepository;
	
	public CenterServiceImpl(CenterRepository centerRepository) {
		this.centerRepository = centerRepository;
	}

	@Override
	public boolean exceedsMaxCapacity(CenterDTO centerDto) {
		return centerDto.currentLoad() > centerDto.maxCapacity();
	}
	
	@Override
	public boolean exceedsMaxCapacity(Center center) {
		return center.getCurrentLoad() > center.getMaxCapacity();
	}

	@Override
	public List<Center> readLogisticsCenters() {
		return (List<Center>) centerRepository.findAll();
	}

	@Override
	public void deleteLogisticsCenterById(int id) {
		centerRepository.deleteById(Long.valueOf(id));
	}
	
	@Override
	public void saveCenter(Center center) {
		centerRepository.save(center);
	}
	
	@Override
	public Optional<Center> findCenterById(int id) {
		return centerRepository.findById(Long.valueOf(id));
	}
	
	@Override
	public boolean existsCenterInCoordinates(Coordinates coordinates) {
		return readLogisticsCenters().stream()
			.anyMatch(c -> matchesCoordinates(c.getCoordinates(), coordinates));
	}
	
	@Override
	public boolean duplicatedCenterInCoordinates(Coordinates coordinates) {
		return readLogisticsCenters().stream()
				.filter(c -> matchesCoordinates(c.getCoordinates(), coordinates))
				.count() > 1;
	}
	
	private boolean matchesCoordinates(Coordinates a, Coordinates b) {
		return a.getLatitude().equals(b.getLatitude()) && 
		a.getLongitude().equals(b.getLongitude());
	}
	
	@Override
	public Center createLogisticsCenter(CenterDTO centerDto) {
		return centerRepository.save(mapCenterDtoToCenter(centerDto));
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

	@SuppressWarnings("unchecked")
	@Override
	public Center updateCenter(Center center, Map<String, Object> updates) {
		updates.entrySet().forEach(entry -> {
			if (entry.getValue() != null) {
				Object updatedValue = entry.getValue();
				switch (entry.getKey()) {
				case "name":
					center.setName(updatedValue.toString());
					break;
				case "capacity":
					center.setCapacity(updatedValue.toString());
					break;
				case "status":
					center.setStatus(updatedValue.toString());
					break;
				case "maxCapacity":
					center.setMaxCapacity(Integer.valueOf(updatedValue.toString()));
					break;
				case "currentLoad":
					center.setCurrentLoad(Integer.valueOf(updatedValue.toString()));
					break;
				case "longitude":
					center.getCoordinates().setLongitude(Double.valueOf(updatedValue.toString()));
					break;
				case "latitude":
					center.getCoordinates().setLatitude(Double.valueOf(updatedValue.toString()));
					break;
				case "coordinates":
					Map<String, Double> updatedCoordinatesMap = (Map<String, Double>) updatedValue;
					updatedCoordinatesMap.entrySet().forEach(coor -> {
						if (coor.getKey().toString().equals("longitude")) {
							center.getCoordinates().setLongitude(coor.getValue());
						} else if (coor.getKey().toString().equals("latitude")) {
							center.getCoordinates().setLatitude(coor.getValue());
						}
					});
					break;
				}
			}
		});
		return center;
	}
}
