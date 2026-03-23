package com.migration.sap.controller;

import com.migration.sap.dto.MaterialStockRequest;
import com.migration.sap.dto.MaterialStockResponse;
import com.migration.sap.service.MaterialStockService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/materials")
public class MaterialStockController {

    private final MaterialStockService service;

    public MaterialStockController(MaterialStockService service) {
        this.service = service;
    }

    @GetMapping("/stock")
    public MaterialStockResponse getStock(
            @RequestParam(name = "matnr", required = false) List<String> matnr,
            @RequestParam(name = "mtart", required = false) List<String> mtart,
            @RequestParam(name = "plant", required = false) String plant,
            @RequestParam(name = "language", defaultValue = "E") String language,
            @RequestParam(name = "byPlant", defaultValue = "false") boolean byPlant,
            @RequestParam(name = "top", defaultValue = "0") int top) {

        MaterialStockRequest request = new MaterialStockRequest();
        request.setMatnrList(matnr);
        request.setMtartList(mtart);
        request.setPlant(plant);
        request.setLanguage(language);
        request.setByPlant(byPlant);
        request.setTop(top);

        return service.getStock(request);
    }
}
