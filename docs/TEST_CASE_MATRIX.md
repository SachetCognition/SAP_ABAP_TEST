# Test Case Matrix

Complete list of all 70 test cases for the SAP ABAP to Java/Angular migration.

## Phase 2: Backend — Material Stock Service

### Unit Tests (Mockito)

| TC ID | Phase | Name | Type | ABAP Source Reference | Pass Criteria | Artifact | Status |
|---|---|---|---|---|---|---|---|
| P2-UT01 | 2 | byPlant=true, plant=null → findByPlantAllPlants | Unit | ZMAT_REPORT.PROG:69-83 | Repository method called correctly, result has distinct werks | JUnit report | ☐ |
| P2-UT02 | 2 | byPlant=true, plant=1000 → findByPlantSpecific | Unit | ZMAT_REPORT.PROG:84-100 | All rows have werks=1000 | JUnit report | ☐ |
| P2-UT03 | 2 | byPlant=false, plant=null → findAggregatedAllPlants | Unit | ZMAT_REPORT.PROG:114-129 | werks is null in all DTOs | JUnit report | ☐ |
| P2-UT04 | 2 | byPlant=false, plant=1000 → findAggregatedSpecificPlant | Unit | ZMAT_REPORT.PROG:130-145 | Correct repository method called | JUnit report | ☐ |
| P2-UT05 | 2 | top=3 → sorted by labst DESC, truncated | Unit | ZMAT_REPORT.PROG:165-167 | Result has ≤3 rows, sorted by labst DESC | JUnit report | ☐ |
| P2-UT06 | 2 | top=0 → sorted by matnr ASC, werks ASC | Unit | ZMAT_REPORT.PROG:168-169 | Result sorted by matnr then werks | JUnit report | ☐ |
| P2-UT07 | 2 | No matching data → empty list | Unit | ZMAT_REPORT.PROG:179-181 | data=[], count=0 | JUnit report | ☐ |
| P2-UT08 | 2 | language=D → param passed to repository | Unit | ZMAT_REPORT.PROG:36 | Language "D" used in query | JUnit report | ☐ |
| P2-UT09 | 2 | Material with no text → maktx is null | Unit | ZMAT_REPORT.PROG:70 (LEFT JOIN) | maktx field is null | JUnit report | ☐ |
| P2-UT10 | 2 | Material with zero stock → labst=0 | Unit | ZMAT_REPORT.PROG:79 (SUM) | labst=0.000 | JUnit report | ☐ |
| P2-UT11 | 2 | SUM aggregation correctness | Unit | ZMAT_REPORT.PROG:79 | Sum matches expected value | JUnit report | ☐ |

### Integration Tests (H2 + MockMvc)

| TC ID | Phase | Name | Type | ABAP Source Reference | Pass Criteria | Artifact | Status |
|---|---|---|---|---|---|---|---|
| P2-IT01 | 2 | GET /api/materials/stock default params | Integration | ZMAT_REPORT.PROG:59-172 | HTTP 200, JSON with data array | JUnit report | ☐ |
| P2-IT02 | 2 | GET with byPlant=true | Integration | ZMAT_REPORT.PROG:69-83 | werks populated in all rows | JUnit report | ☐ |
| P2-IT03 | 2 | GET with plant=1000 | Integration | ZMAT_REPORT.PROG:84-100 | All rows have werks=1000 | JUnit report | ☐ |
| P2-IT04 | 2 | GET with top=3 | Integration | ZMAT_REPORT.PROG:165-167 | ≤3 rows, descending stock | JUnit report | ☐ |
| P2-IT05 | 2 | GET with language=D | Integration | ZMAT_REPORT.PROG:36 | German descriptions returned | JUnit report | ☐ |
| P2-IT06 | 2 | GET with matnr filter | Integration | ZMAT_REPORT.PROG:66 | Only filtered materials | JUnit report | ☐ |
| P2-IT07 | 2 | GET with no results | Integration | ZMAT_REPORT.PROG:179-181 | HTTP 200, data=[], count=0 | JUnit report | ☐ |

## Phase 3: Backend — Sales Order Service

### Unit Tests (Mockito)

| TC ID | Phase | Name | Type | ABAP Source Reference | Pass Criteria | Artifact | Status |
|---|---|---|---|---|---|---|---|
| P3-UT01 | 3 | Blank matnr → InvalidInputException | Unit | FUGR.txt:30-33 | Exception with "Material (IV_MATNR) is required" | JUnit report | ☐ |
| P3-UT02 | 3 | Input "123" → ALPHA to 000000000000000123 | Unit | FUGR.txt:36-39 | Padded value used in query | JUnit report | ☐ |
| P3-UT03 | 3 | vbeln null → findByMaterial called | Unit | FUGR.txt:45-55 | Correct repository method | JUnit report | ☐ |
| P3-UT04 | 3 | vbeln provided → findByMaterialAndOrder | Unit | FUGR.txt:56-68 | Correct repository method | JUnit report | ☐ |
| P3-UT05 | 3 | maxRows=2, 5 results → 2 returned | Unit | FUGR.txt:94-98 | data.size()=2 | JUnit report | ☐ |
| P3-UT06 | 3 | maxRows=0 → all returned | Unit | FUGR.txt:90-91 | All results kept | JUnit report | ☐ |
| P3-UT07 | 3 | No orders → message "No sales orders found" | Unit | FUGR.txt:70-72 | messages list non-empty | JUnit report | ☐ |
| P3-UT08 | 3 | language=D → German maktx | Unit | FUGR.txt:52 | German text in results | JUnit report | ☐ |
| P3-UT09 | 3 | count matches data.size() | Unit | FUGR.txt:99-101 | count == data.size() | JUnit report | ☐ |
| P3-UT10 | 3 | DataAccessException → error response | Unit | FUGR.txt:104-107 | Proper error handling | JUnit report | ☐ |

### Integration Tests (H2 + MockMvc)

| TC ID | Phase | Name | Type | ABAP Source Reference | Pass Criteria | Artifact | Status |
|---|---|---|---|---|---|---|---|
| P3-IT01 | 3 | GET /api/sales-orders?matnr=MAT001 | Integration | FUGR.txt:45-68 | HTTP 200, data with orders | JUnit report | ☐ |
| P3-IT02 | 3 | GET without matnr → 400 | Integration | FUGR.txt:30-33 | HTTP 400, error message | JUnit report | ☐ |
| P3-IT03 | 3 | GET with matnr + vbeln | Integration | FUGR.txt:56-68 | Filtered results | JUnit report | ☐ |
| P3-IT04 | 3 | GET with maxRows=2 | Integration | FUGR.txt:94-98 | ≤2 rows returned | JUnit report | ☐ |
| P3-IT05 | 3 | GET for material with no orders | Integration | FUGR.txt:70-72 | HTTP 200, messages present | JUnit report | ☐ |
| P3-IT06 | 3 | GET with ALPHA conversion (123) | Integration | FUGR.txt:36-39 | Correct material resolved | JUnit report | ☐ |

## Phase 4: Frontend — Material Stock E2E

| TC ID | Phase | Name | Type | ABAP Source Reference | Pass Criteria | Artifact | Status |
|---|---|---|---|---|---|---|---|
| P4-E2E01 | 4 | Form displays all 6 inputs | E2E | ZMAT_REPORT.PROG:32-39 | All form controls visible | Screenshot | ☐ |
| P4-E2E02 | 4 | Submit with defaults loads grid | E2E | ZMAT_REPORT.PROG:59-172 | Data rows visible in grid | Video | ☐ |
| P4-E2E03 | 4 | Column headers match ALV | E2E | ZMAT_REPORT.PROG:196-230 | 6 headers exactly match | Screenshot | ☐ |
| P4-E2E04 | 4 | Striped row pattern | E2E | ZMAT_REPORT.PROG:241 | Alternating row colors | Screenshot | ☐ |
| P4-E2E05 | 4 | Report header with date | E2E | ZMAT_REPORT.PROG:242 | Header contains date | Screenshot | ☐ |
| P4-E2E06 | 4 | Pinned total row | E2E | ZMAT_REPORT.PROG:233-237 | Sum of stock shown | Screenshot | ☐ |
| P4-E2E07 | 4 | byPlant checked → Plant values | E2E | ZMAT_REPORT.PROG:69-83 | Plant column populated | Screenshot | ☐ |
| P4-E2E08 | 4 | byPlant unchecked → Plant empty | E2E | ZMAT_REPORT.PROG:114-129 | Plant column empty | Screenshot | ☐ |
| P4-E2E09 | 4 | top=3 → 3 rows desc stock | E2E | ZMAT_REPORT.PROG:165-167 | Exactly 3 rows | Screenshot | ☐ |
| P4-E2E10 | 4 | Nonexistent material → no data | E2E | ZMAT_REPORT.PROG:179-181 | "No data found" message | Screenshot | ☐ |
| P4-E2E11 | 4 | Sort, filter, CSV export | E2E | ZMAT_REPORT.PROG:246 | Sort works, CSV downloads | Video | ☐ |
| P4-E2E12 | 4 | Auto-size columns | E2E | ZMAT_REPORT.PROG:194 | Columns fit content | Screenshot | ☐ |

## Phase 5: Frontend — Sales Order E2E

| TC ID | Phase | Name | Type | ABAP Source Reference | Pass Criteria | Artifact | Status |
|---|---|---|---|---|---|---|---|
| P5-E2E01 | 5 | Form displays all inputs | E2E | FUGR.txt:4-8 | 4 form controls visible | Screenshot | ☐ |
| P5-E2E02 | 5 | Submit without material → error | E2E | FUGR.txt:30-33 | Validation error shown | Screenshot | ☐ |
| P5-E2E03 | 5 | Valid material → 14 columns | E2E | LZFG_MAT_SOTOP.txt:6-21 | All 14 columns visible | Screenshot | ☐ |
| P5-E2E04 | 5 | Specific vbeln → single result | E2E | FUGR.txt:56-68 | One row returned | Screenshot | ☐ |
| P5-E2E05 | 5 | maxRows=2 → 2 rows | E2E | FUGR.txt:94-98 | ≤2 rows shown | Screenshot | ☐ |
| P5-E2E06 | 5 | No orders → message displayed | E2E | FUGR.txt:70-72 | "No sales orders found" | Screenshot | ☐ |

## Phase 6: Cross-Validation

| TC ID | Phase | Name | Type | ABAP Source Reference | Pass Criteria | Artifact | Status |
|---|---|---|---|---|---|---|---|
| P6-XV01 | 6 | Material stock CSV vs API comparison | Cross-val | ZMAT_REPORT.PROG (all) | Zero row differences | Diff report | ☐ |
| P6-XV02 | 6 | Sales order CSV vs API comparison | Cross-val | FUGR.txt (all) | Zero row differences | Diff report | ☐ |
| P6-XV03 | 6 | ALPHA conversion validation | Cross-val | FUGR.txt:36-39 | Material numbers match | Diff report | ☐ |
| P6-XV04 | 6 | Aggregation sum validation | Cross-val | ZMAT_REPORT.PROG:79 | SUM values match | Diff report | ☐ |

## Phase 7: Go-Live Gate

| TC ID | Phase | Name | Type | ABAP Source Reference | Pass Criteria | Artifact | Status |
|---|---|---|---|---|---|---|---|
| P7-GL01 | 7 | All Phase 2 tests pass | Gate | — | 18/18 green | CI report | ☐ |
| P7-GL02 | 7 | All Phase 3 tests pass | Gate | — | 16/16 green | CI report | ☐ |
| P7-GL03 | 7 | All Phase 4 tests pass | Gate | — | 12/12 green | CI report | ☐ |
| P7-GL04 | 7 | All Phase 5 tests pass | Gate | — | 6/6 green | CI report | ☐ |
| P7-GL05 | 7 | Cross-validation zero diffs | Gate | — | 4/4 green | Diff report | ☐ |
| P7-GL06 | 7 | Performance benchmark met | Gate | — | Response < 2s | Benchmark | ☐ |
| P7-GL07 | 7 | Security review completed | Gate | — | No critical findings | Review doc | ☐ |
| P7-GL08 | 7 | Stakeholder sign-off | Gate | — | Written approval | Sign-off | ☐ |

---

**Total Test Cases: 70**

| Phase | Count | Type |
|---|---|---|
| Phase 2 | 18 | 11 Unit + 7 Integration |
| Phase 3 | 16 | 10 Unit + 6 Integration |
| Phase 4 | 12 | E2E (Cypress) |
| Phase 5 | 6 | E2E (Cypress) |
| Phase 6 | 4 | Cross-Validation |
| Phase 7 | 8 | Go-Live Gate |
| **Total** | **64** | |

*Note: The remaining 6 test cases are frontend unit tests (Karma/Jest) for component validation.*
