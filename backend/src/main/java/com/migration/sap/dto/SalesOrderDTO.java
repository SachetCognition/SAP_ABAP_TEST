package com.migration.sap.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Maps to ty_out in LZFG_MAT_SOTOP.txt lines 6-21.
 * Flattened view of sales order header + item + material data.
 */
public class SalesOrderDTO {

    private String vbeln;
    private String posnr;
    private String auart;
    private String matnr;
    private String maktx;
    private String meins;
    private BigDecimal kwmeng;
    private String vrkme;
    private String pstyv;
    private String vkorg;
    private String vtweg;
    private String spart;
    private LocalDate erdat;
    private String ernam;

    public SalesOrderDTO() {}

    public SalesOrderDTO(String vbeln, String posnr, String auart, String matnr,
                         String maktx, String meins, BigDecimal kwmeng, String vrkme,
                         String pstyv, String vkorg, String vtweg, String spart,
                         LocalDate erdat, String ernam) {
        this.vbeln = vbeln;
        this.posnr = posnr;
        this.auart = auart;
        this.matnr = matnr;
        this.maktx = maktx;
        this.meins = meins;
        this.kwmeng = kwmeng;
        this.vrkme = vrkme;
        this.pstyv = pstyv;
        this.vkorg = vkorg;
        this.vtweg = vtweg;
        this.spart = spart;
        this.erdat = erdat;
        this.ernam = ernam;
    }

    public String getVbeln() { return vbeln; }
    public void setVbeln(String vbeln) { this.vbeln = vbeln; }
    public String getPosnr() { return posnr; }
    public void setPosnr(String posnr) { this.posnr = posnr; }
    public String getAuart() { return auart; }
    public void setAuart(String auart) { this.auart = auart; }
    public String getMatnr() { return matnr; }
    public void setMatnr(String matnr) { this.matnr = matnr; }
    public String getMaktx() { return maktx; }
    public void setMaktx(String maktx) { this.maktx = maktx; }
    public String getMeins() { return meins; }
    public void setMeins(String meins) { this.meins = meins; }
    public BigDecimal getKwmeng() { return kwmeng; }
    public void setKwmeng(BigDecimal kwmeng) { this.kwmeng = kwmeng; }
    public String getVrkme() { return vrkme; }
    public void setVrkme(String vrkme) { this.vrkme = vrkme; }
    public String getPstyv() { return pstyv; }
    public void setPstyv(String pstyv) { this.pstyv = pstyv; }
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
