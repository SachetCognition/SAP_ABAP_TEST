package com.migration.sap.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "material_stock")
@IdClass(MaterialStockId.class)
public class MaterialStock {

    @Id
    @Column(name = "matnr", length = 40)
    private String matnr;

    @Id
    @Column(name = "werks", length = 4)
    private String werks;

    @Id
    @Column(name = "lgort", length = 4)
    private String lgort;

    @Column(name = "labst", precision = 13, scale = 3)
    private BigDecimal labst;

    public MaterialStock() {}

    public String getMatnr() { return matnr; }
    public void setMatnr(String matnr) { this.matnr = matnr; }
    public String getWerks() { return werks; }
    public void setWerks(String werks) { this.werks = werks; }
    public String getLgort() { return lgort; }
    public void setLgort(String lgort) { this.lgort = lgort; }
    public BigDecimal getLabst() { return labst; }
    public void setLabst(BigDecimal labst) { this.labst = labst; }
}
