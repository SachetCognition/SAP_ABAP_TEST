package com.migration.sap.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "material")
public class Material {

    @Id
    @Column(name = "matnr", length = 40)
    private String matnr;

    @Column(name = "mtart", length = 4, nullable = false)
    private String mtart;

    @Column(name = "meins", length = 3)
    private String meins;

    public Material() {}

    public Material(String matnr, String mtart, String meins) {
        this.matnr = matnr;
        this.mtart = mtart;
        this.meins = meins;
    }

    public String getMatnr() { return matnr; }
    public void setMatnr(String matnr) { this.matnr = matnr; }
    public String getMtart() { return mtart; }
    public void setMtart(String mtart) { this.mtart = mtart; }
    public String getMeins() { return meins; }
    public void setMeins(String meins) { this.meins = meins; }
}
