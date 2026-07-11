package com.incubyte.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.incubyte.backend.dto.VehicleRequest;
import com.incubyte.backend.dto.VehicleResponse;
import com.incubyte.backend.service.VehicleService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehicleController.class)
@org.springframework.context.annotation.Import({com.incubyte.backend.config.SecurityConfig.class, com.incubyte.backend.config.JwtAuthenticationFilter.class})
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VehicleService vehicleService;

    @MockBean
    private com.incubyte.backend.service.JwtService jwtService;

    @Test
    @WithMockUser(username = "admin@example.com", authorities = "ADMIN")
    @DisplayName("POST /api/vehicles - Success as Admin")
    void testAddVehicle_AdminSuccess() throws Exception {
        VehicleRequest request = new VehicleRequest("Honda", "Civic", "Sedan", BigDecimal.valueOf(25000), 5);
        VehicleResponse response = new VehicleResponse(1L, "Honda", "Civic", "Sedan", BigDecimal.valueOf(25000), 5);

        when(vehicleService.addVehicle(any(VehicleRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/vehicles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.make").value("Honda"))
                .andExpect(jsonPath("$.quantity").value(5));

        verify(vehicleService).addVehicle(any(VehicleRequest.class));
    }

    @Test
    @WithMockUser(username = "user@example.com", authorities = "USER")
    @DisplayName("POST /api/vehicles - Forbidden as normal User")
    void testAddVehicle_UserForbidden() throws Exception {
        VehicleRequest request = new VehicleRequest("Honda", "Civic", "Sedan", BigDecimal.valueOf(25000), 5);

        mockMvc.perform(post("/api/vehicles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(vehicleService, never()).addVehicle(any(VehicleRequest.class));
    }

    @Test
    @WithMockUser(username = "user@example.com", authorities = "USER")
    @DisplayName("GET /api/vehicles - Success for authenticated User")
    void testGetAllVehicles_Success() throws Exception {
        VehicleResponse response1 = new VehicleResponse(1L, "Toyota", "Corolla", "Sedan", BigDecimal.valueOf(22000), 10);
        VehicleResponse response2 = new VehicleResponse(2L, "Ford", "Mustang", "Coupe", BigDecimal.valueOf(36000), 3);

        when(vehicleService.getAllVehicles()).thenReturn(Arrays.asList(response1, response2));

        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].make").value("Toyota"))
                .andExpect(jsonPath("$[1].make").value("Ford"));

        verify(vehicleService).getAllVehicles();
    }

    @Test
    @DisplayName("GET /api/vehicles - Unauthorized when not logged in")
    void testGetAllVehicles_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@example.com", authorities = "USER")
    @DisplayName("GET /api/vehicles/search - Success with query parameters")
    void testSearchVehicles_Success() throws Exception {
        VehicleResponse response = new VehicleResponse(1L, "Honda", "Civic", "Sedan", BigDecimal.valueOf(25000), 5);

        when(vehicleService.searchVehicles(isNull(), eq("Honda"), eq("Civic"), any(), any(), any()))
                .thenReturn(Arrays.asList(response));

        mockMvc.perform(get("/api/vehicles/search")
                        .param("make", "Honda")
                        .param("model", "Civic"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].make").value("Honda"));

        verify(vehicleService).searchVehicles(isNull(), eq("Honda"), eq("Civic"), any(), any(), any());
    }

    @Test
    @WithMockUser(username = "admin@example.com", authorities = "ADMIN")
    @DisplayName("PUT /api/vehicles/{id} - Success as Admin")
    void testUpdateVehicle_AdminSuccess() throws Exception {
        VehicleRequest request = new VehicleRequest("Honda", "Civic", "Sedan", BigDecimal.valueOf(26000), 4);
        VehicleResponse response = new VehicleResponse(1L, "Honda", "Civic", "Sedan", BigDecimal.valueOf(26000), 4);

        when(vehicleService.updateVehicle(eq(1L), any(VehicleRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/vehicles/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.price").value(26000))
                .andExpect(jsonPath("$.quantity").value(4));

        verify(vehicleService).updateVehicle(eq(1L), any(VehicleRequest.class));
    }

    @Test
    @WithMockUser(username = "user@example.com", authorities = "USER")
    @DisplayName("PUT /api/vehicles/{id} - Forbidden as normal User")
    void testUpdateVehicle_UserForbidden() throws Exception {
        VehicleRequest request = new VehicleRequest("Honda", "Civic", "Sedan", BigDecimal.valueOf(26000), 4);

        mockMvc.perform(put("/api/vehicles/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());

        verify(vehicleService, never()).updateVehicle(anyLong(), any(VehicleRequest.class));
    }

    @Test
    @WithMockUser(username = "admin@example.com", authorities = "ADMIN")
    @DisplayName("DELETE /api/vehicles/{id} - Success as Admin")
    void testDeleteVehicle_AdminSuccess() throws Exception {
        doNothing().when(vehicleService).deleteVehicle(1L);

        mockMvc.perform(delete("/api/vehicles/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());

        verify(vehicleService).deleteVehicle(1L);
    }

    @Test
    @WithMockUser(username = "user@example.com", authorities = "USER")
    @DisplayName("DELETE /api/vehicles/{id} - Forbidden as normal User")
    void testDeleteVehicle_UserForbidden() throws Exception {
        mockMvc.perform(delete("/api/vehicles/1")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(vehicleService, never()).deleteVehicle(anyLong());
    }

    @Test
    @WithMockUser(username = "user@example.com", authorities = "USER")
    @DisplayName("POST /api/vehicles/{id}/purchase - Success for authenticated User")
    void testPurchaseVehicle_Success() throws Exception {
        VehicleResponse response = new VehicleResponse(1L, "Honda", "Civic", "Sedan", BigDecimal.valueOf(25000), 4);
        when(vehicleService.purchaseVehicle(1L)).thenReturn(response);

        mockMvc.perform(post("/api/vehicles/1/purchase")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(4));

        verify(vehicleService).purchaseVehicle(1L);
    }

    @Test
    @WithMockUser(username = "admin@example.com", authorities = "ADMIN")
    @DisplayName("POST /api/vehicles/{id}/restock - Success as Admin")
    void testRestockVehicle_AdminSuccess() throws Exception {
        VehicleResponse response = new VehicleResponse(1L, "Honda", "Civic", "Sedan", BigDecimal.valueOf(25000), 10);
        when(vehicleService.restockVehicle(1L, 5)).thenReturn(response);

        mockMvc.perform(post("/api/vehicles/1/restock")
                        .param("quantity", "5")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(10));

        verify(vehicleService).restockVehicle(1L, 5);
    }

    @Test
    @WithMockUser(username = "user@example.com", authorities = "USER")
    @DisplayName("POST /api/vehicles/{id}/restock - Forbidden as normal User")
    void testRestockVehicle_UserForbidden() throws Exception {
        mockMvc.perform(post("/api/vehicles/1/restock")
                        .param("quantity", "5")
                        .with(csrf()))
                .andExpect(status().isForbidden());

        verify(vehicleService, never()).restockVehicle(anyLong(), anyInt());
    }
}
