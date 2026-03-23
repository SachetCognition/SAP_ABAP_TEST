package com.migration.sap.service;

import com.migration.sap.dto.SalesOrderResponse;
import com.migration.sap.exception.InvalidInputException;
import com.migration.sap.repository.SalesOrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalesOrderServiceTest {

    @Mock
    private SalesOrderRepository repository;

    @InjectMocks
    private SalesOrderService service;

    private Object[] createRow(String vbeln, String posnr, String auart, String matnr,
                                String maktx, String meins, BigDecimal kwmeng, String vrkme,
                                String pstyv, String vkorg, String vtweg, String spart,
                                Date erdat, String ernam) {
        return new Object[]{vbeln, posnr, auart, matnr, maktx, meins, kwmeng, vrkme,
                pstyv, vkorg, vtweg, spart, erdat, ernam};
    }

    @Test
    @DisplayName("P3-UT01: blank matnr -> InvalidInputException with correct message")
    void testBlankMatnr() {
        InvalidInputException ex = assertThrows(InvalidInputException.class,
                () -> service.getOrders("", null, "E", 0));
        assertEquals("Material (IV_MATNR) is required", ex.getMessage());
    }

    @Test
    @DisplayName("P3-UT01b: null matnr -> InvalidInputException")
    void testNullMatnr() {
        assertThrows(InvalidInputException.class,
                () -> service.getOrders(null, null, "E", 0));
    }

    @Test
    @DisplayName("P3-UT02: input '123' -> verify '000000000000000123' used in query")
    void testAlphaConversion() {
        when(repository.findByMaterial(eq("000000000000000123"), eq("E"))).thenReturn(Collections.emptyList());

        service.getOrders("123", null, "E", 0);

        verify(repository).findByMaterial(eq("000000000000000123"), eq("E"));
    }

    @Test
    @DisplayName("P3-UT03: vbeln null -> findByMaterial called")
    void testNoVbelnFilter() {
        when(repository.findByMaterial(anyString(), eq("E"))).thenReturn(Collections.emptyList());

        service.getOrders("MAT001", null, "E", 0);

        verify(repository).findByMaterial(anyString(), eq("E"));
        verify(repository, never()).findByMaterialAndOrder(anyString(), anyString(), anyString());
    }

    @Test
    @DisplayName("P3-UT04: vbeln provided -> findByMaterialAndOrder called")
    void testWithVbelnFilter() {
        when(repository.findByMaterialAndOrder(anyString(), eq("0000000001"), eq("E")))
                .thenReturn(Collections.emptyList());

        service.getOrders("MAT001", "0000000001", "E", 0);

        verify(repository).findByMaterialAndOrder(anyString(), eq("0000000001"), eq("E"));
        verify(repository, never()).findByMaterial(anyString(), anyString());
    }

    @Test
    @DisplayName("P3-UT05: maxRows=2, 5 results -> 2 returned")
    void testMaxRows() {
        List<Object[]> mockData = new ArrayList<>();
        mockData.add(createRow("001", "10", "TA", "MAT001", "Prod A", "EA", new BigDecimal("10"), "EA", "TAN", "1000", "10", "00", Date.valueOf("2024-01-15"), "SMITH"));
        mockData.add(createRow("002", "10", "TA", "MAT001", "Prod A", "EA", new BigDecimal("20"), "EA", "TAN", "2000", "10", "00", Date.valueOf("2024-02-20"), "JONES"));
        mockData.add(createRow("003", "10", "SO", "MAT001", "Prod A", "EA", new BigDecimal("15"), "EA", "TAO", "1000", "20", "01", Date.valueOf("2024-03-10"), "BROWN"));
        mockData.add(createRow("004", "10", "TA", "MAT001", "Prod A", "EA", new BigDecimal("30"), "EA", "TAN", "3000", "10", "00", Date.valueOf("2024-04-05"), "DAVIS"));
        mockData.add(createRow("005", "10", "TA", "MAT001", "Prod A", "EA", new BigDecimal("25"), "EA", "TAN", "1000", "10", "00", Date.valueOf("2024-05-01"), "WHITE"));

        when(repository.findByMaterial(anyString(), eq("E"))).thenReturn(mockData);

        SalesOrderResponse response = service.getOrders("MAT001", null, "E", 2);

        assertEquals(2, response.getCount());
        assertEquals(2, response.getData().size());
    }

    @Test
    @DisplayName("P3-UT06: maxRows=0 -> all returned")
    void testMaxRowsZero() {
        List<Object[]> mockData = new ArrayList<>();
        mockData.add(createRow("001", "10", "TA", "MAT001", "Prod A", "EA", new BigDecimal("10"), "EA", "TAN", "1000", "10", "00", Date.valueOf("2024-01-15"), "SMITH"));
        mockData.add(createRow("002", "10", "TA", "MAT001", "Prod A", "EA", new BigDecimal("20"), "EA", "TAN", "2000", "10", "00", Date.valueOf("2024-02-20"), "JONES"));
        mockData.add(createRow("003", "10", "SO", "MAT001", "Prod A", "EA", new BigDecimal("15"), "EA", "TAO", "1000", "20", "01", Date.valueOf("2024-03-10"), "BROWN"));

        when(repository.findByMaterial(anyString(), eq("E"))).thenReturn(mockData);

        SalesOrderResponse response = service.getOrders("MAT001", null, "E", 0);

        assertEquals(3, response.getCount());
        assertEquals(3, response.getData().size());
    }

    @Test
    @DisplayName("P3-UT07: no orders -> messages list contains 'No sales orders found...'")
    void testNoOrders() {
        when(repository.findByMaterial(anyString(), eq("E"))).thenReturn(Collections.emptyList());

        SalesOrderResponse response = service.getOrders("MAT004", null, "E", 0);

        assertEquals(0, response.getCount());
        assertFalse(response.getMessages().isEmpty());
        assertEquals("No sales orders found for given material/filters", response.getMessages().get(0).getMessage());
        assertEquals("S", response.getMessages().get(0).getType());
    }

    @Test
    @DisplayName("P3-UT08: language=D -> German maktx")
    void testLanguageD() {
        List<Object[]> mockData = new ArrayList<>();
        mockData.add(createRow("001", "10", "TA", "MAT001", "Fertigprodukt Alpha", "EA", new BigDecimal("10"), "EA", "TAN", "1000", "10", "00", Date.valueOf("2024-01-15"), "SMITH"));

        when(repository.findByMaterial(anyString(), eq("D"))).thenReturn(mockData);

        SalesOrderResponse response = service.getOrders("MAT001", null, "D", 0);

        verify(repository).findByMaterial(anyString(), eq("D"));
        assertEquals("Fertigprodukt Alpha", response.getData().get(0).getMaktx());
    }

    @Test
    @DisplayName("P3-UT09: count matches data.size()")
    void testCountMatchesSize() {
        List<Object[]> mockData = new ArrayList<>();
        mockData.add(createRow("001", "10", "TA", "MAT001", "Prod A", "EA", new BigDecimal("10"), "EA", "TAN", "1000", "10", "00", Date.valueOf("2024-01-15"), "SMITH"));
        mockData.add(createRow("002", "10", "TA", "MAT001", "Prod A", "EA", new BigDecimal("20"), "EA", "TAN", "2000", "10", "00", Date.valueOf("2024-02-20"), "JONES"));

        when(repository.findByMaterial(anyString(), eq("E"))).thenReturn(mockData);

        SalesOrderResponse response = service.getOrders("MAT001", null, "E", 0);

        assertEquals(response.getData().size(), response.getCount());
    }

    @Test
    @DisplayName("P3-UT10: DataAccessException -> proper error response (handled by GlobalExceptionHandler)")
    void testDataAccessException() {
        when(repository.findByMaterial(anyString(), eq("E")))
                .thenThrow(new org.springframework.dao.DataRetrievalFailureException("DB error"));

        assertThrows(org.springframework.dao.DataAccessException.class,
                () -> service.getOrders("MAT001", null, "E", 0));
    }
}
