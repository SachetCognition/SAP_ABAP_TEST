package com.migration.sap.controller;

import com.migration.sap.dto.SalesOrderResponse;
import com.migration.sap.service.SalesOrderService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sales-orders")
public class SalesOrderController {

    private final SalesOrderService service;

    public SalesOrderController(SalesOrderService service) {
        this.service = service;
    }

    @GetMapping
    public SalesOrderResponse getOrders(
            @RequestParam(name = "matnr") String matnr,
            @RequestParam(name = "vbeln", required = false) String vbeln,
            @RequestParam(name = "language", defaultValue = "E") String language,
            @RequestParam(name = "maxRows", defaultValue = "0") int maxRows) {

        return service.getOrders(matnr, vbeln, language, maxRows);
    }
}
