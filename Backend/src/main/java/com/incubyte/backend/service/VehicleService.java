package com.incubyte.backend.service;

import com.incubyte.backend.dto.VehicleRequest;
import com.incubyte.backend.dto.VehicleResponse;
import com.incubyte.backend.entity.Vehicle;
import com.incubyte.backend.exception.VehicleNotFoundException;
import com.incubyte.backend.repository.VehicleRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    private VehicleResponse mapToResponse(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .make(vehicle.getMake())
                .model(vehicle.getModel())
                .category(vehicle.getCategory())
                .price(vehicle.getPrice())
                .quantity(vehicle.getQuantity())
                .build();
    }

    public VehicleResponse addVehicle(VehicleRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        Vehicle vehicle = Vehicle.builder()
                .make(request.getMake())
                .model(request.getModel())
                .category(request.getCategory())
                .price(request.getPrice())
                .quantity(request.getQuantity())
                .build();
        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        return mapToResponse(savedVehicle);
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> getAllVehicles() {
        return vehicleRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<VehicleResponse> searchVehicles(String query, String make, String model, String category, BigDecimal minPrice, BigDecimal maxPrice) {
        
        String normQuery    = (query    != null && query.isBlank())    ? null : query;
        String normMake     = (make     != null && make.isBlank())     ? null : make;
        String normModel    = (model    != null && model.isBlank())    ? null : model;
        String normCategory = (category != null && category.isBlank()) ? null : category;

        return vehicleRepository.searchVehicles(normQuery, normMake, normModel, normCategory, minPrice, maxPrice).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public VehicleResponse updateVehicle(Long id, VehicleRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with id: " + id));

        vehicle.setMake(request.getMake());
        vehicle.setModel(request.getModel());
        vehicle.setCategory(request.getCategory());
        vehicle.setPrice(request.getPrice());
        vehicle.setQuantity(request.getQuantity());

        Vehicle updatedVehicle = vehicleRepository.save(vehicle);
        return mapToResponse(updatedVehicle);
    }

    public void deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new VehicleNotFoundException("Vehicle not found with id: " + id);
        }
        vehicleRepository.deleteById(id);
    }

    public VehicleResponse purchaseVehicle(Long id) {
        // Defence-in-depth: reject ADMIN callers even if the filter chain is bypassed
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> "ADMIN".equals(a.getAuthority()))) {
            throw new AccessDeniedException("Admins are not allowed to purchase vehicles");
        }

        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with id: " + id));
        if (vehicle.getQuantity() <= 0) {
            throw new IllegalStateException("Vehicle is out of stock");
        }
        vehicle.setQuantity(vehicle.getQuantity() - 1);
        Vehicle updated = vehicleRepository.save(vehicle);
        return mapToResponse(updated);
    }

    public VehicleResponse restockVehicle(Long id, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Restock quantity must be positive");
        }
        Vehicle vehicle = vehicleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Vehicle not found with id: " + id));
        vehicle.setQuantity(vehicle.getQuantity() + quantity);
        Vehicle updated = vehicleRepository.save(vehicle);
        return mapToResponse(updated);
    }
}
