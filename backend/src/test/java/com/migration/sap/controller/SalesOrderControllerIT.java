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
class SalesOrderControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("P3-IT01: GET /api/sales-orders?matnr=MAT001 returns orders")
    void testGetOrdersForMaterial() throws Exception {
        mockMvc.perform(get("/api/sales-orders").param("matnr", "MAT001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.count").isNumber());
    }

    @Test
    @DisplayName("P3-IT02: GET /api/sales-orders without matnr returns 400")
    void testGetOrdersMissingMatnr() throws Exception {
        mockMvc.perform(get("/api/sales-orders"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("P3-IT03: GET /api/sales-orders?matnr=MAT001&vbeln=0000000001 returns single order")
    void testGetOrdersWithVbeln() throws Exception {
        mockMvc.perform(get("/api/sales-orders")
                        .param("matnr", "MAT001")
                        .param("vbeln", "0000000001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].vbeln", is("0000000001")));
    }

    @Test
    @DisplayName("P3-IT04: GET /api/sales-orders?matnr=MAT004 returns no orders with message")
    void testGetOrdersNoResults() throws Exception {
        mockMvc.perform(get("/api/sales-orders").param("matnr", "MAT004"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(0)))
                .andExpect(jsonPath("$.messages[0].message", containsString("No sales orders found")));
    }

    @Test
    @DisplayName("P3-IT05: GET /api/sales-orders?matnr=MAT001&maxRows=1 returns 1 row")
    void testGetOrdersMaxRows() throws Exception {
        mockMvc.perform(get("/api/sales-orders")
                        .param("matnr", "MAT001")
                        .param("maxRows", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data", hasSize(1)))
                .andExpect(jsonPath("$.count", is(1)));
    }

    @Test
    @DisplayName("P3-IT06: GET /api/sales-orders?matnr=123 tests ALPHA conversion")
    void testGetOrdersAlphaConversion() throws Exception {
        mockMvc.perform(get("/api/sales-orders").param("matnr", "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray());
    }
}
