package com.hackathon.inditex;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.github.database.rider.junit5.DBUnitExtension;
import com.hackathon.inditex.Controllers.CenterServiceController;
import com.hackathon.inditex.DTO.CenterDTO;
import com.hackathon.inditex.DTO.CoordinatesDTO;
import com.hackathon.inditex.Entities.Center;
import com.hackathon.inditex.Entities.Coordinates;

@SpringBootTest
@ExtendWith(DBUnitExtension.class)
@DataSet("centers.yml")
class InditexCenterITests {
	@Autowired
	private CenterServiceController centerServiceController;
	
	@Test
	void shouldReadLogisticsCenter() {
		// GIVEN
		Center expectedCenter = getExistingCenter();
		
		// WHEN
		ResponseEntity<List<Center>> actualResponse = centerServiceController.readLogisticsCenters();
		
		// THEN
		assertNotNull(actualResponse, "actualResponse wasn't expected to be null");
		assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
		assertNotNull(actualResponse.getBody(), "actualResponse.getBody() wasn't expected to be null");
		List<Center> centerList = actualResponse.getBody();
		assertNotNull(centerList, "centerList wasn't expected to be null");
		Center center = centerList.get(0);
		assertNotNull(center, "center wasn't expected to be null");
		assertEquals(expectedCenter.getId(), center.getId());
		assertEquals(expectedCenter.getName(), center.getName());
		assertEquals(expectedCenter.getCapacity(), center.getCapacity());
		assertEquals(expectedCenter.getStatus(), center.getStatus());
		assertEquals(expectedCenter.getMaxCapacity(), center.getMaxCapacity());
		assertEquals(expectedCenter.getCurrentLoad(), center.getCurrentLoad());
		assertEquals(expectedCenter.getCoordinates().getLatitude(), center.getCoordinates().getLatitude());
		assertEquals(expectedCenter.getCoordinates().getLongitude(), center.getCoordinates().getLongitude());
	}

	private Center getExistingCenter() {
		return new Center(Long.valueOf(10), "CENTER B", "MS", "AVAILABLE", 5, 10, new Coordinates(48.8566, 2.3522));
	}
	
	@Test
	@ExpectedDataSet("centersexpected_afterpost.yml")
	void shouldPostCenter() {
		// GIVEN 
		CenterDTO centerDto = new CenterDTO("CENTER A", "MS", "AVAILABLE", 5, 4, new CoordinatesDTO(142.3601, -71.0589));
		
		// WHEN 
		ResponseEntity<?> actualResponse = centerServiceController.createNewLogisticsCenter(centerDto);
		
		// THEN
		assertNotNull(actualResponse, "actualResponse wasn't expected to be null");
		assertEquals(HttpStatus.CREATED, actualResponse.getStatusCode());
		assertNotNull(actualResponse.getBody(), "actualResponse.getBody() wasn't expected to be null");
		@SuppressWarnings("unchecked")
		Map<String, Object> responseMap = (Map<String, Object>) actualResponse.getBody();
		assertEquals("Logistics center created successfully.", responseMap.get("message"));
	}
	
	@Test
	@ExpectedDataSet("centers.yml")
	void shouldfailPostCenter_repeatedCoordinates() {
		// GIVEN 
		CenterDTO centerDto = new CenterDTO("CENTER A", "MS", "AVAILABLE", 5, 4, new CoordinatesDTO(48.8566, 2.3522));
		
		// WHEN 
		ResponseEntity<?> actualResponse = centerServiceController.createNewLogisticsCenter(centerDto);
		
		// THEN
		assertNotNull(actualResponse, "actualResponse wasn't expected to be null");
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actualResponse.getStatusCode());
		assertNotNull(actualResponse.getBody(), "actualResponse.getBody() wasn't expected to be null");
		@SuppressWarnings("unchecked")
		Map<String, Object> responseMap = (Map<String, Object>) actualResponse.getBody();
		assertEquals("There is already a logistics center in that position.", responseMap.get("message"));
	}
	
	@ParameterizedTest
	@CsvSource({"2,4", "0,2"})
	@ExpectedDataSet("centers.yml")
	void shouldFailPostCenter_currentLoadExceedsMaxCapacity(int maxCapacity, int currentLoad) {
		// GIVEN 
		CenterDTO centerDto = new CenterDTO("CENTER A", "MS", "AVAILABLE", maxCapacity, currentLoad, new CoordinatesDTO(142.3601, -71.0589));
		
		// WHEN 
		ResponseEntity<?> actualResponse = centerServiceController.createNewLogisticsCenter(centerDto);
		
		// THEN
		assertNotNull(actualResponse, "actualResponse wasn't expected to be null");
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actualResponse.getStatusCode());
		assertNotNull(actualResponse.getBody(), "actualResponse.getBody() wasn't expected to be null");
		@SuppressWarnings("unchecked")
		Map<String, Object> responseMap = (Map<String, Object>) actualResponse.getBody();
		assertEquals("Current load cannot exceed max capacity.", responseMap.get("message"));
	}
	
	@ParameterizedTest
	@CsvSource({"-2,4", "2,-4", "0,-1", "-1,0", "-1,-1"})
	@ExpectedDataSet("centers.yml")
	void shouldFailPostCenter_negativeCenterCapacityOrCurrentLoad(int maxCapacity, int currentLoad) {
		// GIVEN 
		CenterDTO centerDto = new CenterDTO("CENTER A", "MS", "AVAILABLE", maxCapacity, currentLoad, new CoordinatesDTO(142.3601, -71.0589));
		
		// WHEN 
		ResponseEntity<?> actualResponse = centerServiceController.createNewLogisticsCenter(centerDto);
		
		// THEN
		assertNotNull(actualResponse, "actualResponse wasn't expected to be null");
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actualResponse.getStatusCode());
		assertNotNull(actualResponse.getBody(), "actualResponse.getBody() wasn't expected to be null");
		@SuppressWarnings("unchecked")
		Map<String, Object> responseMap = (Map<String, Object>) actualResponse.getBody();
		assertEquals("Current load and max capacity must be 0 or greater.", responseMap.get("message"));
	}
	
	@ParameterizedTest
	@ValueSource(strings= {"FMS", "fms", "F", "f", "B M", " s", "s ", " s ", " ", "", "@"})
	@ExpectedDataSet("centers.yml")
	void shouldFailPostCenter_invalidCenterCapacity(String capacity) {
		// GIVEN 
		CenterDTO centerDto = new CenterDTO("CENTER A", capacity, "AVAILABLE", 5, 4, new CoordinatesDTO(142.3601, -71.0589));
		
		// WHEN 
		ResponseEntity<?> actualResponse = centerServiceController.createNewLogisticsCenter(centerDto);
		
		// THEN
		assertNotNull(actualResponse, "actualResponse wasn't expected to be null");
		assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, actualResponse.getStatusCode());
		assertNotNull(actualResponse.getBody(), "actualResponse.getBody() wasn't expected to be null");
		@SuppressWarnings("unchecked")
		Map<String, Object> responseMap = (Map<String, Object>) actualResponse.getBody();
		assertEquals("Invalid center capacity.", responseMap.get("message"));
	}
	
	@Test
	@ExpectedDataSet("centersExpected_afterDeletion.yml")
	void shouldDeleteCenter() {
		// GIVEN
		Long existingCenterId = 10L;
		
		// WHEN
		ResponseEntity<?> actualResponse = centerServiceController.deleteLogisticsCenter(existingCenterId);
		
		// THEN
		assertNotNull(actualResponse, "actualResponse wasn't expected to be null");
		assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
		assertNotNull(actualResponse.getBody(), "actualResponse.getBody() wasn't expected to be null");
		@SuppressWarnings("unchecked")
		Map<String, Object> responseMap = (Map<String, Object>) actualResponse.getBody();
		assertEquals("Logistics center deleted successfully.", responseMap.get("message"));
	}
	
	@Test
	@ExpectedDataSet("centers.yml")
	void shouldNotDeleteCenter_unexistingCenterId() {
		// GIVEN
		Long existingCenterId = 1L;
		
		// WHEN
		ResponseEntity<?> actualResponse = centerServiceController.deleteLogisticsCenter(existingCenterId);
		
		// THEN
		assertNotNull(actualResponse, "actualResponse wasn't expected to be null");
		assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
		assertNotNull(actualResponse.getBody(), "actualResponse.getBody() wasn't expected to be null");
		@SuppressWarnings("unchecked")
		Map<String, Object> responseMap = (Map<String, Object>) actualResponse.getBody();
		assertEquals("No logistics center found with the given ID.", responseMap.get("message"));
	}
	
	@ParameterizedTest
	@MethodSource
	@ExpectedDataSet("centersExpected_AfterUpdate.yml")
	void shouldUpdateCenter(Map<String, Object> updates) {
		// GIVEN
		Center existingCenter = getExistingCenter();
		
		// WHEN
		ResponseEntity<?> actualResponse = centerServiceController.updateDetailsLogisticsCenter(existingCenter.getId(), updates);
		
		// THEN
		assertNotNull(actualResponse, "actualResponse wasn't expected to be null");
		assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
		assertNotNull(actualResponse.getBody(), "actualResponse.getBody() wasn't expected to be null");
		@SuppressWarnings("unchecked")
		Map<String, Object> responseMap = (Map<String, Object>) actualResponse.getBody();
		assertEquals("Logistics center updated successfully.", responseMap.get("message"));
	}
	
	private static Stream<Arguments> shouldUpdateCenter() {
		return Stream.of(
				Arguments.of(
						Map.of("name", "UPDATED CENTER", "capacity", "B", "status", "FULL", "currentLoad", "16", "maxCapacity", "20", "latitude", "12.1234", "longitude", "12.1234"),
						Map.of("name", "UPDATED CENTER", "capacity", "B", "status", "FULL", "currentLoad", "16", "maxCapacity", "20", "coordinates", Map.of("latitude", "12.1234", "longitude", "12.1234"))
						)
				);
	}
	
	@ParameterizedTest
	@CsvSource({"name, 'UPDATED CENTER'", "latitude, 12.1234"})
	void shouldUpdateCenter(String key, String value) {
		// GIVEN
		Center existingCenter = getExistingCenter();
		Map<String, Object> updates = Map.of(key, value);
		
		// WHEN
		ResponseEntity<?> actualResponse = centerServiceController.updateDetailsLogisticsCenter(existingCenter.getId(), updates);
		
		// THEN
		assertNotNull(actualResponse, "actualResponse wasn't expected to be null");
		assertEquals(HttpStatus.OK, actualResponse.getStatusCode());
		assertNotNull(actualResponse.getBody(), "actualResponse.getBody() wasn't expected to be null");
		@SuppressWarnings("unchecked")
		Map<String, Object> responseMap = (Map<String, Object>) actualResponse.getBody();
		assertEquals("Logistics center updated successfully.", responseMap.get("message"));
	}

}
