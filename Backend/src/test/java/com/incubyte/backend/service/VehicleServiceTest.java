package com.incubyte.backend.service;

import com.incubyte.backend.dto.VehicleRequest;
import com.incubyte.backend.dto.VehicleResponse;
import com.incubyte.backend.entity.Vehicle;
import com.incubyte.backend.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository vehicleRepository;

    private VehicleService vehicleService;

    @BeforeEach
    void setUp() {
        vehicleService = new VehicleService(vehicleRepository);
    }

    @Test
    @DisplayName("addVehicle should successfully save and return the vehicle")
    void testAddVehicle_Success() {
        VehicleRequest request = new VehicleRequest("Honda", "Civic", "Sedan", BigDecimal.valueOf(25000), 5);
        Vehicle savedVehicle = new Vehicle(1L, "Honda", "Civic", "Sedan", BigDecimal.valueOf(25000), 5);

        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(savedVehicle);

        VehicleResponse response = vehicleService.addVehicle(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Honda", response.getMake());
        assertEquals("Civic", response.getModel());
        assertEquals("Sedan", response.getCategory());
        assertEquals(BigDecimal.valueOf(25000), response.getPrice());
        assertEquals(5, response.getQuantity());

        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("getAllVehicles should return a list of all vehicle responses")
    void testGetAllVehicles() {
        Vehicle vehicle1 = new Vehicle(1L, "Toyota", "Corolla", "Sedan", BigDecimal.valueOf(22000), 10);
        Vehicle vehicle2 = new Vehicle(2L, "Ford", "Mustang", "Coupe", BigDecimal.valueOf(36000), 3);

        when(vehicleRepository.findAll()).thenReturn(Arrays.asList(vehicle1, vehicle2));

        List<VehicleResponse> responses = vehicleService.getAllVehicles();

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Toyota", responses.get(0).getMake());
        assertEquals("Ford", responses.get(1).getMake());

        verify(vehicleRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("updateVehicle should modify details of an existing vehicle and return it")
    void testUpdateVehicle_Success() {
        Long vehicleId = 1L;
        VehicleRequest updateRequest = new VehicleRequest("Toyota", "Camry", "Sedan", BigDecimal.valueOf(28000), 8);
        Vehicle existingVehicle = new Vehicle(vehicleId, "Toyota", "Corolla", "Sedan", BigDecimal.valueOf(22000), 10);
        Vehicle updatedVehicle = new Vehicle(vehicleId, "Toyota", "Camry", "Sedan", BigDecimal.valueOf(28000), 8);

        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.of(existingVehicle));
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(updatedVehicle);

        VehicleResponse response = vehicleService.updateVehicle(vehicleId, updateRequest);

        assertNotNull(response);
        assertEquals(vehicleId, response.getId());
        assertEquals("Camry", response.getModel());
        assertEquals(BigDecimal.valueOf(28000), response.getPrice());
        assertEquals(8, response.getQuantity());

        verify(vehicleRepository, times(1)).findById(vehicleId);
        verify(vehicleRepository, times(1)).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("updateVehicle should throw IllegalArgumentException if vehicle does not exist")
    void testUpdateVehicle_NotFound() {
        Long vehicleId = 99L;
        VehicleRequest request = new VehicleRequest("Toyota", "Camry", "Sedan", BigDecimal.valueOf(28000), 8);

        when(vehicleRepository.findById(vehicleId)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> vehicleService.updateVehicle(vehicleId, request));

        verify(vehicleRepository, times(1)).findById(vehicleId);
        verify(vehicleRepository, never()).save(any(Vehicle.class));
    }

    @Test
    @DisplayName("deleteVehicle should delete the vehicle if it exists")
    void testDeleteVehicle_Success() {
        Long vehicleId = 1L;
        when(vehicleRepository.existsById(vehicleId)).thenReturn(true);
        doNothing().when(vehicleRepository).deleteById(vehicleId);

        assertDoesNotThrow(() -> vehicleService.deleteVehicle(vehicleId));

        verify(vehicleRepository, times(1)).existsById(vehicleId);
        verify(vehicleRepository, times(1)).deleteById(vehicleId);
    }

    @Test
    @DisplayName("deleteVehicle should throw IllegalArgumentException if vehicle does not exist")
    void testDeleteVehicle_NotFound() {
        Long vehicleId = 99L;
        when(vehicleRepository.existsById(vehicleId)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> vehicleService.deleteVehicle(vehicleId));

        verify(vehicleRepository, times(1)).existsById(vehicleId);
        verify(vehicleRepository, never()).deleteById(anyLong());
    }
}
