# Artifact Capture Guide

> Instructions for capturing, storing, and organising test evidence for each phase of the SAP ABAP-to-Java migration.

---

## Table of Contents

1. [SAP GUI Screenshots](#1-sap-gui-screenshots)
2. [SAP GUI Video Recording](#2-sap-gui-video-recording)
3. [JUnit Reports (Maven Surefire)](#3-junit-reports-maven-surefire)
4. [Postman Collections & Responses](#4-postman-collections--responses)
5. [Cypress / Playwright Automatic Capture](#5-cypress--playwright-automatic-capture)
6. [Cross-Validation Diff Reports](#6-cross-validation-diff-reports)
7. [Side-by-Side Visual Comparison](#7-side-by-side-visual-comparison)
8. [File Naming Convention](#8-file-naming-convention)

---

## 1. SAP GUI Screenshots

**Used in:** Phase 0 (Baseline), Phase 4 (Front-End comparison reference)

### Option A -- SAP GUI Built-in Screenshot

1. Navigate to the target transaction / report output in SAP GUI.
2. Press **Ctrl+Shift+PrintScreen** (or use menu **System > List > Save > Local File > Unconverted**).
3. Save as `.png` or `.bmp`.
4. Rename following the naming convention (see [Section 8](#8-file-naming-convention)).
5. Place in the appropriate `test-artifacts/` subdirectory.

### Option B -- Snagit / Windows Snipping Tool

1. Open Snagit (or press `Win+Shift+S` for Snip & Sketch).
2. Select the region containing the SAP GUI window.
3. Annotate if needed (highlight fields, add callouts for key values).
4. Save as `.png`.
5. Place in the appropriate `test-artifacts/` subdirectory.

### Tips

- Always capture the **full ALV grid** including column headers and totals row.
- Include the **selection screen** in a separate screenshot so inputs are traceable.
- For SE37 test runs, capture both the **input parameters** and the **output table**.

---

## 2. SAP GUI Video Recording

**Used in:** Phase 0 (Baseline), Phase 5 (E2E comparison reference)

### OBS Studio Setup

1. **Download:** [obsproject.com](https://obsproject.com/)
2. **Configure:**
   - Source: **Window Capture** > select SAP GUI window.
   - Output: `Settings > Output > Recording Path` = `test-artifacts/phase0-baseline/videos/`
   - Format: `.mp4` (H.264)
   - Resolution: Native (match SAP GUI window size)
3. **Record:**
   - Click **Start Recording** before executing the transaction.
   - Walk through the complete user journey (enter selection parameters, execute, scroll through ALV).
   - Click **Stop Recording**.
4. **Rename** the file following the naming convention.

### Checklist for Baseline Videos

- [ ] Show selection screen with all parameters filled
- [ ] Show execution (F8 or Execute button)
- [ ] Scroll through full ALV grid (top to bottom)
- [ ] Show totals/aggregation row
- [ ] Show any error/info messages

---

## 3. JUnit Reports (Maven Surefire)

**Used in:** Phase 2 (Back-End Unit), Phase 3 (Back-End Integration)

### Maven Surefire Plugin Configuration

Add to `pom.xml`:

```xml
<build>
  <plugins>
    <plugin>
      <groupId>org.apache.maven.plugins</groupId>
      <artifactId>maven-surefire-plugin</artifactId>
      <version>3.2.5</version>
      <configuration>
        <reportsDirectory>${project.basedir}/test-artifacts/phase2-backend/junit-reports</reportsDirectory>
        <statelessTestsetReporter implementation="org.apache.maven.plugin.surefire.extensions.junit5.JUnit5Xml30StatelessReporter">
          <usePhrasedTestSuiteClassName>true</usePhrasedTestSuiteClassName>
          <usePhrasedTestCaseClassName>true</usePhrasedTestCaseClassName>
          <usePhrasedTestCaseMethodName>true</usePhrasedTestCaseMethodName>
        </statelessTestsetReporter>
      </configuration>
    </plugin>
  </plugins>
</build>
```

### Running Tests

```bash
# Unit tests (Phase 2)
mvn test -Dtest="*UnitTest" -DreportsDirectory=test-artifacts/phase2-backend/junit-reports

# Integration tests (Phase 3)
mvn verify -Dtest="*IntegrationTest" -DreportsDirectory=test-artifacts/phase3-backend/junit-reports
```

### Output

JUnit XML reports are generated at the specified `reportsDirectory`. Each file is named `TEST-<fully.qualified.ClassName>.xml`.

---

## 4. Postman Collections & Responses

**Used in:** Phase 2 (Back-End Unit reference), Phase 3 (Back-End Integration)

### Exporting a Postman Collection

1. In Postman, right-click the collection > **Export**.
2. Choose **Collection v2.1** format.
3. Save to `test-artifacts/phase3-backend/postman/`.

### Exporting Individual Responses

1. After running a request, click the **Save Response** button (floppy disk icon).
2. Choose **Save as Example**.
3. Alternatively, use the Postman CLI (Newman):

```bash
newman run collection.json \
  --reporters cli,json \
  --reporter-json-export test-artifacts/phase3-backend/postman/results.json
```

### Naming Convention

```
<TC-ID>_<endpoint-slug>_<timestamp>.postman_collection.json
<TC-ID>_<endpoint-slug>_<timestamp>_response.json
```

Example: `TC-027_get-sales-orders_20260323.postman_collection.json`

---

## 5. Cypress / Playwright Automatic Capture

**Used in:** Phase 4 (Front-End Unit screenshots), Phase 5 (Front-End E2E)

### Cypress Configuration

Create or update `cypress.config.ts`:

```typescript
import { defineConfig } from 'cypress';

export default defineConfig({
  e2e: {
    baseUrl: 'http://localhost:3000',
    video: true,
    screenshotOnRunFailure: true,
    videosFolder: 'test-artifacts/phase5-frontend/videos',
    screenshotsFolder: 'test-artifacts/phase5-frontend/screenshots',
    supportFile: 'cypress/support/e2e.ts',
    specPattern: 'cypress/e2e/**/*.cy.{ts,tsx}',
  },
  component: {
    screenshotsFolder: 'test-artifacts/phase4-frontend/screenshots',
    videosFolder: 'test-artifacts/phase4-frontend/videos',
    devServer: {
      framework: 'react',
      bundler: 'vite',
    },
  },
});
```

### Playwright Configuration (Alternative)

Create or update `playwright.config.ts`:

```typescript
import { defineConfig } from '@playwright/test';

export default defineConfig({
  use: {
    screenshot: 'on',
    video: 'on',
    trace: 'on-first-retry',
  },
  outputDir: 'test-artifacts/phase5-frontend',
  reporter: [
    ['html', { outputFolder: 'test-artifacts/phase5-frontend/report' }],
    ['junit', { outputFile: 'test-artifacts/phase5-frontend/results.xml' }],
  ],
});
```

### Running Tests

```bash
# Cypress
npx cypress run --spec "cypress/e2e/**/*.cy.ts"

# Playwright
npx playwright test
```

### Manual Screenshots in Tests

```typescript
// Cypress
cy.screenshot('TC-037_selection-screen-renders');

// Playwright
await page.screenshot({ path: 'test-artifacts/phase4-frontend/screenshots/TC-037_selection-screen.png' });
```

---

## 6. Cross-Validation Diff Reports

**Used in:** Phase 6

### Overview

A script reads the SAP baseline CSV export and calls the Java REST API with equivalent parameters. It then compares row-by-row, normalizing field names from SAP conventions to Java naming, and outputs an HTML report.

See `docs/CROSS_VALIDATION_SCRIPT.md` for the full pseudocode and template.

### Quick Reference

```bash
# Node.js
node scripts/cross-validate.js \
  --sap-csv test-artifacts/phase0-baseline/csv-exports/TC-001_fm-default.csv \
  --api-url http://localhost:8080/api/materials/000000000000000123/sales-orders \
  --output test-artifacts/phase6-xvalidation/diff-reports/TC-057_diff.html

# Python
python scripts/cross_validate.py \
  --sap-csv test-artifacts/phase0-baseline/csv-exports/TC-001_fm-default.csv \
  --api-url http://localhost:8080/api/materials/000000000000000123/sales-orders \
  --output test-artifacts/phase6-xvalidation/diff-reports/TC-057_diff.html
```

### Output

An HTML file with:
- Summary: total rows, matching rows, mismatched rows
- Per-row detail table with highlighted differences
- ALPHA conversion notes (leading zeros normalised before comparison)

---

## 7. Side-by-Side Visual Comparison

**Used in:** Phase 6 (TC-066)

### ImageMagick `montage` Command

```bash
# Install ImageMagick (if not present)
sudo apt-get install imagemagick   # Debian/Ubuntu
brew install imagemagick            # macOS

# Create side-by-side comparison
montage \
  test-artifacts/phase0-baseline/screenshots/TC-006_report-aggregated.png \
  test-artifacts/phase5-frontend/screenshots/TC-047_e2e-aggregated.png \
  -tile 2x1 \
  -geometry +10+10 \
  -title "SAP ALV vs. Web UI -- Aggregated All Plants" \
  test-artifacts/phase6-xvalidation/visual/TC-066_side-by-side-aggregated.png
```

### Tips

- Use the same zoom level / window size for both captures.
- Annotate differences with ImageMagick `-annotate` or a tool like GIMP.
- Generate one montage per scenario (aggregated, by-plant, top-N, etc.).

---

## 8. File Naming Convention

All artifact files should follow this pattern:

```
<TC-ID>_<short-description>_<YYYYMMDD>.<ext>
```

**Examples:**

| File Name | Description |
|-----------|-------------|
| `TC-001_fm-default-output_20260323.csv` | Phase 0 FM baseline CSV |
| `TC-006_report-aggregated_20260323.png` | Phase 0 Report screenshot |
| `TC-006_report-aggregated_20260323.mp4` | Phase 0 Report video |
| `TC-027_get-sales-orders_20260323.xml` | Phase 3 JUnit report |
| `TC-057_diff-fm-default_20260323.html` | Phase 6 diff report |
| `TC-066_side-by-side-aggregated_20260323.png` | Phase 6 visual comparison |

---

## Storage

All artifacts are stored in the `test-artifacts/` directory tree:

```
test-artifacts/
  phase0-baseline/
    videos/
    screenshots/
    csv-exports/
  phase2-backend/
    junit-reports/
    postman/
  phase3-backend/
    junit-reports/
    postman/
  phase4-frontend/
    screenshots/
    videos/
  phase5-frontend/
    screenshots/
    videos/
  phase6-xvalidation/
    diff-reports/
    visual/
  phase7-nonfunctional/
```

Artifacts are uploaded to GitHub Actions via `actions/upload-artifact@v4` in the CI workflow (`.github/workflows/test-artifacts.yml`).
