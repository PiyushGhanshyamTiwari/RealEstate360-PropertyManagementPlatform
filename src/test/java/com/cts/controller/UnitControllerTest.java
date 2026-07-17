package com.cts.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.cts.dto.UnitInputDTO;
import com.cts.dto.UnitOutputDTO;
import com.cts.service.UnitService;

@ExtendWith(MockitoExtension.class)
class UnitControllerTest {

    @Mock
    private UnitService unitService;

    @InjectMocks
    private UnitController unitController;

    @Test
    void testAddUnit() {

        UnitInputDTO inputDTO = new UnitInputDTO();
        UnitOutputDTO outputDTO = new UnitOutputDTO();

        when(unitService.addUnit(inputDTO)).thenReturn(outputDTO);

        ResponseEntity<?> response = unitController.addUnit(inputDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(outputDTO, response.getBody());

        verify(unitService).addUnit(inputDTO);
    }

    @Test
    void testGetAllUnit() {

        List<UnitOutputDTO> units = Arrays.asList(
                new UnitOutputDTO(),
                new UnitOutputDTO());

        when(unitService.findAllUnit()).thenReturn(units);

        ResponseEntity<?> response = unitController.getAllUnit();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(units, response.getBody());

        verify(unitService).findAllUnit();
    }

    @Test
    void testFilterUnits() {

        String type = "1BHK";
        Double minRent = 5000.0;
        Double maxRent = 15000.0;
        Integer propertyId = 1;
        String propertyName = "Green Villa";
        String city = "Chennai";
        String status = "AVAILABLE";

        List<UnitOutputDTO> expected = List.of(new UnitOutputDTO());

        when(unitService.filterUnits(
                type,
                minRent,
                maxRent,
                propertyId,
                propertyName,
                city,
                status))
                .thenReturn(expected);

        ResponseEntity<?> response =
                unitController.filterUnits(
                        type,
                        minRent,
                        maxRent,
                        propertyId,
                        propertyName,
                        city,
                        status);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());

        verify(unitService).filterUnits(
                type,
                minRent,
                maxRent,
                propertyId,
                propertyName,
                city,
                status);
    }

    @Test
    void testFilterUnitsWithNullParameters() {

        List<UnitOutputDTO> expected = List.of();

        when(unitService.filterUnits(
                null,
                null,
                null,
                null,
                null,
                null,
                null))
                .thenReturn(expected);

        ResponseEntity<?> response =
                unitController.filterUnits(
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expected, response.getBody());

        verify(unitService).filterUnits(
                null,
                null,
                null,
                null,
                null,
                null,
                null);
    }

    @Test
    void testUpdateUnit() {

        int unitId = 1;

        UnitInputDTO inputDTO = new UnitInputDTO();
        UnitOutputDTO outputDTO = new UnitOutputDTO();

        when(unitService.updateUnit(unitId, inputDTO))
                .thenReturn(outputDTO);

        ResponseEntity<?> response =
                unitController.updateUnit(unitId, inputDTO);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(outputDTO, response.getBody());

        verify(unitService).updateUnit(unitId, inputDTO);
    }
}