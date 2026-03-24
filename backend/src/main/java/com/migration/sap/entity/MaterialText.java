package com.migration.sap.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

@Entity
@Table(name = "material_text")
@IdClass(MaterialTextId.class)
public class MaterialText {

    @Id
    @Column(name = "matnr", length = 40)
    private String matnr;

    @Id
    @Column(name = "spras", length = 1)
    private String spras;

    @Column(name = "maktx", length = 40)
    private String maktx;

    public MaterialText() {}

    public MaterialText(String matnr, String spras, String maktx) {
        this.matnr = matnr;
        this.spras = spras;
        this.maktx = maktx;
    }

    public String getMatnr() { return matnr; }
    public void setMatnr(String matnr) { this.matnr = matnr; }
    public String getSpras() { return spras; }
    public void setSpras(String spras) { this.spras = spras; }
    public String getMaktx() { return maktx; }
    public void setMaktx(String maktx) { this.maktx = maktx; }
}
