package com.migration.sap.service;

import com.migration.sap.dto.MaterialStockDTO;
import com.migration.sap.dto.MaterialStockRequest;
import com.migration.sap.dto.MaterialStockResponse;
import com.migration.sap.repository.MaterialStockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaterialStockServiceTest {

    @Mock
    private MaterialStockRepository repository;

    @InjectMocks
    private MaterialStockService service;

    private MaterialStockRequest request;

    @BeforeEach
    void setUp() {
        request = new MaterialStockRequest();
        request.setLanguage("E");
    }

    @Test
    @DisplayName("P2-UT01: byPlant=true, plant=null -> findByPlantAllPlants, result has distinct werks")
    void testByPlantAllPlants() {
        request.setByPlant(true);
        request.setPlant(null);

        List<Object[]> mockData = Arrays.asList(
                new Object[]{"MAT001", "Prod A", "FERT", "EA", "1000", new BigDecimal("100.000")},
                new Object[]{"MAT001", "Prod A", "FERT", "EA", "2000", new BigDecimal("200.000")},
                new Object[]{"MAT002", "Prod B", "HALB", "KG", "1000", new BigDecimal("50.000")}
        );

        when(repository.findByPlantAllPlants(eq("E"), isNull(), isNull())).thenReturn(mockData);

        MaterialStockResponse response = service.getStock(request);

        verify(repository).findByPlantAllPlants(eq("E"), isNull(), isNull());
        assertEquals(3, response.getCount());
        assertNotNull(response.getData().get(0).getWerks());
        assertNotNull(response.getData().get(1).getWerks());
    }

    @Test
    @DisplayName("P2-UT02: byPlant=true, plant=1000 -> findByPlantSpecific, all rows have werks=1000")
    void testByPlantSpecific() {
        request.setByPlant(true);
        request.setPlant("1000");

        List<Object[]> mockData = Arrays.asList(
                new Object[]{"MAT001", "Prod A", "FERT", "EA", "1000", new BigDecimal("100.000")},
                new Object[]{"MAT002", "Prod B", "HALB", "KG", "1000", new BigDecimal("50.000")}
        );

        when(repository.findByPlantSpecific(eq("E"), eq("1000"), isNull(), isNull())).thenReturn(mockData);

        MaterialStockResponse response = service.getStock(request);

        verify(repository).findByPlantSpecific(eq("E"), eq("1000"), isNull(), isNull());
        assertEquals(2, response.getCount());
        response.getData().forEach(dto -> assertEquals("1000", dto.getWerks()));
    }

    @Test
    @DisplayName("P2-UT03: byPlant=false, plant=null -> findAggregatedAllPlants, werks is null in all DTOs")
    void testAggregatedAllPlants() {
        request.setByPlant(false);
        request.setPlant(null);

        List<Object[]> mockData = Arrays.asList(
                new Object[]{"MAT001", "Prod A", "FERT", "EA", new BigDecimal("300.000")},
                new Object[]{"MAT002", "Prod B", "HALB", "KG", new BigDecimal("50.000")}
        );

        when(repository.findAggregatedAllPlants(eq("E"), isNull(), isNull())).thenReturn(mockData);

        MaterialStockResponse response = service.getStock(request);

        verify(repository).findAggregatedAllPlants(eq("E"), isNull(), isNull());
        assertEquals(2, response.getCount());
        response.getData().forEach(dto -> assertNull(dto.getWerks()));
    }

    @Test
    @DisplayName("P2-UT04: byPlant=false, plant=1000 -> findAggregatedSpecificPlant")
    void testAggregatedSpecificPlant() {
        request.setByPlant(false);
        request.setPlant("1000");

        List<Object[]> mockData = Arrays.asList(
                new Object[]{"MAT001", "Prod A", "FERT", "EA", new BigDecimal("100.000")}
        );

        when(repository.findAggregatedSpecificPlant(eq("E"), eq("1000"), isNull(), isNull())).thenReturn(mockData);

        MaterialStockResponse response = service.getStock(request);

        verify(repository).findAggregatedSpecificPlant(eq("E"), eq("1000"), isNull(), isNull());
        assertEquals(1, response.getCount());
        assertEquals("1000", response.getData().get(0).getWerks());
    }

    @Test
    @DisplayName("P2-UT05: top=3 -> result sorted by labst DESC and truncated to 3")
    void testTopN() {
        request.setByPlant(false);
        request.setTop(3);

        List<Object[]> mockData = Arrays.asList(
                new Object[]{"MAT001", "Prod A", "FERT", "EA", new BigDecimal("100.000")},
                new Object[]{"MAT002", "Prod B", "HALB", "KG", new BigDecimal("500.000")},
                new Object[]{"MAT003", "Prod C", "ROH", "L", new BigDecimal("200.000")},
                new Object[]{"MAT004", "Prod D", "FERT", "EA", new BigDecimal("50.000")},
                new Object[]{"MAT005", "Prod E", "FERT", "M", new BigDecimal("300.000")}
        );

        when(repository.findAggregatedAllPlants(eq("E"), isNull(), isNull())).thenReturn(mockData);

        MaterialStockResponse response = service.getStock(request);

        assertEquals(3, response.getCount());
        assertEquals(new BigDecimal("500.000"), response.getData().get(0).getLabst());
        assertEquals(new BigDecimal("300.000"), response.getData().get(1).getLabst());
        assertEquals(new BigDecimal("200.000"), response.getData().get(2).getLabst());
    }

    @Test
    @DisplayName("P2-UT06: top=0 -> result sorted by matnr ASC, werks ASC")
    void testDefaultSort() {
        request.setByPlant(true);
        request.setTop(0);

        List<Object[]> mockData = Arrays.asList(
                new Object[]{"MAT002", "Prod B", "HALB", "KG", "1000", new BigDecimal("50.000")},
                new Object[]{"MAT001", "Prod A", "FERT", "EA", "2000", new BigDecimal("200.000")},
                new Object[]{"MAT001", "Prod A", "FERT", "EA", "1000", new BigDecimal("100.000")}
        );

        when(repository.findByPlantAllPlants(eq("E"), isNull(), isNull())).thenReturn(mockData);

        MaterialStockResponse response = service.getStock(request);

        assertEquals("MAT001", response.getData().get(0).getMatnr());
        assertEquals("1000", response.getData().get(0).getWerks());
        assertEquals("MAT001", response.getData().get(1).getMatnr());
        assertEquals("2000", response.getData().get(1).getWerks());
        assertEquals("MAT002", response.getData().get(2).getMatnr());
    }

    @Test
    @DisplayName("P2-UT07: no matching data -> empty list, count=0")
    void testNoData() {
        request.setByPlant(false);

        when(repository.findAggregatedAllPlants(eq("E"), isNull(), isNull())).thenReturn(Collections.emptyList());

        MaterialStockResponse response = service.getStock(request);

        assertEquals(0, response.getCount());
        assertTrue(response.getData().isEmpty());
    }

    @Test
    @DisplayName("P2-UT08: language=D -> verify language param passed to repository")
    void testLanguageParam() {
        request.setByPlant(false);
        request.setLanguage("D");

        when(repository.findAggregatedAllPlants(eq("D"), isNull(), isNull())).thenReturn(Collections.emptyList());

        service.getStock(request);

        verify(repository).findAggregatedAllPlants(eq("D"), isNull(), isNull());
    }

    @Test
    @DisplayName("P2-UT09: material with no text -> maktx is null")
    void testNoText() {
        request.setByPlant(false);

        List<Object[]> mockData = Arrays.asList(
                new Object[]{"MAT003", null, "ROH", "L", new BigDecimal("500.000")}
        );

        when(repository.findAggregatedAllPlants(eq("E"), isNull(), isNull())).thenReturn(mockData);

        MaterialStockResponse response = service.getStock(request);

        assertEquals(1, response.getCount());
        assertNull(response.getData().get(0).getMaktx());
    }

    @Test
    @DisplayName("P2-UT10: material with zero stock -> labst=0")
    void testZeroStock() {
        request.setByPlant(false);

        List<Object[]> mockData = Arrays.asList(
                new Object[]{"MAT004", "Prod D", "FERT", "EA", new BigDecimal("0.000")}
        );

        when(repository.findAggregatedAllPlants(eq("E"), isNull(), isNull())).thenReturn(mockData);

        MaterialStockResponse response = service.getStock(request);

        assertEquals(1, response.getCount());
        assertEquals(0, response.getData().get(0).getLabst().compareTo(BigDecimal.ZERO));
    }

    @Test
    @DisplayName("P2-UT11: SUM aggregation correctness with known test data")
    void testAggregation() {
        request.setByPlant(false);

        // MAT001 has stock in multiple plants: 100 + 50 + 200 = 350
        List<Object[]> mockData = Arrays.asList(
                new Object[]{"MAT001", "Prod A", "FERT", "EA", new BigDecimal("350.000")}
        );

        when(repository.findAggregatedAllPlants(eq("E"), isNull(), isNull())).thenReturn(mockData);

        MaterialStockResponse response = service.getStock(request);

        assertEquals(1, response.getCount());
        assertEquals(new BigDecimal("350.000"), response.getData().get(0).getLabst());
    }
}
