# Artifact Capture Guide

This guide describes how to capture, organize, and archive test artifacts for each phase of the SAP ABAP to Java/Angular migration.

## Directory Structure

```
test-artifacts/
├── phase0-baseline/
│   ├── videos/          # OBS recordings of SAP GUI execution
│   ├── screenshots/     # SAP GUI screenshots
│   └── csv-exports/     # ALV grid CSV exports
├── phase2-backend/
│   ├── junit-reports/   # Maven Surefire XML reports
│   └── postman/         # Postman collection + environment exports
├── phase4-e2e/
│   ├── screenshots/     # Cypress screenshots on assertion
│   └── videos/          # Cypress video recordings
└── phase6-xvalidation/
    └── diff-reports/    # HTML diff reports from cross-validate.js
```

## Phase 0: Baseline Capture (SAP System)

### SAP GUI Screenshots
1. Open SAP GUI and navigate to transaction SE38
2. Execute ZMAT_REPORT with the following parameters:
   - Material: leave blank (all materials)
   - Material Type: leave blank (all types)
   - Plant: leave blank
   - Language: E
   - By Plant: unchecked
   - Top: 0
3. Take screenshot of selection screen → `screenshots/zmat_report_selection.png`
4. Take screenshot of ALV output → `screenshots/zmat_report_output.png`
5. Repeat with byPlant=checked → `screenshots/zmat_report_byplant.png`

### OBS Video Recordings
1. Start OBS recording (1920x1080, 30fps)
2. Execute full ZMAT_REPORT flow from selection screen to ALV display
3. Demonstrate sorting, filtering, and export
4. Save to `videos/zmat_report_full_flow.mp4`
5. Repeat for ZFM_GET_MAT_SO_DETAILS via SE37

### CSV Exports
1. In ALV display, use menu: List → Export → Spreadsheet
2. Save as CSV format
3. Export with default params → `csv-exports/material_stock_default.csv`
4. Export with byPlant=true → `csv-exports/material_stock_byplant.csv`
5. Export sales orders → `csv-exports/sales_orders_mat001.csv`

## Phase 2: Backend Test Artifacts

### JUnit Reports (Maven Surefire)
```bash
cd backend
mvn test
# Reports automatically generated at:
# target/surefire-reports/TEST-*.xml
cp target/surefire-reports/*.xml ../test-artifacts/phase2-backend/junit-reports/
```

### Postman Collections
1. Import the following requests into Postman:

**Material Stock API:**
```
GET http://localhost:8080/api/materials/stock
GET http://localhost:8080/api/materials/stock?byPlant=true
GET http://localhost:8080/api/materials/stock?plant=1000
GET http://localhost:8080/api/materials/stock?top=3
GET http://localhost:8080/api/materials/stock?matnr=MAT001,MAT002
GET http://localhost:8080/api/materials/stock?language=D
```

**Sales Order API:**
```
GET http://localhost:8080/api/sales-orders?matnr=MAT001
GET http://localhost:8080/api/sales-orders?matnr=MAT001&vbeln=0000000001
GET http://localhost:8080/api/sales-orders?matnr=123
GET http://localhost:8080/api/sales-orders?matnr=MAT001&maxRows=2
GET http://localhost:8080/api/sales-orders?matnr=MAT004
GET http://localhost:8080/api/sales-orders  (no matnr - expect 400)
```

2. Run collection and export results
3. Save to `test-artifacts/phase2-backend/postman/`

## Phase 4: E2E Test Artifacts (Cypress)

### Running Cypress Tests
```bash
cd frontend

# Run with video recording
npx cypress run --spec "cypress/e2e/material-stock.cy.ts" --config video=true

# Run with screenshots
npx cypress run --spec "cypress/e2e/sales-order.cy.ts" --config screenshotOnRunFailure=true
```

### Collecting Artifacts
```bash
# Screenshots are saved automatically at:
# frontend/cypress/screenshots/

# Videos are saved automatically at:
# frontend/cypress/videos/

# Copy to test-artifacts
cp -r cypress/screenshots/* ../test-artifacts/phase4-e2e/screenshots/
cp -r cypress/videos/* ../test-artifacts/phase4-e2e/videos/
```

### Screenshot Naming Convention
Screenshots are auto-named by Cypress using the test name:
- `material-stock.cy.ts/Material Stock Report -- P4-E2E01 should display form with all 6 inputs.png`
- `sales-order.cy.ts/Sales Order Details -- P5-E2E01 should display form with all inputs.png`

## Phase 6: Cross-Validation Artifacts

### Running Cross-Validation
```bash
# Ensure Phase 0 CSV exports are in place
ls test-artifacts/phase0-baseline/csv-exports/

# Ensure backend is running
curl http://localhost:8080/api/materials/stock

# Run cross-validation script
node scripts/cross-validate.js

# Diff report generated at:
# test-artifacts/phase6-xvalidation/diff-reports/
```

### Diff Report Format
The HTML diff report includes:
- **Summary table**: Total rows, matches, mismatches, missing
- **Row-by-row comparison**: Side-by-side SAP vs Java values
- **Highlighted differences**: Red for mismatches, yellow for type coercion
- **ALPHA conversion notes**: Material number padding differences

## Side-by-Side Visual Comparison

For final documentation, create side-by-side screenshots:

| SAP GUI (Source) | Angular (Target) |
|---|---|
| ALV grid output | AG Grid output |
| Selection screen | Material Stock form |
| SE37 test | Sales Order form |

Use any image comparison tool (e.g., ImageMagick montage):
```bash
montage sap_alv.png angular_grid.png -tile 2x1 -geometry +10+10 comparison.png
```

## Archiving

All artifacts should be committed to the `test-artifacts/` directory with meaningful filenames and dates:
- `material_stock_default_20260323.csv`
- `junit_phase2_20260323.xml`
- `diff_report_20260323.html`
