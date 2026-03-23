package com.migration.sap.entity;

import java.io.Serializable;
import java.util.Objects;

public class MaterialStockId implements Serializable {

    private String matnr;
    private String werks;
    private String lgort;

    public MaterialStockId() {}

    public MaterialStockId(String matnr, String werks, String lgort) {
        this.matnr = matnr;
        this.werks = werks;
        this.lgort = lgort;
    }

    public String getMatnr() { return matnr; }
    public void setMatnr(String matnr) { this.matnr = matnr; }
    public String getWerks() { return werks; }
    public void setWerks(String werks) { this.werks = werks; }
    public String getLgort() { return lgort; }
    public void setLgort(String lgort) { this.lgort = lgort; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaterialStockId that = (MaterialStockId) o;
        return Objects.equals(matnr, that.matnr) && Objects.equals(werks, that.werks) && Objects.equals(lgort, that.lgort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matnr, werks, lgort);
    }
}
