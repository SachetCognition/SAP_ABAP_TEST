package com.migration.sap.service;

import com.migration.sap.dto.MaterialStockDTO;
import com.migration.sap.dto.MaterialStockRequest;
import com.migration.sap.dto.MaterialStockResponse;
import com.migration.sap.repository.MaterialStockRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Replicates ZMAT_REPORT.PROG fetch_data business logic (lines 59-172).
 */
@Service
public class MaterialStockService {

    private final MaterialStockRepository repository;

    public MaterialStockService(MaterialStockRepository repository) {
        this.repository = repository;
    }

    public MaterialStockResponse getStock(MaterialStockRequest request) {
        List<String> matnrList = (request.getMatnrList() != null && !request.getMatnrList().isEmpty())
                ? request.getMatnrList() : null;
        List<String> mtartList = (request.getMtartList() != null && !request.getMtartList().isEmpty())
                ? request.getMtartList() : null;
        String lang = request.getLanguage() != null ? request.getLanguage() : "E";
        String plant = (request.getPlant() != null && !request.getPlant().isBlank())
                ? request.getPlant() : null;
        boolean byPlant = request.isByPlant();

        List<Object[]> rawResults;

        if (byPlant) {
            if (plant == null) {
                // All plants mode, by plant lines (ZMAT_REPORT.PROG lines 69-83)
                rawResults = repository.findByPlantAllPlants(lang, matnrList, mtartList);
            } else {
                // Specific plant detail (ZMAT_REPORT.PROG lines 84-100)
                rawResults = repository.findByPlantSpecific(lang, plant, matnrList, mtartList);
            }
        } else {
            if (plant == null) {
                // Aggregated across all plants (ZMAT_REPORT.PROG lines 114-129)
                rawResults = repository.findAggregatedAllPlants(lang, matnrList, mtartList);
            } else {
                // Aggregated within a specific plant (ZMAT_REPORT.PROG lines 130-145)
                rawResults = repository.findAggregatedSpecificPlant(lang, plant, matnrList, mtartList);
            }
        }

        List<MaterialStockDTO> items = mapToDto(rawResults, byPlant, plant);

        // Sorting and top-N (ZMAT_REPORT.PROG lines 164-170)
        if (request.getTop() > 0) {
            // Sort by labst DESC then matnr ASC (line 166)
            items.sort(Comparator.comparing(MaterialStockDTO::getLabst).reversed()
                    .thenComparing(MaterialStockDTO::getMatnr));
            // Truncate to top rows (line 167)
            if (items.size() > request.getTop()) {
                items = new ArrayList<>(items.subList(0, request.getTop()));
            }
        } else {
            // Sort by matnr ASC then werks ASC (line 169)
            items.sort(Comparator.comparing(MaterialStockDTO::getMatnr)
                    .thenComparing(dto -> dto.getWerks() != null ? dto.getWerks() : ""));
        }

        return new MaterialStockResponse(items, items.size());
    }

    private List<MaterialStockDTO> mapToDto(List<Object[]> rawResults, boolean byPlant, String plant) {
        List<MaterialStockDTO> items = new ArrayList<>();
        for (Object[] row : rawResults) {
            MaterialStockDTO dto = new MaterialStockDTO();
            dto.setMatnr(row[0] != null ? row[0].toString() : null);
            dto.setMaktx(row[1] != null ? row[1].toString() : null);
            dto.setMtart(row[2] != null ? row[2].toString() : null);
            dto.setMeins(row[3] != null ? row[3].toString() : null);

            if (byPlant) {
                // byPlant queries return 6 columns including werks
                dto.setWerks(row[4] != null ? row[4].toString() : null);
                dto.setLabst(row[5] != null ? new BigDecimal(row[5].toString()) : BigDecimal.ZERO);
            } else {
                // Aggregated queries return 5 columns, no werks column
                // Replicates ZMAT_REPORT.PROG lines 154-158
                if (plant != null) {
                    dto.setWerks(plant);
                } else {
                    dto.setWerks(null); // CLEAR gs_out-werks (line 155)
                }
                dto.setLabst(row[4] != null ? new BigDecimal(row[4].toString()) : BigDecimal.ZERO);
            }

            items.add(dto);
        }
        return items;
    }
}
