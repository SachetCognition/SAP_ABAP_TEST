package com.migration.sap.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "sales_order_header")
public class SalesOrderHeader {

    @Id
    @Column(name = "vbeln", length = 10)
    private String vbeln;

    @Column(name = "auart", length = 4)
    private String auart;

    @Column(name = "vkorg", length = 4)
    private String vkorg;

    @Column(name = "vtweg", length = 2)
    private String vtweg;

    @Column(name = "spart", length = 2)
    private String spart;

    @Column(name = "erdat")
    private LocalDate erdat;

    @Column(name = "ernam", length = 12)
    private String ernam;

    public SalesOrderHeader() {}

    public String getVbeln() { return vbeln; }
    public void setVbeln(String vbeln) { this.vbeln = vbeln; }
    public String getAuart() { return auart; }
    public void setAuart(String auart) { this.auart = auart; }
    public String getVkorg() { return vkorg; }
    public void setVkorg(String vkorg) { this.vkorg = vkorg; }
    public String getVtweg() { return vtweg; }
    public void setVtweg(String vtweg) { this.vtweg = vtweg; }
    public String getSpart() { return spart; }
    public void setSpart(String spart) { this.spart = spart; }
    public LocalDate getErdat() { return erdat; }
    public void setErdat(LocalDate erdat) { this.erdat = erdat; }
    public String getErnam() { return ernam; }
    public void setErnam(String ernam) { this.ernam = ernam; }
}
