-- Replaces SAP MARA table
CREATE TABLE material (
    matnr VARCHAR(40) PRIMARY KEY,
    mtart VARCHAR(4) NOT NULL,
    meins VARCHAR(3)
);

-- Replaces SAP MAKT table
CREATE TABLE material_text (
    matnr VARCHAR(40) NOT NULL REFERENCES material(matnr),
    spras CHAR(1) NOT NULL,
    maktx VARCHAR(40),
    PRIMARY KEY (matnr, spras)
);

-- Replaces SAP MARD table
CREATE TABLE material_stock (
    matnr VARCHAR(40) NOT NULL REFERENCES material(matnr),
    werks VARCHAR(4) NOT NULL,
    lgort VARCHAR(4) NOT NULL DEFAULT '',
    labst DECIMAL(13,3) DEFAULT 0,
    PRIMARY KEY (matnr, werks, lgort)
);

-- Replaces SAP VBAK table
CREATE TABLE sales_order_header (
    vbeln VARCHAR(10) PRIMARY KEY,
    auart VARCHAR(4),
    vkorg VARCHAR(4),
    vtweg VARCHAR(2),
    spart VARCHAR(2),
    erdat DATE,
    ernam VARCHAR(12)
);

-- Replaces SAP VBAP table
CREATE TABLE sales_order_item (
    vbeln VARCHAR(10) NOT NULL REFERENCES sales_order_header(vbeln),
    posnr VARCHAR(6) NOT NULL,
    matnr VARCHAR(40) REFERENCES material(matnr),
    kwmeng DECIMAL(15,3),
    vrkme VARCHAR(3),
    pstyv VARCHAR(4),
    PRIMARY KEY (vbeln, posnr)
);
