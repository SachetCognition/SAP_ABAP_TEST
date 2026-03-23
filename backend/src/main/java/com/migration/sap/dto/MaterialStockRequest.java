package com.migration.sap.dto;

import java.util.List;

/**
 * Maps to ZMAT_REPORT.PROG selection screen parameters (lines 32-39).
 */
public class MaterialStockRequest {

    private List<String> matnrList;
    private List<String> mtartList;
    private String plant;
    private String language = "E";
    private boolean byPlant = false;
    private int top = 0;

    public MaterialStockRequest() {}

    public List<String> getMatnrList() { return matnrList; }
    public void setMatnrList(List<String> matnrList) { this.matnrList = matnrList; }
    public List<String> getMtartList() { return mtartList; }
    public void setMtartList(List<String> mtartList) { this.mtartList = mtartList; }
    public String getPlant() { return plant; }
    public void setPlant(String plant) { this.plant = plant; }
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    public boolean isByPlant() { return byPlant; }
    public void setByPlant(boolean byPlant) { this.byPlant = byPlant; }
    public int getTop() { return top; }
    public void setTop(int top) { this.top = top; }
}
