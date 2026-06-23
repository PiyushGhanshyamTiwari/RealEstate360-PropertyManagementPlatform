package com.cts.service;

import java.util.List;

import com.cts.dto.PropertyInputDTO;

import com.cts.dto.PropertyOutputDTO;

public interface PropertyService {

    public PropertyOutputDTO addProperty(PropertyInputDTO property, int ownerId);

    public List<PropertyOutputDTO> findPropertyByCity(String city);

    public List<PropertyOutputDTO> findPropertyByState(String state);

    public List<PropertyOutputDTO> findPropertyByOwnerId(int ownerId);
}

