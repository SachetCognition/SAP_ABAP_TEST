package com.migration.sap.entity;

import java.io.Serializable;
import java.util.Objects;

public class MaterialTextId implements Serializable {

    private String matnr;
    private String spras;

    public MaterialTextId() {}

    public MaterialTextId(String matnr, String spras) {
        this.matnr = matnr;
        this.spras = spras;
    }

    public String getMatnr() { return matnr; }
    public void setMatnr(String matnr) { this.matnr = matnr; }
    public String getSpras() { return spras; }
    public void setSpras(String spras) { this.spras = spras; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaterialTextId that = (MaterialTextId) o;
        return Objects.equals(matnr, that.matnr) && Objects.equals(spras, that.spras);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matnr, spras);
    }
}
