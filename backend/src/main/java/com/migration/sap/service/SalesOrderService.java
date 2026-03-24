package com.migration.sap.service;

import com.migration.sap.dto.ApiMessage;
import com.migration.sap.dto.SalesOrderDTO;
import com.migration.sap.dto.SalesOrderResponse;
import com.migration.sap.exception.InvalidInputException;
import com.migration.sap.repository.SalesOrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Replicates ZFM_GET_MAT_SO_DETAILS..FUGR.txt business logic.
 */
@Service
public class SalesOrderService {

    private final SalesOrderRepository repository;

    public SalesOrderService(SalesOrderRepository repository) {
        this.repository = repository;
    }

    public SalesOrderResponse getOrders(String matnr, String vbeln, String language, int maxRows) {
        SalesOrderResponse response = new SalesOrderResponse();
        List<ApiMessage> messages = new ArrayList<>();

        // Validate matnr is not blank (FUGR lines 30-33)
        if (matnr == null || matnr.isBlank()) {
            throw new InvalidInputException("Material (IV_MATNR) is required");
        }

        // ALPHA conversion: left-pad with zeros to 18 chars (CONVERSION_EXIT_MATN1_INPUT, FUGR lines 36-39)
        String normalizedMatnr = String.format("%18s", matnr.trim()).replace(' ', '0');

        String lang = (language != null && !language.isBlank()) ? language : "E";

        // Fetch data (FUGR lines 45-68)
        List<Object[]> rawResults;
        if (vbeln == null || vbeln.isBlank()) {
            rawResults = repository.findByMaterial(normalizedMatnr, lang);
        } else {
            rawResults = repository.findByMaterialAndOrder(normalizedMatnr, vbeln, lang);
        }

        List<SalesOrderDTO> items = mapToDto(rawResults);

        // No results message (FUGR lines 70-72)
        if (items.isEmpty()) {
            messages.add(new ApiMessage("S", "ZMSG", "002", "No sales orders found for given material/filters"));
        }

        // Max rows limitation (FUGR lines 94-98)
        // The ABAP code increments lv_max before deleting, effectively keeping maxRows items
        if (maxRows > 0 && items.size() > maxRows) {
            items = new ArrayList<>(items.subList(0, Math.min(maxRows, items.size())));
        }

        response.setData(items);
        response.setCount(items.size());
        response.setMessages(messages);

        return response;
    }

    private List<SalesOrderDTO> mapToDto(List<Object[]> rawResults) {
        List<SalesOrderDTO> items = new ArrayList<>();
        for (Object[] row : rawResults) {
            SalesOrderDTO dto = new SalesOrderDTO();
            dto.setVbeln(row[0] != null ? row[0].toString() : null);
            dto.setPosnr(row[1] != null ? row[1].toString() : null);
            dto.setAuart(row[2] != null ? row[2].toString() : null);
            dto.setMatnr(row[3] != null ? row[3].toString() : null);
            dto.setMaktx(row[4] != null ? row[4].toString() : null);
            dto.setMeins(row[5] != null ? row[5].toString() : null);
            dto.setKwmeng(row[6] != null ? new BigDecimal(row[6].toString()) : BigDecimal.ZERO);
            dto.setVrkme(row[7] != null ? row[7].toString() : null);
            dto.setPstyv(row[8] != null ? row[8].toString() : null);
            dto.setVkorg(row[9] != null ? row[9].toString() : null);
            dto.setVtweg(row[10] != null ? row[10].toString() : null);
            dto.setSpart(row[11] != null ? row[11].toString() : null);
            dto.setErdat(row[12] != null ? LocalDate.parse(row[12].toString()) : null);
            dto.setErnam(row[13] != null ? row[13].toString() : null);
            items.add(dto);
        }
        return items;
    }
}
