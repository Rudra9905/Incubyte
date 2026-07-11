package com.incubyte.backend.controller;

import com.incubyte.backend.BaseIntegrationTest;
import com.incubyte.backend.entity.Vehicle;
import com.incubyte.backend.repository.VehicleRepository;
import com.incubyte.backend.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class InventoryIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private JwtService jwtService;

    private Long inStockVehicleId;
    private Long outOfStockVehicleId;

    @BeforeEach
    void setUp() {
        vehicleRepository.deleteAll();

        Vehicle v1 = vehicleRepository.save(new Vehicle(null, "Toyota", "Corolla", "Sedan", BigDecimal.valueOf(22000), 5));
        inStockVehicleId = v1.getId();

        Vehicle v2 = vehicleRepository.save(new Vehicle(null, "Honda", "Civic", "Sedan", BigDecimal.valueOf(25000), 0));
        outOfStockVehicleId = v2.getId();
    }

    @Test
    @DisplayName("POST /api/vehicles/{id}/purchase - Success for authenticated User")
    void testPurchaseVehicle_Success() throws Exception {
        String token = jwtService.generateToken("user@example.com");

        mockMvc.perform(post("/api/vehicles/" + inStockVehicleId + "/purchase")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(4));

        Vehicle updated = vehicleRepository.findById(inStockVehicleId).orElseThrow();
        assertEquals(4, updated.getQuantity());
    }

    @Test
    @DisplayName("POST /api/vehicles/{id}/purchase - Returns 400 Bad Request when Out of Stock")
    void testPurchaseVehicle_OutOfStock() throws Exception {
        String token = jwtService.generateToken("user@example.com");

        mockMvc.perform(post("/api/vehicles/" + outOfStockVehicleId + "/purchase")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/vehicles/{id}/restock - Success for Admin role")
    void testRestockVehicle_AdminSuccess() throws Exception {
        String token = jwtService.generateToken("admin@example.com", "ADMIN");

        mockMvc.perform(post("/api/vehicles/" + inStockVehicleId + "/restock")
                        .param("quantity", "10")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(15));

        Vehicle updated = vehicleRepository.findById(inStockVehicleId).orElseThrow();
        assertEquals(15, updated.getQuantity());
    }

    @Test
    @DisplayName("POST /api/vehicles/{id}/restock - Returns 403 Forbidden for non-Admin user")
    void testRestockVehicle_NonAdminForbidden() throws Exception {
        String token = jwtService.generateToken("user@example.com", "USER");

        mockMvc.perform(post("/api/vehicles/" + inStockVehicleId + "/restock")
                        .param("quantity", "10")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
