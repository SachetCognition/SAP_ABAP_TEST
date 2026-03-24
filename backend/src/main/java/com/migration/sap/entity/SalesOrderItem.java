package com.migration.sap.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "sales_order_item")
@IdClass(SalesOrderItemId.class)
public class SalesOrderItem {

    @Id
    @Column(name = "vbeln", length = 10)
    private String vbeln;

    @Id
    @Column(name = "posnr", length = 6)
    private String posnr;

    @Column(name = "matnr", length = 40)
    private String matnr;

    @Column(name = "kwmeng", precision = 15, scale = 3)
    private BigDecimal kwmeng;

    @Column(name = "vrkme", length = 3)
    private String vrkme;

    @Column(name = "pstyv", length = 4)
    private String pstyv;

    public SalesOrderItem() {}

    public String getVbeln() { return vbeln; }
    public void setVbeln(String vbeln) { this.vbeln = vbeln; }
    public String getPosnr() { return posnr; }
    public void setPosnr(String posnr) { this.posnr = posnr; }
    public String getMatnr() { return matnr; }
    public void setMatnr(String matnr) { this.matnr = matnr; }
    public BigDecimal getKwmeng() { return kwmeng; }
    public void setKwmeng(BigDecimal kwmeng) { this.kwmeng = kwmeng; }
    public String getVrkme() { return vrkme; }
    public void setVrkme(String vrkme) { this.vrkme = vrkme; }
    public String getPstyv() { return pstyv; }
    public void setPstyv(String pstyv) { this.pstyv = pstyv; }
}
