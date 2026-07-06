package com.cts.dto;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import com.cts.entity.Property;
import com.cts.enums.UnitStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UnitOutputDTO {

    private int unitId;
    private String type;
    private double areaSqFt;
    private int floor;
    private double rentAmount;
    private double depositAmount;
    private UnitStatus status;
    private LocalDate availableFrom;
    private LocalDate createdAt;
    private LocalDate updatedAt;
    private int propertyId;
    private String propertyName;
    private String propertyCity;
    private String propertyState;
    private String propertyPostalCode;
    private String propertyCountry;
    private List<String> amenities;
    private HashMap<Integer,String> propertyPhotos;
}
