# SAP ABAP-to-Java/Cloud Migration Plan

> **Repository:** `SachetCognition/SAP_ABAP_TEST`
> **Scope:** Migration of ABAP custom developments (Function Module `ZFM_GET_MAT_SO_DETAILS`, Report `ZMAT_REPORT`) to a Java/Spring Boot REST back-end with a modern web front-end.
> **Total Test Cases:** 70 (TC-001 through TC-070)

---

## Table of Contents

1. [Phase 0 -- Baseline Capture](#phase-0----baseline-capture)
2. [Phase 1 -- Static Analysis & Rule Extraction](#phase-1----static-analysis--rule-extraction)
3. [Phase 2 -- Back-End Unit Tests (Function Module)](#phase-2----back-end-unit-tests-function-module)
4. [Phase 3 -- Back-End Integration Tests (Function Module)](#phase-3----back-end-integration-tests-function-module)
5. [Phase 4 -- Front-End Unit / Component Tests (Report UI)](#phase-4----front-end-unit--component-tests-report-ui)
6. [Phase 5 -- Front-End E2E Tests (Report UI)](#phase-5----front-end-e2e-tests-report-ui)
7. [Phase 6 -- Cross-Validation (SAP vs. Java)](#phase-6----cross-validation-sap-vs-java)
8. [Phase 7 -- Non-Functional & Regression](#phase-7----non-functional--regression)

---

## Phase 0 -- Baseline Capture

**Goal:** Record the current SAP system behaviour so every future test can be compared to an authoritative "golden" output.

**Artifacts:** `test-artifacts/phase0-baseline/`

| TC ID | Test Case Name | ABAP Source Reference | Pass Criteria | Artifact |
|-------|---------------|----------------------|---------------|----------|
| TC-001 | Capture FM default output (material only) | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:45-55` | FM returns rows for a known material; CSV exported matches SAP GUI display | CSV export + screenshot |
| TC-002 | Capture FM output with order filter | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:56-68` | FM returns rows filtered by VBELN; CSV matches GUI | CSV export + screenshot |
| TC-003 | Capture FM output with MAXROWS limit | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:93-99` | Row count in export equals IV_MAXROWS | CSV export |
| TC-004 | Capture FM output with language parameter | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:7,52,63` | MAKTX column reflects requested language | CSV export |
| TC-005 | Capture FM error for missing material | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:30-33` | BAPIRET2 contains type E, message 001 | Screenshot of SE37 test output |
| TC-006 | Capture Report -- aggregated all plants | `ZMAT_REPORT.PROG:113-161` | ALV grid shows blank WERKS, LABST is summed across plants | Video + screenshot |
| TC-007 | Capture Report -- by-plant detail (all) | `ZMAT_REPORT.PROG:65-112` | ALV grid shows WERKS per row | Video + screenshot |
| TC-008 | Capture Report -- specific plant | `ZMAT_REPORT.PROG:84-101` | Only rows for p_werks appear | Screenshot |
| TC-009 | Capture Report -- Top-N restriction | `ZMAT_REPORT.PROG:164-170` | ALV shows exactly p_top rows, sorted DESC by LABST | Screenshot + CSV |
| TC-010 | Capture Report -- no data found | `ZMAT_REPORT.PROG:179-182` | Message "No data found for given selection" displayed | Screenshot |

---

## Phase 1 -- Static Analysis & Rule Extraction

**Goal:** Document every business rule, data type, table join, conversion exit, and error path in the ABAP source so the Java implementation can be verified rule-by-rule.

**Artifacts:** This document itself serves as the Phase 1 artifact; each subsequent phase references the rules below.

### 1.1 Function Module `ZFM_GET_MAT_SO_DETAILS` Rules

| Rule ID | Description | Source Reference |
|---------|-------------|-----------------|
| R-FM-01 | IV_MATNR is mandatory; raise `INVALID_INPUT` when blank | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:30-33` |
| R-FM-02 | Apply ALPHA conversion (leading zeros) via `CONVERSION_EXIT_MATN1_INPUT` | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:36-39` |
| R-FM-03 | When IV_VBELN is initial, select all orders for material (VBAP JOIN VBAK JOIN MAKT JOIN MARA) | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:45-55` |
| R-FM-04 | When IV_VBELN is supplied, add AND filter on VBELN | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:56-68` |
| R-FM-05 | Language filter: MAKT join uses IV_SPRAS (default 'E') | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:7,52,63` |
| R-FM-06 | No-data path: set informational BAPIRET2 message (type S, number 002) | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:70-72` |
| R-FM-07 | Map result set to ty_out structure field-by-field | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:74-91`, `LZFG_MAT_SOTOP.txt:6-21` |
| R-FM-08 | MAXROWS truncation: delete rows beyond IV_MAXROWS (off-by-one: lv_max+1) | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:93-99` |
| R-FM-09 | Return count EV_COUNT = lines(gt_items) | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:101-102,112` |
| R-FM-10 | SQL exception handling: catch cx_sy_open_sql_db | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:104-107` |
| R-FM-11 | BAPIRET2 message helper macro `add_message` | `LZFG_MAT_SOTOP.txt:32-39` |
| R-FM-12 | Global data: gt_items, gs_item, gt_ret, gs_ret, gv_rows | `LZFG_MAT_SOTOP.txt:23-28` |

### 1.2 Report `ZMAT_REPORT` Rules

| Rule ID | Description | Source Reference |
|---------|-------------|-----------------|
| R-RPT-01 | Selection screen: s_matnr (range), s_mtart (range), p_werks (obligatory default blank), p_spras (default SY-LANGU), p_bywrk (checkbox), p_top (integer default 0) | `ZMAT_REPORT.PROG:32-39` |
| R-RPT-02 | By-plant + all plants: GROUP BY MATNR, MAKTX, MTART, MEINS, WERKS | `ZMAT_REPORT.PROG:65-101` |
| R-RPT-03 | By-plant + specific plant: add d~werks = @p_werks; GROUP BY without WERKS (hardcode plant value) | `ZMAT_REPORT.PROG:84-101` |
| R-RPT-04 | Aggregated + all plants: GROUP BY MATNR, MAKTX, MTART, MEINS; WERKS blank | `ZMAT_REPORT.PROG:113-146` |
| R-RPT-05 | Aggregated + specific plant: same GROUP BY; add d~werks = @p_werks filter | `ZMAT_REPORT.PROG:130-146` |
| R-RPT-06 | Top-N: SORT BY labst DESC, matnr ASC; DELETE FROM p_top+1 | `ZMAT_REPORT.PROG:165-167` |
| R-RPT-07 | Default sort: matnr ASC, werks ASC | `ZMAT_REPORT.PROG:169` |
| R-RPT-08 | No data: display message type S "No data found for given selection" | `ZMAT_REPORT.PROG:179-182` |
| R-RPT-09 | ALV factory using CL_SALV_TABLE | `ZMAT_REPORT.PROG:184-190` |
| R-RPT-10 | Column labels configuration (MATNR, MAKTX, MTART, MEINS, WERKS, LABST) | `ZMAT_REPORT.PROG:192-230` |
| R-RPT-11 | Aggregation on LABST column | `ZMAT_REPORT.PROG:233-237` |
| R-RPT-12 | Display settings: striped pattern, dynamic header with date/time | `ZMAT_REPORT.PROG:240-242` |
| R-RPT-13 | All ALV functions enabled | `ZMAT_REPORT.PROG:245-246` |

### 1.3 Data Structures

| Structure | Fields | Source Reference |
|-----------|--------|-----------------|
| ty_out (FM) | vbeln, posnr, auart, matnr, maktx, meins, kwmeng, vrkme, pstyv, vkorg, vtweg, spart, erdat, ernam | `LZFG_MAT_SOTOP.txt:6-21` |
| ty_out (Report) | matnr, maktx, mtart, meins, werks, labst | `ZMAT_REPORT.PROG:14-21` |
| BAPIRET2 return table | type, id, number, message (via add_message macro) | `LZFG_MAT_SOTOP.txt:25-26,32-39` |

---

## Phase 2 -- Back-End Unit Tests (Function Module)

**Goal:** Verify the Java/Spring Boot service layer reproduces every business rule from `ZFM_GET_MAT_SO_DETAILS` in isolation (mocked repository layer).

**Artifacts:** `test-artifacts/phase2-backend/junit-reports/`, `test-artifacts/phase2-backend/postman/`

| TC ID | Test Case Name | ABAP Rule Ref | Pass Criteria | Artifact |
|-------|---------------|---------------|---------------|----------|
| TC-011 | Material required validation | R-FM-01 | 400 Bad Request with error body when matnr is null/empty | JUnit report |
| TC-012 | ALPHA conversion -- short material number | R-FM-02 | Input "123" is padded to "000000000000000123" before DB query | JUnit report |
| TC-013 | ALPHA conversion -- already padded material | R-FM-02 | Input "000000000000000123" remains unchanged | JUnit report |
| TC-014 | ALPHA conversion -- alphanumeric material | R-FM-02 | Non-numeric material (e.g., "ABC-100") not zero-padded | JUnit report |
| TC-015 | Query all orders for material (no VBELN filter) | R-FM-03 | Repository called without VBELN predicate; returns all matching rows | JUnit report |
| TC-016 | Query filtered by VBELN | R-FM-04 | Repository called with VBELN predicate; only matching order returned | JUnit report |
| TC-017 | Language filter defaults to 'E' | R-FM-05 | When spras param omitted, MAKT join uses 'E' | JUnit report |
| TC-018 | Language filter explicit value 'D' | R-FM-05 | MAKT description returned in German | JUnit report |
| TC-019 | No data returns info message | R-FM-06 | Response includes BAPIRET2-equivalent message type S, number 002 | JUnit report |
| TC-020 | Field mapping -- all 14 fields present | R-FM-07 | JSON response contains vbeln, posnr, auart, matnr, maktx, meins, kwmeng, vrkme, pstyv, vkorg, vtweg, spart, erdat, ernam | JUnit report |
| TC-021 | MAXROWS = 5 limits output | R-FM-08 | Exactly 5 items returned when DB has > 5 | JUnit report |
| TC-022 | MAXROWS = 0 returns all | R-FM-08 | No truncation applied | JUnit report |
| TC-023 | MAXROWS off-by-one boundary | R-FM-08 | MAXROWS = N returns exactly N rows (verify ABAP lv_max+1 logic) | JUnit report |
| TC-024 | EV_COUNT matches item count | R-FM-09 | `count` field in response equals array length | JUnit report |
| TC-025 | SQL exception maps to 500 | R-FM-10 | Simulated DB error returns 500 with error body | JUnit report |
| TC-026 | BAPIRET2 message structure | R-FM-11 | Return messages contain type, id, number, message fields | JUnit report |

---

## Phase 3 -- Back-End Integration Tests (Function Module)

**Goal:** Verify end-to-end REST API behaviour against a real (test) database with seed data matching SAP baseline.

**Artifacts:** `test-artifacts/phase3-backend/junit-reports/`, `test-artifacts/phase3-backend/postman/`

| TC ID | Test Case Name | ABAP Rule Ref | Pass Criteria | Artifact |
|-------|---------------|---------------|---------------|----------|
| TC-027 | GET /api/materials/{matnr}/sales-orders -- happy path | R-FM-03, R-FM-07 | 200 OK; JSON matches baseline CSV (TC-001) | JUnit report + Postman export |
| TC-028 | GET with ?vbeln= filter | R-FM-04 | Only rows for that order returned; matches TC-002 baseline | JUnit report + Postman export |
| TC-029 | GET with ?maxrows=3 | R-FM-08 | Exactly 3 rows; matches TC-003 baseline | JUnit report + Postman export |
| TC-030 | GET with ?spras=D | R-FM-05 | Descriptions in German; matches TC-004 baseline | Postman export |
| TC-031 | GET missing material | R-FM-01 | 400 Bad Request | Postman export |
| TC-032 | GET non-existent material | R-FM-06 | 200 OK with empty items + info message | Postman export |
| TC-033 | VBAK-VBAP INNER JOIN correctness | R-FM-03 | No orphan items (every VBAP row has matching VBAK) | JUnit report |
| TC-034 | MAKT LEFT JOIN -- material with no description | R-FM-05 | maktx is null/empty, remaining fields populated | JUnit report |
| TC-035 | MARA LEFT JOIN -- material with no master | R-FM-03 | meins is null/empty, remaining fields populated | JUnit report |
| TC-036 | Seed data row count matches SAP | R-FM-09 | ev_count from SAP == count from Java API for same input | JUnit report |

---

## Phase 4 -- Front-End Unit / Component Tests (Report UI)

**Goal:** Verify the web UI components that replace `ZMAT_REPORT` render correctly in isolation.

**Artifacts:** `test-artifacts/phase4-frontend/screenshots/`, `test-artifacts/phase4-frontend/videos/`

| TC ID | Test Case Name | ABAP Rule Ref | Pass Criteria | Artifact |
|-------|---------------|---------------|---------------|----------|
| TC-037 | Selection screen renders all inputs | R-RPT-01 | Material range, Material type range, Plant, Language, By-plant checkbox, Top-N input all visible | Screenshot |
| TC-038 | Plant field is required | R-RPT-01 | Form validation blocks submit when Plant is empty (mirrors OBLIGATORY) | Screenshot |
| TC-039 | By-plant checkbox toggles mode | R-RPT-02, R-RPT-04 | Checkbox state propagates to API call parameter | Screenshot |
| TC-040 | Top-N input accepts 0 and positive integers | R-RPT-06 | Input validation allows 0 (no limit) and rejects negative/non-numeric | Screenshot |
| TC-041 | Data table renders column headers | R-RPT-10 | Columns: Material, Material Desc, Material Type, Base UoM, Plant, Unrestricted Stock | Screenshot |
| TC-042 | Data table striped pattern | R-RPT-12 | Alternating row colours applied | Screenshot |
| TC-043 | Data table dynamic header with date/time | R-RPT-12 | Header text includes current date and time | Screenshot |
| TC-044 | LABST aggregation footer row | R-RPT-11 | Footer/total row shows SUM of LABST column | Screenshot |
| TC-045 | No data message display | R-RPT-08 | "No data found for given selection" shown when API returns empty | Screenshot |
| TC-046 | Column optimize (auto-width) | R-RPT-10 | Columns auto-sized to content (no truncation of MAKTX) | Screenshot |

---

## Phase 5 -- Front-End E2E Tests (Report UI)

**Goal:** Cypress / Playwright end-to-end tests simulating full user journeys through the migrated report UI.

**Artifacts:** `test-artifacts/phase5-frontend/screenshots/`, `test-artifacts/phase5-frontend/videos/`

| TC ID | Test Case Name | ABAP Rule Ref | Pass Criteria | Artifact |
|-------|---------------|---------------|---------------|----------|
| TC-047 | E2E: Aggregated all-plants report | R-RPT-04 | User leaves Plant blank, unchecks by-plant; table shows blank WERKS, LABST summed | Video + screenshot |
| TC-048 | E2E: By-plant detail (all plants) | R-RPT-02 | User checks by-plant, leaves Plant blank; table shows WERKS per row | Video + screenshot |
| TC-049 | E2E: By-plant detail (specific plant) | R-RPT-03 | User checks by-plant, enters Plant "1000"; only plant 1000 rows | Video + screenshot |
| TC-050 | E2E: Aggregated specific plant | R-RPT-05 | User enters Plant "1000", unchecks by-plant; LABST summed for plant 1000 | Video + screenshot |
| TC-051 | E2E: Top-N = 5 | R-RPT-06 | Only 5 rows shown, sorted LABST DESC | Video + screenshot |
| TC-052 | E2E: Top-N = 0 (no limit) | R-RPT-06, R-RPT-07 | All rows shown, sorted MATNR ASC | Video + screenshot |
| TC-053 | E2E: Material range filter | R-RPT-01 | Only materials in range appear | Video |
| TC-054 | E2E: Material type filter | R-RPT-01 | Only materials of specified MTART appear | Video |
| TC-055 | E2E: No data scenario | R-RPT-08 | Empty state message displayed | Screenshot |
| TC-056 | E2E: Export to Excel/CSV | R-RPT-13 | ALV "Export" equivalent works; downloaded file matches table | Screenshot + downloaded file |

---

## Phase 6 -- Cross-Validation (SAP vs. Java)

**Goal:** Row-by-row comparison of SAP baseline exports (Phase 0) against Java API responses to prove functional equivalence.

**Artifacts:** `test-artifacts/phase6-xvalidation/diff-reports/`, `test-artifacts/phase6-xvalidation/visual/`

| TC ID | Test Case Name | ABAP Rule Ref | Pass Criteria | Artifact |
|-------|---------------|---------------|---------------|----------|
| TC-057 | Diff: FM default output vs. GET /api/materials/{matnr}/sales-orders | R-FM-03, R-FM-07 | 0 row differences after field-name normalization | HTML diff report |
| TC-058 | Diff: FM + VBELN filter vs. GET ?vbeln= | R-FM-04 | 0 row differences | HTML diff report |
| TC-059 | Diff: FM + MAXROWS vs. GET ?maxrows= | R-FM-08 | Row count identical; row content matches | HTML diff report |
| TC-060 | Diff: FM + language DE vs. GET ?spras=D | R-FM-05 | MAKTX values identical | HTML diff report |
| TC-061 | Diff: Report aggregated all plants vs. GET /api/materials/stock | R-RPT-04 | LABST sums identical per MATNR | HTML diff report |
| TC-062 | Diff: Report by-plant vs. GET /api/materials/stock?byPlant=true | R-RPT-02 | LABST values identical per MATNR+WERKS | HTML diff report |
| TC-063 | Diff: Report specific plant vs. GET /api/materials/stock?plant=1000 | R-RPT-03, R-RPT-05 | Values identical for plant 1000 | HTML diff report |
| TC-064 | Diff: Report Top-N=10 vs. GET /api/materials/stock?topN=10 | R-RPT-06 | Same 10 rows in same order | HTML diff report |
| TC-065 | ALPHA conversion alignment | R-FM-02 | Material numbers match after normalization (leading-zero handling) | HTML diff report |
| TC-066 | Side-by-side visual: SAP ALV vs. Web table | R-RPT-10, R-RPT-12 | Visual comparison shows equivalent layout and data | Side-by-side image (montage) |

---

## Phase 7 -- Non-Functional & Regression

**Goal:** Performance, security, and regression checks on the migrated application.

**Artifacts:** `test-artifacts/phase7-nonfunctional/`

| TC ID | Test Case Name | ABAP Rule Ref | Pass Criteria | Artifact |
|-------|---------------|---------------|---------------|----------|
| TC-067 | Performance: API < 2s for 1000 rows | R-FM-08 | p95 response time under 2 seconds with 1000-row result set | JMeter/k6 report |
| TC-068 | Performance: UI renders 1000 rows < 3s | R-RPT-09 | Time-to-interactive under 3 seconds | Lighthouse report |
| TC-069 | Security: SQL injection on matnr parameter | R-FM-01, R-FM-02 | Malicious input rejected; no SQL error exposed | Postman export |
| TC-070 | Regression: Re-run all Phase 6 diffs after code freeze | All | 0 differences on all diff reports | Combined HTML diff report |

---

## Phase Dependency Graph

```
Phase 0 (Baseline)
    |
Phase 1 (Static Analysis)
    |
    +---> Phase 2 (Back-End Unit) ---> Phase 3 (Back-End Integration)
    |                                        |
    +---> Phase 4 (Front-End Unit) ----> Phase 5 (Front-End E2E)
                                             |
                                   Phase 6 (Cross-Validation)
                                             |
                                   Phase 7 (Non-Functional)
```

---

## ABAP Source File Index

| File | Description | Key Lines |
|------|-------------|-----------|
| `ZFM_GET_MAT_SO_DETAILS..FUGR.txt` | Function Module -- Sales Order details by material | L1-119: full FM body |
| `LZFG_MAT_SOTOP.txt` | Function Group top include -- data types & globals | L1-40: ty_out, gt_items, BAPIRET2, add_message macro |
| `LZFG_MAT_SOUXX.txt` | Function Group include registry | L5-6: includes ZFM_GET_MAT_SO_DETAILS |
| `ZMAT_REPORT.PROG` | Material Stock Report with ALV | L1-250: full report |

---

## Revision History

| Date | Author | Description |
|------|--------|-------------|
| 2026-03-23 | Devin (automated) | Initial migration plan with 70 test cases |
