package com.migration.sap.dto;

import java.math.BigDecimal;

/**
 * Maps to ty_out in ZMAT_REPORT.PROG lines 14-21.
 * Flattened view of material master + stock data.
 */
public class MaterialStockDTO {

    private String matnr;
    private String maktx;
    private String mtart;
    private String meins;
    private String werks;
    private BigDecimal labst;

    public MaterialStockDTO() {}

    public MaterialStockDTO(String matnr, String maktx, String mtart, String meins, String werks, BigDecimal labst) {
        this.matnr = matnr;
        this.maktx = maktx;
        this.mtart = mtart;
        this.meins = meins;
        this.werks = werks;
        this.labst = labst;
    }

    public String getMatnr() { return matnr; }
    public void setMatnr(String matnr) { this.matnr = matnr; }
    public String getMaktx() { return maktx; }
    public void setMaktx(String maktx) { this.maktx = maktx; }
    public String getMtart() { return mtart; }
    public void setMtart(String mtart) { this.mtart = mtart; }
    public String getMeins() { return meins; }
    public void setMeins(String meins) { this.meins = meins; }
    public String getWerks() { return werks; }
    public void setWerks(String werks) { this.werks = werks; }
    public BigDecimal getLabst() { return labst; }
    public void setLabst(BigDecimal labst) { this.labst = labst; }
}
