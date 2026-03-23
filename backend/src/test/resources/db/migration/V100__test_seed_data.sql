-- Test-specific seed data for H2 matching SD-1 through SD-7 scenarios

-- Materials (MARA equivalent)
INSERT INTO material (matnr, mtart, meins) VALUES ('000000000000MAT001', 'FERT', 'EA');
INSERT INTO material (matnr, mtart, meins) VALUES ('000000000000MAT002', 'HALB', 'KG');
INSERT INTO material (matnr, mtart, meins) VALUES ('000000000000MAT003', 'ROH',  'L');
INSERT INTO material (matnr, mtart, meins) VALUES ('000000000000MAT004', 'FERT', 'EA');
INSERT INTO material (matnr, mtart, meins) VALUES ('000000000000MAT005', 'FERT', 'M');
INSERT INTO material (matnr, mtart, meins) VALUES ('000000000000000123', 'FERT', 'EA');

-- Material Texts (MAKT equivalent)
-- SD-5: MAT003 has no MAKT entry for language E (only D)
-- SD-6: MAT001 has entries in both E and D
INSERT INTO material_text (matnr, spras, maktx) VALUES ('000000000000MAT001', 'E', 'Finished Product Alpha');
INSERT INTO material_text (matnr, spras, maktx) VALUES ('000000000000MAT001', 'D', 'Fertigprodukt Alpha');
INSERT INTO material_text (matnr, spras, maktx) VALUES ('000000000000MAT002', 'E', 'Semi-Finished Beta');
INSERT INTO material_text (matnr, spras, maktx) VALUES ('000000000000MAT003', 'D', 'Rohstoff Gamma');
INSERT INTO material_text (matnr, spras, maktx) VALUES ('000000000000MAT004', 'E', 'Finished Product Delta');
INSERT INTO material_text (matnr, spras, maktx) VALUES ('000000000000MAT005', 'E', 'Finished Product Epsilon');
INSERT INTO material_text (matnr, spras, maktx) VALUES ('000000000000000123', 'E', 'Test Material 123');

-- Material Stock (MARD equivalent)
INSERT INTO material_stock (matnr, werks, lgort, labst) VALUES ('000000000000MAT001', '1000', '0001', 100.000);
INSERT INTO material_stock (matnr, werks, lgort, labst) VALUES ('000000000000MAT001', '1000', '0002', 50.000);
INSERT INTO material_stock (matnr, werks, lgort, labst) VALUES ('000000000000MAT001', '2000', '0001', 200.000);
INSERT INTO material_stock (matnr, werks, lgort, labst) VALUES ('000000000000MAT002', '1000', '0001', 75.500);
INSERT INTO material_stock (matnr, werks, lgort, labst) VALUES ('000000000000MAT002', '3000', '0001', 120.000);
INSERT INTO material_stock (matnr, werks, lgort, labst) VALUES ('000000000000MAT003', '2000', '0001', 500.000);
INSERT INTO material_stock (matnr, werks, lgort, labst) VALUES ('000000000000MAT004', '1000', '0001', 0.000);
INSERT INTO material_stock (matnr, werks, lgort, labst) VALUES ('000000000000MAT005', '1000', '0001', 300.000);
INSERT INTO material_stock (matnr, werks, lgort, labst) VALUES ('000000000000MAT005', '2000', '0001', 150.000);
INSERT INTO material_stock (matnr, werks, lgort, labst) VALUES ('000000000000000123', '1000', '0001', 42.000);

-- Sales Order Headers (VBAK equivalent)
INSERT INTO sales_order_header (vbeln, auart, vkorg, vtweg, spart, erdat, ernam) VALUES ('0000000001', 'TA', '1000', '10', '00', '2024-01-15', 'SMITH');
INSERT INTO sales_order_header (vbeln, auart, vkorg, vtweg, spart, erdat, ernam) VALUES ('0000000002', 'TA', '2000', '10', '00', '2024-02-20', 'JONES');
INSERT INTO sales_order_header (vbeln, auart, vkorg, vtweg, spart, erdat, ernam) VALUES ('0000000003', 'SO', '1000', '20', '01', '2024-03-10', 'BROWN');
INSERT INTO sales_order_header (vbeln, auart, vkorg, vtweg, spart, erdat, ernam) VALUES ('0000000004', 'TA', '3000', '10', '00', '2024-04-05', 'DAVIS');

-- Sales Order Items (VBAP equivalent)
INSERT INTO sales_order_item (vbeln, posnr, matnr, kwmeng, vrkme, pstyv) VALUES ('0000000001', '000010', '000000000000MAT001', 10.000, 'EA', 'TAN');
INSERT INTO sales_order_item (vbeln, posnr, matnr, kwmeng, vrkme, pstyv) VALUES ('0000000001', '000020', '000000000000MAT002', 5.000, 'KG', 'TAN');
INSERT INTO sales_order_item (vbeln, posnr, matnr, kwmeng, vrkme, pstyv) VALUES ('0000000002', '000010', '000000000000MAT001', 20.000, 'EA', 'TAN');
INSERT INTO sales_order_item (vbeln, posnr, matnr, kwmeng, vrkme, pstyv) VALUES ('0000000003', '000010', '000000000000MAT001', 15.000, 'EA', 'TAO');
INSERT INTO sales_order_item (vbeln, posnr, matnr, kwmeng, vrkme, pstyv) VALUES ('0000000003', '000020', '000000000000MAT003', 100.000, 'L', 'TAO');
INSERT INTO sales_order_item (vbeln, posnr, matnr, kwmeng, vrkme, pstyv) VALUES ('0000000004', '000010', '000000000000MAT005', 50.000, 'M', 'TAN');
