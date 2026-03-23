# Cross-Validation Script -- Phase 6

> Template and pseudocode for the row-by-row comparison between SAP baseline CSV exports and Java REST API responses.

---

## Table of Contents

1. [Purpose](#purpose)
2. [Field Name Mapping (SAP to Java)](#field-name-mapping-sap-to-java)
3. [ALPHA Conversion Handling](#alpha-conversion-handling)
4. [Node.js Template](#nodejs-template)
5. [Python Template](#python-template)
6. [HTML Report Format](#html-report-format)
7. [Usage Examples](#usage-examples)

---

## Purpose

Phase 6 test cases (TC-057 through TC-066) require an automated comparison between:

- **SAP Baseline:** CSV files exported from SAP GUI during Phase 0 (stored in `test-artifacts/phase0-baseline/csv-exports/`)
- **Java API Response:** JSON returned by the migrated Spring Boot REST API

The script must:
1. Read a SAP baseline CSV file
2. Call the Java REST API with equivalent parameters
3. Normalise field names from SAP naming to Java naming
4. Handle ALPHA conversion differences (leading zeros on material numbers)
5. Compare row-by-row
6. Output an HTML report with pass/fail per row and highlighted differences

---

## Field Name Mapping (SAP to Java)

### Function Module `ZFM_GET_MAT_SO_DETAILS` (`ty_out`)

Source: `LZFG_MAT_SOTOP.txt:6-21`

| SAP Field (CSV Header) | Java Field (JSON Key) | Data Type | Notes |
|------------------------|----------------------|-----------|-------|
| VBELN | salesOrderNumber | String | Sales Document Number |
| POSNR | itemNumber | String | Item Number (may have leading zeros) |
| AUART | salesDocumentType | String | e.g., "OR", "ZOR" |
| MATNR | materialNumber | String | **ALPHA conversion** -- see below |
| MAKTX | materialDescription | String | Language-dependent |
| MEINS | baseUnitOfMeasure | String | e.g., "EA", "KG" |
| KWMENG | orderQuantity | Decimal | Cumulative order quantity |
| VRKME | salesUnit | String | |
| PSTYV | itemCategory | String | e.g., "TAN", "TANN" |
| VKORG | salesOrganization | String | |
| VTWEG | distributionChannel | String | |
| SPART | division | String | |
| ERDAT | createdDate | String/Date | Format: YYYYMMDD (SAP) vs. ISO 8601 (Java) |
| ERNAM | createdBy | String | |

### Report `ZMAT_REPORT` (`ty_out`)

Source: `ZMAT_REPORT.PROG:14-21`

| SAP Field (CSV Header) | Java Field (JSON Key) | Data Type | Notes |
|------------------------|----------------------|-----------|-------|
| MATNR | materialNumber | String | **ALPHA conversion** |
| MAKTX | materialDescription | String | Language-dependent |
| MTART | materialType | String | e.g., "FERT", "ROH" |
| MEINS | baseUnitOfMeasure | String | |
| WERKS | plant | String | Blank when aggregated across all plants |
| LABST | unrestrictedStock | Decimal | SUM of storage location stock |

---

## ALPHA Conversion Handling

Source: `ZFM_GET_MAT_SO_DETAILS..FUGR.txt:36-39`

SAP stores material numbers with leading zeros (e.g., `000000000000000123`). The Java API may accept/return them without leading zeros (`123`) or in external format.

### Normalisation Rules

```
1. Strip all leading zeros from both SAP and Java values
2. If the value is purely numeric, compare the stripped versions
3. If the value is alphanumeric (e.g., "ABC-100"), compare as-is (no padding)
4. For POSNR (item number), strip leading zeros from both sides
```

### Implementation

```javascript
function normalizeAlpha(value) {
  if (!value) return '';
  const stripped = String(value).replace(/^0+/, '');
  return stripped || '0'; // preserve a single '0' if all zeros
}
```

```python
def normalize_alpha(value: str) -> str:
    if not value:
        return ''
    stripped = str(value).lstrip('0')
    return stripped or '0'  # preserve a single '0' if all zeros
```

---

## Node.js Template

```javascript
#!/usr/bin/env node
/**
 * cross-validate.js
 *
 * Compares SAP baseline CSV with Java REST API response.
 * Outputs an HTML diff report.
 *
 * Usage:
 *   node cross-validate.js \
 *     --sap-csv <path-to-csv> \
 *     --api-url <java-api-endpoint> \
 *     --output  <output-html-path> \
 *     [--field-map fm|report]
 */

const fs = require('fs');
const path = require('path');
const https = require('http'); // use 'https' for production
const { parse } = require('csv-parse/sync'); // npm install csv-parse

// ---- Field mappings --------------------------------------------------------

const FM_FIELD_MAP = {
  VBELN:  'salesOrderNumber',
  POSNR:  'itemNumber',
  AUART:  'salesDocumentType',
  MATNR:  'materialNumber',
  MAKTX:  'materialDescription',
  MEINS:  'baseUnitOfMeasure',
  KWMENG: 'orderQuantity',
  VRKME:  'salesUnit',
  PSTYV:  'itemCategory',
  VKORG:  'salesOrganization',
  VTWEG:  'distributionChannel',
  SPART:  'division',
  ERDAT:  'createdDate',
  ERNAM:  'createdBy',
};

const REPORT_FIELD_MAP = {
  MATNR: 'materialNumber',
  MAKTX: 'materialDescription',
  MTART: 'materialType',
  MEINS: 'baseUnitOfMeasure',
  WERKS: 'plant',
  LABST: 'unrestrictedStock',
};

const ALPHA_FIELDS = ['MATNR', 'POSNR', 'VBELN'];

// ---- Helpers ---------------------------------------------------------------

function normalizeAlpha(value) {
  if (!value) return '';
  const stripped = String(value).replace(/^0+/, '');
  return stripped || '0';
}

function normalizeDate(sapDate) {
  // SAP: "20260115" -> ISO: "2026-01-15"
  if (!sapDate || sapDate.length !== 8) return sapDate;
  return `${sapDate.slice(0, 4)}-${sapDate.slice(4, 6)}-${sapDate.slice(6, 8)}`;
}

function normalizeValue(field, value) {
  if (ALPHA_FIELDS.includes(field)) return normalizeAlpha(value);
  if (field === 'ERDAT') return normalizeDate(value);
  if (field === 'KWMENG' || field === 'LABST') return parseFloat(value || 0).toString();
  return String(value || '').trim();
}

// ---- Main ------------------------------------------------------------------

async function main() {
  const args = parseArgs(process.argv.slice(2));

  // 1. Read SAP CSV
  const csvContent = fs.readFileSync(args.sapCsv, 'utf-8');
  const sapRows = parse(csvContent, { columns: true, skip_empty_lines: true });
  console.log(`SAP CSV: ${sapRows.length} rows`);

  // 2. Call Java API
  const apiResponse = await fetch(args.apiUrl);
  const apiJson = await apiResponse.json();
  const javaRows = apiJson.items || apiJson.data || apiJson;
  console.log(`Java API: ${Array.isArray(javaRows) ? javaRows.length : 0} rows`);

  // 3. Select field map
  const fieldMap = args.fieldMap === 'report' ? REPORT_FIELD_MAP : FM_FIELD_MAP;

  // 4. Compare row-by-row
  const results = [];
  const maxRows = Math.max(sapRows.length, javaRows.length);

  for (let i = 0; i < maxRows; i++) {
    const sapRow = sapRows[i] || {};
    const javaRow = javaRows[i] || {};
    const rowResult = { index: i, fields: [], pass: true };

    for (const [sapField, javaField] of Object.entries(fieldMap)) {
      const sapVal = normalizeValue(sapField, sapRow[sapField]);
      const javaVal = normalizeValue(sapField, javaRow[javaField]);
      const match = sapVal === javaVal;
      if (!match) rowResult.pass = false;
      rowResult.fields.push({ sapField, javaField, sapVal, javaVal, match });
    }

    results.push(rowResult);
  }

  // 5. Generate HTML report
  const html = generateHtml(results, args);
  fs.writeFileSync(args.output, html, 'utf-8');

  const passCount = results.filter(r => r.pass).length;
  const failCount = results.filter(r => !r.pass).length;
  console.log(`Result: ${passCount} pass, ${failCount} fail -- report saved to ${args.output}`);

  process.exit(failCount > 0 ? 1 : 0);
}

// ---- HTML Report -----------------------------------------------------------

function generateHtml(results, args) {
  const passCount = results.filter(r => r.pass).length;
  const failCount = results.filter(r => !r.pass).length;
  const totalCount = results.length;

  let rows = '';
  for (const r of results) {
    for (const f of r.fields) {
      const bgColor = f.match ? '#e6ffe6' : '#ffe6e6';
      const status = f.match ? 'MATCH' : 'DIFF';
      rows += `
        <tr style="background:${bgColor}">
          <td>${r.index}</td>
          <td>${f.sapField}</td>
          <td>${f.javaField}</td>
          <td><code>${escapeHtml(f.sapVal)}</code></td>
          <td><code>${escapeHtml(f.javaVal)}</code></td>
          <td><strong>${status}</strong></td>
        </tr>`;
    }
  }

  return `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Cross-Validation Diff Report</title>
  <style>
    body { font-family: Arial, sans-serif; margin: 20px; }
    h1 { color: #333; }
    .summary { margin: 20px 0; padding: 15px; border: 1px solid #ccc; border-radius: 4px; }
    .pass { color: green; font-weight: bold; }
    .fail { color: red; font-weight: bold; }
    table { border-collapse: collapse; width: 100%; margin-top: 20px; }
    th, td { border: 1px solid #ccc; padding: 8px; text-align: left; font-size: 13px; }
    th { background: #f5f5f5; }
    code { background: #f0f0f0; padding: 2px 4px; border-radius: 2px; }
  </style>
</head>
<body>
  <h1>Cross-Validation Diff Report</h1>
  <div class="summary">
    <p><strong>SAP CSV:</strong> ${escapeHtml(args.sapCsv)}</p>
    <p><strong>Java API:</strong> ${escapeHtml(args.apiUrl)}</p>
    <p><strong>Total Rows:</strong> ${totalCount}</p>
    <p class="pass">Matching Rows: ${passCount}</p>
    <p class="fail">Mismatched Rows: ${failCount}</p>
    <p><strong>Result:</strong> ${failCount === 0 ? '<span class="pass">PASS</span>' : '<span class="fail">FAIL</span>'}</p>
  </div>
  <table>
    <thead>
      <tr>
        <th>Row</th>
        <th>SAP Field</th>
        <th>Java Field</th>
        <th>SAP Value</th>
        <th>Java Value</th>
        <th>Status</th>
      </tr>
    </thead>
    <tbody>${rows}
    </tbody>
  </table>
</body>
</html>`;
}

function escapeHtml(str) {
  return String(str || '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

// ---- Arg Parsing -----------------------------------------------------------

function parseArgs(argv) {
  const args = { fieldMap: 'fm' };
  for (let i = 0; i < argv.length; i++) {
    if (argv[i] === '--sap-csv')   args.sapCsv  = argv[++i];
    if (argv[i] === '--api-url')   args.apiUrl  = argv[++i];
    if (argv[i] === '--output')    args.output  = argv[++i];
    if (argv[i] === '--field-map') args.fieldMap = argv[++i];
  }
  if (!args.sapCsv || !args.apiUrl || !args.output) {
    console.error('Usage: node cross-validate.js --sap-csv <csv> --api-url <url> --output <html> [--field-map fm|report]');
    process.exit(1);
  }
  return args;
}

main().catch(err => { console.error(err); process.exit(1); });
```

---

## Python Template

```python
#!/usr/bin/env python3
"""
cross_validate.py

Compares SAP baseline CSV with Java REST API response.
Outputs an HTML diff report.

Usage:
    python cross_validate.py \
        --sap-csv <path-to-csv> \
        --api-url <java-api-endpoint> \
        --output  <output-html-path> \
        [--field-map fm|report]
"""

import argparse
import csv
import json
import sys
from pathlib import Path
from urllib.request import urlopen

# ---- Field Mappings ---------------------------------------------------------

FM_FIELD_MAP = {
    "VBELN":  "salesOrderNumber",
    "POSNR":  "itemNumber",
    "AUART":  "salesDocumentType",
    "MATNR":  "materialNumber",
    "MAKTX":  "materialDescription",
    "MEINS":  "baseUnitOfMeasure",
    "KWMENG": "orderQuantity",
    "VRKME":  "salesUnit",
    "PSTYV":  "itemCategory",
    "VKORG":  "salesOrganization",
    "VTWEG":  "distributionChannel",
    "SPART":  "division",
    "ERDAT":  "createdDate",
    "ERNAM":  "createdBy",
}

REPORT_FIELD_MAP = {
    "MATNR": "materialNumber",
    "MAKTX": "materialDescription",
    "MTART": "materialType",
    "MEINS": "baseUnitOfMeasure",
    "WERKS": "plant",
    "LABST": "unrestrictedStock",
}

ALPHA_FIELDS = {"MATNR", "POSNR", "VBELN"}


# ---- Helpers ----------------------------------------------------------------

def normalize_alpha(value: str) -> str:
    """Strip leading zeros for ALPHA-converted fields."""
    if not value:
        return ""
    stripped = str(value).lstrip("0")
    return stripped or "0"


def normalize_date(sap_date: str) -> str:
    """Convert SAP date (YYYYMMDD) to ISO 8601 (YYYY-MM-DD)."""
    if not sap_date or len(sap_date) != 8:
        return sap_date or ""
    return f"{sap_date[:4]}-{sap_date[4:6]}-{sap_date[6:8]}"


def normalize_value(field: str, value) -> str:
    """Apply field-specific normalisation."""
    if field in ALPHA_FIELDS:
        return normalize_alpha(value)
    if field == "ERDAT":
        return normalize_date(str(value or ""))
    if field in ("KWMENG", "LABST"):
        try:
            return str(float(value or 0))
        except ValueError:
            return str(value or "")
    return str(value or "").strip()


# ---- Main ------------------------------------------------------------------

def main():
    parser = argparse.ArgumentParser(description="Cross-validate SAP CSV vs Java API")
    parser.add_argument("--sap-csv", required=True, help="Path to SAP baseline CSV")
    parser.add_argument("--api-url", required=True, help="Java REST API endpoint URL")
    parser.add_argument("--output", required=True, help="Output HTML report path")
    parser.add_argument("--field-map", default="fm", choices=["fm", "report"],
                        help="Field mapping to use (fm or report)")
    args = parser.parse_args()

    field_map = REPORT_FIELD_MAP if args.field_map == "report" else FM_FIELD_MAP

    # 1. Read SAP CSV
    with open(args.sap_csv, newline="", encoding="utf-8") as f:
        sap_rows = list(csv.DictReader(f))
    print(f"SAP CSV: {len(sap_rows)} rows")

    # 2. Call Java API
    with urlopen(args.api_url) as resp:
        api_json = json.loads(resp.read())
    java_rows = api_json.get("items") or api_json.get("data") or api_json
    if not isinstance(java_rows, list):
        java_rows = []
    print(f"Java API: {len(java_rows)} rows")

    # 3. Compare row-by-row
    results = []
    max_rows = max(len(sap_rows), len(java_rows))

    for i in range(max_rows):
        sap_row = sap_rows[i] if i < len(sap_rows) else {}
        java_row = java_rows[i] if i < len(java_rows) else {}
        row_result = {"index": i, "fields": [], "pass": True}

        for sap_field, java_field in field_map.items():
            sap_val = normalize_value(sap_field, sap_row.get(sap_field, ""))
            java_val = normalize_value(sap_field, java_row.get(java_field, ""))
            match = sap_val == java_val
            if not match:
                row_result["pass"] = False
            row_result["fields"].append({
                "sap_field": sap_field,
                "java_field": java_field,
                "sap_val": sap_val,
                "java_val": java_val,
                "match": match,
            })

        results.append(row_result)

    # 4. Generate HTML report
    html = generate_html(results, args)
    Path(args.output).parent.mkdir(parents=True, exist_ok=True)
    Path(args.output).write_text(html, encoding="utf-8")

    pass_count = sum(1 for r in results if r["pass"])
    fail_count = sum(1 for r in results if not r["pass"])
    print(f"Result: {pass_count} pass, {fail_count} fail -- report: {args.output}")

    sys.exit(1 if fail_count > 0 else 0)


# ---- HTML Report ------------------------------------------------------------

def generate_html(results: list, args) -> str:
    pass_count = sum(1 for r in results if r["pass"])
    fail_count = sum(1 for r in results if not r["pass"])
    total_count = len(results)

    rows_html = ""
    for r in results:
        for f in r["fields"]:
            bg = "#e6ffe6" if f["match"] else "#ffe6e6"
            status = "MATCH" if f["match"] else "DIFF"
            rows_html += f"""
        <tr style="background:{bg}">
          <td>{r['index']}</td>
          <td>{_esc(f['sap_field'])}</td>
          <td>{_esc(f['java_field'])}</td>
          <td><code>{_esc(f['sap_val'])}</code></td>
          <td><code>{_esc(f['java_val'])}</code></td>
          <td><strong>{status}</strong></td>
        </tr>"""

    return f"""<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Cross-Validation Diff Report</title>
  <style>
    body {{ font-family: Arial, sans-serif; margin: 20px; }}
    h1 {{ color: #333; }}
    .summary {{ margin: 20px 0; padding: 15px; border: 1px solid #ccc; border-radius: 4px; }}
    .pass {{ color: green; font-weight: bold; }}
    .fail {{ color: red; font-weight: bold; }}
    table {{ border-collapse: collapse; width: 100%; margin-top: 20px; }}
    th, td {{ border: 1px solid #ccc; padding: 8px; text-align: left; font-size: 13px; }}
    th {{ background: #f5f5f5; }}
    code {{ background: #f0f0f0; padding: 2px 4px; border-radius: 2px; }}
  </style>
</head>
<body>
  <h1>Cross-Validation Diff Report</h1>
  <div class="summary">
    <p><strong>SAP CSV:</strong> {_esc(args.sap_csv)}</p>
    <p><strong>Java API:</strong> {_esc(args.api_url)}</p>
    <p><strong>Total Rows:</strong> {total_count}</p>
    <p class="pass">Matching Rows: {pass_count}</p>
    <p class="fail">Mismatched Rows: {fail_count}</p>
    <p><strong>Result:</strong> {'<span class="pass">PASS</span>' if fail_count == 0 else '<span class="fail">FAIL</span>'}</p>
  </div>
  <table>
    <thead>
      <tr>
        <th>Row</th>
        <th>SAP Field</th>
        <th>Java Field</th>
        <th>SAP Value</th>
        <th>Java Value</th>
        <th>Status</th>
      </tr>
    </thead>
    <tbody>{rows_html}
    </tbody>
  </table>
</body>
</html>"""


def _esc(text: str) -> str:
    return (str(text or "")
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace('"', "&quot;"))


if __name__ == "__main__":
    main()
```

---

## HTML Report Format

The generated HTML report contains:

### Summary Section
- SAP CSV file path
- Java API endpoint URL
- Total row count
- Matching row count (green)
- Mismatched row count (red)
- Overall PASS / FAIL verdict

### Detail Table

| Column | Description |
|--------|-------------|
| Row | Zero-based row index |
| SAP Field | Original SAP field name (e.g., `VBELN`) |
| Java Field | Mapped Java field name (e.g., `salesOrderNumber`) |
| SAP Value | Normalised value from CSV |
| Java Value | Normalised value from API |
| Status | `MATCH` (green) or `DIFF` (red) |

Rows with differences are highlighted in red (`#ffe6e6`).
Matching rows are highlighted in green (`#e6ffe6`).

---

## Usage Examples

### TC-057: FM Default Output Diff

```bash
# Node.js
node scripts/cross-validate.js \
  --sap-csv test-artifacts/phase0-baseline/csv-exports/TC-001_fm-default-output.csv \
  --api-url http://localhost:8080/api/materials/000000000000000123/sales-orders \
  --output test-artifacts/phase6-xvalidation/diff-reports/TC-057_diff.html \
  --field-map fm

# Python
python scripts/cross_validate.py \
  --sap-csv test-artifacts/phase0-baseline/csv-exports/TC-001_fm-default-output.csv \
  --api-url http://localhost:8080/api/materials/000000000000000123/sales-orders \
  --output test-artifacts/phase6-xvalidation/diff-reports/TC-057_diff.html \
  --field-map fm
```

### TC-061: Report Aggregated All Plants Diff

```bash
node scripts/cross-validate.js \
  --sap-csv test-artifacts/phase0-baseline/csv-exports/TC-006_report-aggregated.csv \
  --api-url http://localhost:8080/api/materials/stock \
  --output test-artifacts/phase6-xvalidation/diff-reports/TC-061_diff.html \
  --field-map report
```

### TC-065: ALPHA Conversion Alignment

```bash
# The script automatically handles ALPHA conversion for MATNR, POSNR, VBELN.
# No special flags needed -- normalisation is built in.
node scripts/cross-validate.js \
  --sap-csv test-artifacts/phase0-baseline/csv-exports/TC-001_fm-default-output.csv \
  --api-url http://localhost:8080/api/materials/123/sales-orders \
  --output test-artifacts/phase6-xvalidation/diff-reports/TC-065_alpha-diff.html
```

---

## ABAP Source References

| Topic | File | Lines | Description |
|-------|------|-------|-------------|
| ALPHA conversion | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt` | 36-39 | `CONVERSION_EXIT_MATN1_INPUT` call |
| FM output structure | `LZFG_MAT_SOTOP.txt` | 6-21 | `ty_out` with 14 fields |
| Report output structure | `ZMAT_REPORT.PROG` | 14-21 | `ty_out` with 6 fields |
| FM data selection (all orders) | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt` | 45-55 | VBAP/VBAK/MAKT/MARA joins |
| FM data selection (filtered) | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt` | 56-68 | + VBELN filter |
| Report by-plant query | `ZMAT_REPORT.PROG` | 65-112 | GROUP BY with WERKS |
| Report aggregated query | `ZMAT_REPORT.PROG` | 113-161 | GROUP BY without WERKS |
| MAXROWS truncation | `ZFM_GET_MAT_SO_DETAILS..FUGR.txt` | 93-99 | Off-by-one: lv_max+1 |
| Top-N restriction | `ZMAT_REPORT.PROG` | 164-170 | SORT DESC + DELETE |
