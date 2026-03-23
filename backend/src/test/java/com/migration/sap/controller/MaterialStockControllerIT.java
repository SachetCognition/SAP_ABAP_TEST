package com.migration.sap.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MaterialStockControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("P2-IT01: GET /api/materials/stock with defaults returns all materials")
    void testGetStockDefaults() throws Exception {
        mockMvc.perform(get("/api/materials/stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.count").isNumber());
    }

    @Test
    @DisplayName("P2-IT02: GET /api/materials/stock?byPlant=true returns werks in data")
    void testGetStockByPlant() throws Exception {
        mockMvc.perform(get("/api/materials/stock").param("byPlant", "true"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].werks").exists());
    }

    @Test
    @DisplayName("P2-IT03: GET /api/materials/stock?byPlant=true&plant=1000 filters by plant")
    void testGetStockByPlantSpecific() throws Exception {
        mockMvc.perform(get("/api/materials/stock")
                        .param("byPlant", "true")
                        .param("plant", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[*].werks", everyItem(is("1000"))));
    }

    @Test
    @DisplayName("P2-IT04: GET /api/materials/stock?top=3 returns max 3 rows sorted by stock desc")
    void testGetStockTopN() throws Exception {
        mockMvc.perform(get("/api/materials/stock").param("top", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(lessThanOrEqualTo(3))))
                .andExpect(jsonPath("$.count", lessThanOrEqualTo(3)));
    }

    @Test
    @DisplayName("P2-IT05: GET /api/materials/stock?matnr=NONEXISTENT returns empty")
    void testGetStockNoData() throws Exception {
        mockMvc.perform(get("/api/materials/stock").param("matnr", "NONEXISTENT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)))
                .andExpect(jsonPath("$.count", is(0)));
    }

    @Test
    @DisplayName("P2-IT06: GET /api/materials/stock?mtart=FERT filters by material type")
    void testGetStockByMaterialType() throws Exception {
        mockMvc.perform(get("/api/materials/stock").param("mtart", "FERT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[*].mtart", everyItem(is("FERT"))));
    }

    @Test
    @DisplayName("P2-IT07: GET /api/materials/stock?language=D returns German descriptions")
    void testGetStockGerman() throws Exception {
        mockMvc.perform(get("/api/materials/stock")
                        .param("language", "D")
                        .param("matnr", "000000000000MAT001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].maktx", is("Fertigprodukt Alpha")));
    }
}
