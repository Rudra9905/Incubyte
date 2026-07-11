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
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
class SearchIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private JwtService jwtService;

    private String userToken;

    @BeforeEach
    void setUp() {
        vehicleRepository.deleteAll();

        // Seed test vehicles
        vehicleRepository.saveAll(Arrays.asList(
                new Vehicle(null, "Toyota", "Corolla", "Sedan", BigDecimal.valueOf(22000), 5),
                new Vehicle(null, "Honda", "Civic", "Sedan", BigDecimal.valueOf(25000), 8),
                new Vehicle(null, "Ford", "Mustang", "Coupe", BigDecimal.valueOf(36000), 3),
                new Vehicle(null, "Tesla", "Model 3", "Electric", BigDecimal.valueOf(45000), 4)
        ));

        userToken = jwtService.generateToken("user@example.com", "USER");
    }

    @Test
    @DisplayName("GET /api/vehicles/search - Filter by partial make")
    void testSearch_PartialMake() throws Exception {
        mockMvc.perform(get("/api/vehicles/search")
                        .param("query", "toy")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].make").value("Toyota"));
    }

    @Test
    @DisplayName("GET /api/vehicles/search - Filter by partial model")
    void testSearch_PartialModel() throws Exception {
        mockMvc.perform(get("/api/vehicles/search")
                        .param("query", "civ")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].model").value("Civic"));
    }

    @Test
    @DisplayName("GET /api/vehicles/search - Filter by partial category")
    void testSearch_PartialCategory() throws Exception {
        mockMvc.perform(get("/api/vehicles/search")
                        .param("query", "sed")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].category").value("Sedan"))
                .andExpect(jsonPath("$[1].category").value("Sedan"));
    }

    @Test
    @DisplayName("GET /api/vehicles/search - Filter by price range")
    void testSearch_PriceRange() throws Exception {
        mockMvc.perform(get("/api/vehicles/search")
                        .param("minPrice", "23000")
                        .param("maxPrice", "40000")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].model").value("Civic"))
                .andExpect(jsonPath("$[1].model").value("Mustang"));
    }

    @Test
    @DisplayName("GET /api/vehicles/search - Filter by query AND price range")
    void testSearch_QueryAndPriceRange() throws Exception {
        mockMvc.perform(get("/api/vehicles/search")
                        .param("query", "Model")
                        .param("minPrice", "40000")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].model").value("Model 3"));
    }
}
