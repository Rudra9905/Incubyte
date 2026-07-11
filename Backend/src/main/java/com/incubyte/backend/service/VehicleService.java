package com.incubyte.backend.service;

import com.incubyte.backend.dto.VehicleRequest;
import com.incubyte.backend.dto.VehicleResponse;
import com.incubyte.backend.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public VehicleResponse addVehicle(VehicleRequest request) {
        return null;
    }

    public List<VehicleResponse> getAllVehicles() {
        return Collections.emptyList();
    }

    public List<VehicleResponse> searchVehicles(String make, String model, String category, BigDecimal minPrice, BigDecimal maxPrice) {
        return Collections.emptyList();
    }

    public VehicleResponse updateVehicle(Long id, VehicleRequest request) {
        return null;
    }

    public void deleteVehicle(Long id) {
    }
}
