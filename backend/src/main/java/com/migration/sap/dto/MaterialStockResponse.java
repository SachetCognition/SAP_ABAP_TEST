package com.migration.sap.dto;

import java.util.List;

public class MaterialStockResponse {

    private List<MaterialStockDTO> data;
    private int count;

    public MaterialStockResponse() {}

    public MaterialStockResponse(List<MaterialStockDTO> data, int count) {
        this.data = data;
        this.count = count;
    }

    public List<MaterialStockDTO> getData() { return data; }
    public void setData(List<MaterialStockDTO> data) { this.data = data; }
    public int getCount() { return count; }
    public void setCount(int count) { this.count = count; }
}
