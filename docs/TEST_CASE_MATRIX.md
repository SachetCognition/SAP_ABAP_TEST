# Test Case Matrix

> **Total Test Cases:** 70 | **Phases:** 0--7
> **Repository:** `SachetCognition/SAP_ABAP_TEST`

## Status Legend

| Symbol | Meaning |
|--------|---------|
| -- | Not started |
| IN PROGRESS | Currently being executed |
| PASS | Test passed |
| FAIL | Test failed |
| BLOCKED | Blocked by dependency |
| N/A | Not applicable in current iteration |

---

| TC ID | Phase | Test Case Name | Type | ABAP Source Reference | Pass Criteria | Artifact Type | Status |
|-------|-------|---------------|------|----------------------|---------------|---------------|--------|
| TC-001 | 0 | Capture FM default output (material only) | Manual | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:45-55` | FM returns rows for a known material; CSV exported matches SAP GUI display | CSV export + screenshot | -- |
| TC-002 | 0 | Capture FM output with order filter | Manual | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:56-68` | FM returns rows filtered by VBELN; CSV matches GUI | CSV export + screenshot | -- |
| TC-003 | 0 | Capture FM output with MAXROWS limit | Manual | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:93-99` | Row count in export equals IV_MAXROWS | CSV export | -- |
| TC-004 | 0 | Capture FM output with language parameter | Manual | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:7,52,63` | MAKTX column reflects requested language | CSV export | -- |
| TC-005 | 0 | Capture FM error for missing material | Manual | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:30-33` | BAPIRET2 contains type E, message 001 | Screenshot | -- |
| TC-006 | 0 | Capture Report -- aggregated all plants | Manual | `ZMAT_REPORT.PROG:113-161` | ALV grid shows blank WERKS, LABST summed across plants | Video + screenshot | -- |
| TC-007 | 0 | Capture Report -- by-plant detail (all) | Manual | `ZMAT_REPORT.PROG:65-112` | ALV grid shows WERKS per row | Video + screenshot | -- |
| TC-008 | 0 | Capture Report -- specific plant | Manual | `ZMAT_REPORT.PROG:84-101` | Only rows for p_werks appear | Screenshot | -- |
| TC-009 | 0 | Capture Report -- Top-N restriction | Manual | `ZMAT_REPORT.PROG:164-170` | ALV shows exactly p_top rows, sorted DESC by LABST | Screenshot + CSV | -- |
| TC-010 | 0 | Capture Report -- no data found | Manual | `ZMAT_REPORT.PROG:179-182` | Message "No data found for given selection" displayed | Screenshot | -- |
| TC-011 | 2 | Material required validation | Unit | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:30-33` | 400 Bad Request with error body when matnr is null/empty | JUnit report | -- |
| TC-012 | 2 | ALPHA conversion -- short material number | Unit | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:36-39` | Input "123" padded to "000000000000000123" before DB query | JUnit report | -- |
| TC-013 | 2 | ALPHA conversion -- already padded material | Unit | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:36-39` | Input "000000000000000123" remains unchanged | JUnit report | -- |
| TC-014 | 2 | ALPHA conversion -- alphanumeric material | Unit | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:36-39` | Non-numeric material (e.g., "ABC-100") not zero-padded | JUnit report | -- |
| TC-015 | 2 | Query all orders for material (no VBELN filter) | Unit | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:45-55` | Repository called without VBELN predicate; returns all matching rows | JUnit report | -- |
| TC-016 | 2 | Query filtered by VBELN | Unit | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:56-68` | Repository called with VBELN predicate; only matching order returned | JUnit report | -- |
| TC-017 | 2 | Language filter defaults to 'E' | Unit | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:7,52,63` | When spras param omitted, MAKT join uses 'E' | JUnit report | -- |
| TC-018 | 2 | Language filter explicit value 'D' | Unit | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:7,52,63` | MAKT description returned in German | JUnit report | -- |
| TC-019 | 2 | No data returns info message | Unit | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:70-72` | Response includes BAPIRET2-equivalent message type S, number 002 | JUnit report | -- |
| TC-020 | 2 | Field mapping -- all 14 fields present | Unit | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:74-91`, `LZFG_MAT_SOTOP.txt:6-21` | JSON response contains all 14 ty_out fields | JUnit report | -- |
| TC-021 | 2 | MAXROWS = 5 limits output | Unit | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:93-99` | Exactly 5 items returned when DB has > 5 | JUnit report | -- |
| TC-022 | 2 | MAXROWS = 0 returns all | Unit | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:93-99` | No truncation applied | JUnit report | -- |
| TC-023 | 2 | MAXROWS off-by-one boundary | Unit | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:93-99` | MAXROWS = N returns exactly N rows (verify ABAP lv_max+1 logic) | JUnit report | -- |
| TC-024 | 2 | EV_COUNT matches item count | Unit | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:101-102,112` | `count` field in response equals array length | JUnit report | -- |
| TC-025 | 2 | SQL exception maps to 500 | Unit | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:104-107` | Simulated DB error returns 500 with error body | JUnit report | -- |
| TC-026 | 2 | BAPIRET2 message structure | Unit | `LZFG_MAT_SOTOP.txt:32-39` | Return messages contain type, id, number, message fields | JUnit report | -- |
| TC-027 | 3 | GET /api/materials/{matnr}/sales-orders -- happy path | Integration | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:45-55,74-91` | 200 OK; JSON matches baseline CSV (TC-001) | JUnit report + Postman | -- |
| TC-028 | 3 | GET with ?vbeln= filter | Integration | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:56-68` | Only rows for that order returned; matches TC-002 baseline | JUnit report + Postman | -- |
| TC-029 | 3 | GET with ?maxrows=3 | Integration | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:93-99` | Exactly 3 rows; matches TC-003 baseline | JUnit report + Postman | -- |
| TC-030 | 3 | GET with ?spras=D | Integration | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:7,52,63` | Descriptions in German; matches TC-004 baseline | Postman | -- |
| TC-031 | 3 | GET missing material | Integration | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:30-33` | 400 Bad Request | Postman | -- |
| TC-032 | 3 | GET non-existent material | Integration | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:70-72` | 200 OK with empty items + info message | Postman | -- |
| TC-033 | 3 | VBAK-VBAP INNER JOIN correctness | Integration | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:47-51` | No orphan items (every VBAP row has matching VBAK) | JUnit report | -- |
| TC-034 | 3 | MAKT LEFT JOIN -- material with no description | Integration | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:52,63` | maktx is null/empty, remaining fields populated | JUnit report | -- |
| TC-035 | 3 | MARA LEFT JOIN -- material with no master | Integration | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:53,64` | meins is null/empty, remaining fields populated | JUnit report | -- |
| TC-036 | 3 | Seed data row count matches SAP | Integration | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:101-102,112` | ev_count from SAP == count from Java API for same input | JUnit report | -- |
| TC-037 | 4 | Selection screen renders all inputs | Unit | `ZMAT_REPORT.PROG:32-39` | Material range, Material type range, Plant, Language, By-plant checkbox, Top-N input all visible | Screenshot | -- |
| TC-038 | 4 | Plant field is required | Unit | `ZMAT_REPORT.PROG:35` | Form validation blocks submit when Plant is empty (mirrors OBLIGATORY) | Screenshot | -- |
| TC-039 | 4 | By-plant checkbox toggles mode | Unit | `ZMAT_REPORT.PROG:37,65,113` | Checkbox state propagates to API call parameter | Screenshot | -- |
| TC-040 | 4 | Top-N input accepts 0 and positive integers | Unit | `ZMAT_REPORT.PROG:38` | Input validation allows 0 (no limit) and rejects negative/non-numeric | Screenshot | -- |
| TC-041 | 4 | Data table renders column headers | Unit | `ZMAT_REPORT.PROG:192-230` | Columns: Material, Material Desc, Material Type, Base UoM, Plant, Unrestricted Stock | Screenshot | -- |
| TC-042 | 4 | Data table striped pattern | Unit | `ZMAT_REPORT.PROG:241` | Alternating row colours applied | Screenshot | -- |
| TC-043 | 4 | Data table dynamic header with date/time | Unit | `ZMAT_REPORT.PROG:242` | Header text includes current date and time | Screenshot | -- |
| TC-044 | 4 | LABST aggregation footer row | Unit | `ZMAT_REPORT.PROG:233-237` | Footer/total row shows SUM of LABST column | Screenshot | -- |
| TC-045 | 4 | No data message display | Unit | `ZMAT_REPORT.PROG:179-182` | "No data found for given selection" shown when API returns empty | Screenshot | -- |
| TC-046 | 4 | Column optimize (auto-width) | Unit | `ZMAT_REPORT.PROG:194` | Columns auto-sized to content (no truncation of MAKTX) | Screenshot | -- |
| TC-047 | 5 | E2E: Aggregated all-plants report | E2E | `ZMAT_REPORT.PROG:113-161` | User leaves Plant blank, unchecks by-plant; table shows blank WERKS, LABST summed | Video + screenshot | -- |
| TC-048 | 5 | E2E: By-plant detail (all plants) | E2E | `ZMAT_REPORT.PROG:65-112` | User checks by-plant, leaves Plant blank; table shows WERKS per row | Video + screenshot | -- |
| TC-049 | 5 | E2E: By-plant detail (specific plant) | E2E | `ZMAT_REPORT.PROG:84-101` | User checks by-plant, enters Plant "1000"; only plant 1000 rows | Video + screenshot | -- |
| TC-050 | 5 | E2E: Aggregated specific plant | E2E | `ZMAT_REPORT.PROG:130-146` | User enters Plant "1000", unchecks by-plant; LABST summed for plant 1000 | Video + screenshot | -- |
| TC-051 | 5 | E2E: Top-N = 5 | E2E | `ZMAT_REPORT.PROG:165-167` | Only 5 rows shown, sorted LABST DESC | Video + screenshot | -- |
| TC-052 | 5 | E2E: Top-N = 0 (no limit) | E2E | `ZMAT_REPORT.PROG:168-169` | All rows shown, sorted MATNR ASC | Video + screenshot | -- |
| TC-053 | 5 | E2E: Material range filter | E2E | `ZMAT_REPORT.PROG:33` | Only materials in range appear | Video | -- |
| TC-054 | 5 | E2E: Material type filter | E2E | `ZMAT_REPORT.PROG:34` | Only materials of specified MTART appear | Video | -- |
| TC-055 | 5 | E2E: No data scenario | E2E | `ZMAT_REPORT.PROG:179-182` | Empty state message displayed | Screenshot | -- |
| TC-056 | 5 | E2E: Export to Excel/CSV | E2E | `ZMAT_REPORT.PROG:245-246` | ALV "Export" equivalent works; downloaded file matches table | Screenshot + file | -- |
| TC-057 | 6 | Diff: FM default output vs. GET /api/materials/{matnr}/sales-orders | Automated | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:45-55,74-91` | 0 row differences after field-name normalization | HTML diff report | -- |
| TC-058 | 6 | Diff: FM + VBELN filter vs. GET ?vbeln= | Automated | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:56-68` | 0 row differences | HTML diff report | -- |
| TC-059 | 6 | Diff: FM + MAXROWS vs. GET ?maxrows= | Automated | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:93-99` | Row count identical; row content matches | HTML diff report | -- |
| TC-060 | 6 | Diff: FM + language DE vs. GET ?spras=D | Automated | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:7,52,63` | MAKTX values identical | HTML diff report | -- |
| TC-061 | 6 | Diff: Report aggregated all plants vs. GET /api/materials/stock | Automated | `ZMAT_REPORT.PROG:113-161` | LABST sums identical per MATNR | HTML diff report | -- |
| TC-062 | 6 | Diff: Report by-plant vs. GET /api/materials/stock?byPlant=true | Automated | `ZMAT_REPORT.PROG:65-112` | LABST values identical per MATNR+WERKS | HTML diff report | -- |
| TC-063 | 6 | Diff: Report specific plant vs. GET /api/materials/stock?plant=1000 | Automated | `ZMAT_REPORT.PROG:84-101,130-146` | Values identical for plant 1000 | HTML diff report | -- |
| TC-064 | 6 | Diff: Report Top-N=10 vs. GET /api/materials/stock?topN=10 | Automated | `ZMAT_REPORT.PROG:165-167` | Same 10 rows in same order | HTML diff report | -- |
| TC-065 | 6 | ALPHA conversion alignment | Automated | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:36-39` | Material numbers match after normalization (leading-zero handling) | HTML diff report | -- |
| TC-066 | 6 | Side-by-side visual: SAP ALV vs. Web table | Manual | `ZMAT_REPORT.PROG:192-246` | Visual comparison shows equivalent layout and data | Side-by-side image | -- |
| TC-067 | 7 | Performance: API < 2s for 1000 rows | Automated | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:93-99` | p95 response time under 2 seconds with 1000-row result set | JMeter/k6 report | -- |
| TC-068 | 7 | Performance: UI renders 1000 rows < 3s | Automated | `ZMAT_REPORT.PROG:184-248` | Time-to-interactive under 3 seconds | Lighthouse report | -- |
| TC-069 | 7 | Security: SQL injection on matnr parameter | Automated | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:30-33,36-39` | Malicious input rejected; no SQL error exposed | Postman export | -- |
| TC-070 | 7 | Regression: Re-run all Phase 6 diffs after code freeze | Automated | All rules | 0 differences on all diff reports | Combined HTML diff | -- |

---

## Summary by Phase

| Phase | Name | Test Cases | Types |
|-------|------|-----------|-------|
| 0 | Baseline Capture | TC-001 -- TC-010 (10) | Manual |
| 1 | Static Analysis & Rule Extraction | (documented in MIGRATION_PLAN.md) | N/A |
| 2 | Back-End Unit Tests | TC-011 -- TC-026 (16) | Unit |
| 3 | Back-End Integration Tests | TC-027 -- TC-036 (10) | Integration |
| 4 | Front-End Unit/Component Tests | TC-037 -- TC-046 (10) | Unit |
| 5 | Front-End E2E Tests | TC-047 -- TC-056 (10) | E2E |
| 6 | Cross-Validation | TC-057 -- TC-066 (10) | Automated + Manual |
| 7 | Non-Functional & Regression | TC-067 -- TC-070 (4) | Automated |
| **Total** | | **70** | |

## Summary by Type

| Type | Count |
|------|-------|
| Manual | 11 |
| Unit | 26 |
| Integration | 10 |
| E2E | 10 |
| Automated | 13 |
| **Total** | **70** |
