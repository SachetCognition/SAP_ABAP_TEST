package com.migration.sap.entity;

import java.io.Serializable;
import java.util.Objects;

public class SalesOrderItemId implements Serializable {

    private String vbeln;
    private String posnr;

    public SalesOrderItemId() {}

    public SalesOrderItemId(String vbeln, String posnr) {
        this.vbeln = vbeln;
        this.posnr = posnr;
    }

    public String getVbeln() { return vbeln; }
    public void setVbeln(String vbeln) { this.vbeln = vbeln; }
    public String getPosnr() { return posnr; }
    public void setPosnr(String posnr) { this.posnr = posnr; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SalesOrderItemId that = (SalesOrderItemId) o;
        return Objects.equals(vbeln, that.vbeln) && Objects.equals(posnr, that.posnr);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vbeln, posnr);
    }
}
