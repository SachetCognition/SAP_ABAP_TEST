#!/usr/bin/env node

/**
 * Cross-Validation Script
 * 
 * Compares SAP baseline CSV exports with Java REST API output
 * to verify that the migration produces identical results.
 * 
 * Usage:
 *   node scripts/cross-validate.js [--api-base http://localhost:8080/api]
 * 
 * Prerequisites:
 *   - SAP baseline CSVs in test-artifacts/phase0-baseline/csv-exports/
 *   - Java backend running at the specified API base URL
 */

const fs = require('fs');
const path = require('path');
const http = require('http');

// Configuration
const API_BASE = process.argv.includes('--api-base')
  ? process.argv[process.argv.indexOf('--api-base') + 1]
  : 'http://localhost:8080/api';

const CSV_DIR = path.join(__dirname, '..', 'test-artifacts', 'phase0-baseline', 'csv-exports');
const OUTPUT_DIR = path.join(__dirname, '..', 'test-artifacts', 'phase6-xvalidation', 'diff-reports');

// SAP field name to Java camelCase mapping
const FIELD_MAP = {
  'MATNR': 'matnr',
  'MAKTX': 'maktx',
  'MTART': 'mtart',
  'MEINS': 'meins',
  'WERKS': 'werks',
  'LABST': 'labst',
  'VBELN': 'vbeln',
  'POSNR': 'posnr',
  'AUART': 'auart',
  'KWMENG': 'kwmeng',
  'VRKME': 'vrkme',
  'PSTYV': 'pstyv',
  'VKORG': 'vkorg',
  'VTWEG': 'vtweg',
  'SPART': 'spart',
  'ERDAT': 'erdat',
  'ERNAM': 'ernam'
};

/**
 * Parse CSV file into array of objects
 */
function parseCsv(filePath) {
  const content = fs.readFileSync(filePath, 'utf-8');
  const lines = content.trim().split('\n');
  if (lines.length < 2) return { headers: [], rows: [] };

  const headers = lines[0].split(',').map(h => h.trim().replace(/"/g, ''));
  const rows = [];

  for (let i = 1; i < lines.length; i++) {
    const values = lines[i].split(',').map(v => v.trim().replace(/"/g, ''));
    const row = {};
    headers.forEach((h, idx) => {
      row[h] = values[idx] || '';
    });
    rows.push(row);
  }

  return { headers, rows };
}

/**
 * Normalize SAP field names to Java camelCase
 */
function normalizeFieldName(sapField) {
  return FIELD_MAP[sapField.toUpperCase()] || sapField.toLowerCase();
}

/**
 * Apply ALPHA conversion - pad material number to 18 chars with leading zeros
 */
function alphaConvert(matnr) {
  if (!matnr) return matnr;
  const trimmed = matnr.trim();
  // If it's purely numeric, pad to 18
  if (/^\d+$/.test(trimmed)) {
    return trimmed.padStart(18, '0');
  }
  return trimmed;
}

/**
 * Normalize a value for comparison
 */
function normalizeValue(value, fieldName) {
  if (value === null || value === undefined) return '';
  const str = String(value).trim();

  // Handle ALPHA conversion for material numbers
  if (fieldName === 'matnr') {
    return alphaConvert(str);
  }

  // Handle numeric values - normalize decimal places
  if (['labst', 'kwmeng'].includes(fieldName)) {
    const num = parseFloat(str);
    if (!isNaN(num)) return num.toFixed(3);
  }

  // Handle date normalization (SAP YYYYMMDD vs Java YYYY-MM-DD)
  if (fieldName === 'erdat') {
    const cleaned = str.replace(/-/g, '');
    if (/^\d{8}$/.test(cleaned)) {
      return `${cleaned.substring(0, 4)}-${cleaned.substring(4, 6)}-${cleaned.substring(6, 8)}`;
    }
  }

  return str;
}

/**
 * Fetch data from Java REST API
 */
function fetchApi(endpoint) {
  return new Promise((resolve, reject) => {
    const url = API_BASE + endpoint;
    http.get(url, (res) => {
      let data = '';
      res.on('data', chunk => data += chunk);
      res.on('end', () => {
        try {
          resolve(JSON.parse(data));
        } catch (e) {
          reject(new Error(`Failed to parse API response: ${e.message}`));
        }
      });
    }).on('error', reject);
  });
}

/**
 * Compare SAP CSV rows with Java API rows
 */
function compareRows(sapRows, javaRows, fieldMapping) {
  const results = {
    totalSap: sapRows.length,
    totalJava: javaRows.length,
    matches: 0,
    mismatches: [],
    missingSap: [],
    missingJava: []
  };

  // Create lookup maps
  const javaMap = new Map();
  javaRows.forEach((row, idx) => {
    const key = Object.values(row).join('|');
    javaMap.set(key, { row, idx });
  });

  for (let i = 0; i < sapRows.length; i++) {
    const sapRow = sapRows[i];
    const normalizedSap = {};

    // Normalize SAP row
    Object.keys(sapRow).forEach(key => {
      const javaField = normalizeFieldName(key);
      normalizedSap[javaField] = normalizeValue(sapRow[key], javaField);
    });

    // Find matching Java row
    if (i < javaRows.length) {
      const javaRow = javaRows[i];
      const normalizedJava = {};
      Object.keys(javaRow).forEach(key => {
        normalizedJava[key] = normalizeValue(javaRow[key], key);
      });

      // Compare fields
      let isMatch = true;
      const diffs = {};
      Object.keys(normalizedSap).forEach(field => {
        if (normalizedSap[field] !== normalizedJava[field]) {
          isMatch = false;
          diffs[field] = {
            sap: normalizedSap[field],
            java: normalizedJava[field] || '(missing)'
          };
        }
      });

      if (isMatch) {
        results.matches++;
      } else {
        results.mismatches.push({ rowIndex: i, diffs });
      }
    } else {
      results.missingJava.push({ rowIndex: i, sapRow: normalizedSap });
    }
  }

  // Check for extra Java rows
  for (let i = sapRows.length; i < javaRows.length; i++) {
    results.missingSap.push({ rowIndex: i, javaRow: javaRows[i] });
  }

  return results;
}

/**
 * Generate HTML diff report
 */
function generateHtmlReport(title, results, outputPath) {
  const timestamp = new Date().toISOString();
  const status = results.mismatches.length === 0 && results.missingJava.length === 0 && results.missingSap.length === 0
    ? '✅ PASS' : '❌ FAIL';

  let html = `<!DOCTYPE html>
<html>
<head>
  <title>${title} - Cross-Validation Report</title>
  <style>
    body { font-family: Arial, sans-serif; margin: 20px; }
    h1 { color: #333; }
    .pass { color: green; font-weight: bold; }
    .fail { color: red; font-weight: bold; }
    table { border-collapse: collapse; width: 100%; margin-top: 10px; }
    th, td { border: 1px solid #ddd; padding: 8px; text-align: left; }
    th { background-color: #4CAF50; color: white; }
    .mismatch { background-color: #ffcccc; }
    .missing { background-color: #ffffcc; }
    .summary { background-color: #f0f0f0; padding: 15px; border-radius: 5px; margin: 10px 0; }
  </style>
</head>
<body>
  <h1>${title} - Cross-Validation Report</h1>
  <p>Generated: ${timestamp}</p>
  <p>Status: <span class="${results.mismatches.length === 0 ? 'pass' : 'fail'}">${status}</span></p>

  <div class="summary">
    <h2>Summary</h2>
    <table>
      <tr><th>Metric</th><th>Value</th></tr>
      <tr><td>SAP Rows</td><td>${results.totalSap}</td></tr>
      <tr><td>Java Rows</td><td>${results.totalJava}</td></tr>
      <tr><td>Matching Rows</td><td>${results.matches}</td></tr>
      <tr><td>Mismatched Rows</td><td>${results.mismatches.length}</td></tr>
      <tr><td>Missing in Java</td><td>${results.missingJava.length}</td></tr>
      <tr><td>Missing in SAP</td><td>${results.missingSap.length}</td></tr>
    </table>
  </div>`;

  if (results.mismatches.length > 0) {
    html += `
  <h2>Mismatched Rows</h2>
  <table>
    <tr><th>Row</th><th>Field</th><th>SAP Value</th><th>Java Value</th></tr>`;

    results.mismatches.forEach(m => {
      Object.entries(m.diffs).forEach(([field, vals]) => {
        html += `
    <tr class="mismatch">
      <td>${m.rowIndex}</td>
      <td>${field}</td>
      <td>${vals.sap}</td>
      <td>${vals.java}</td>
    </tr>`;
      });
    });

    html += '\n  </table>';
  }

  if (results.missingJava.length > 0) {
    html += `
  <h2>Rows Missing in Java Output</h2>
  <table>
    <tr><th>Row Index</th><th>SAP Data</th></tr>`;

    results.missingJava.forEach(m => {
      html += `
    <tr class="missing">
      <td>${m.rowIndex}</td>
      <td>${JSON.stringify(m.sapRow)}</td>
    </tr>`;
    });

    html += '\n  </table>';
  }

  html += `
</body>
</html>`;

  fs.mkdirSync(path.dirname(outputPath), { recursive: true });
  fs.writeFileSync(outputPath, html, 'utf-8');
  console.log(`Report saved to: ${outputPath}`);
}

/**
 * Main execution
 */
async function main() {
  console.log('=== SAP ABAP to Java Cross-Validation ===');
  console.log(`API Base: ${API_BASE}`);
  console.log(`CSV Directory: ${CSV_DIR}`);
  console.log(`Output Directory: ${OUTPUT_DIR}`);
  console.log('');

  // Check if CSV directory exists
  if (!fs.existsSync(CSV_DIR)) {
    console.log('⚠️  CSV directory not found. Creating placeholder...');
    fs.mkdirSync(CSV_DIR, { recursive: true });
    console.log('Please place SAP baseline CSV exports in:');
    console.log(`  ${CSV_DIR}`);
    console.log('');
    console.log('Expected files:');
    console.log('  - material_stock_default.csv');
    console.log('  - material_stock_byplant.csv');
    console.log('  - sales_orders_mat001.csv');
    return;
  }

  const csvFiles = fs.readdirSync(CSV_DIR).filter(f => f.endsWith('.csv'));

  if (csvFiles.length === 0) {
    console.log('⚠️  No CSV files found in baseline directory.');
    console.log('Please capture SAP baseline data first (see docs/ARTIFACT_CAPTURE_GUIDE.md)');
    return;
  }

  let allPassed = true;

  for (const csvFile of csvFiles) {
    console.log(`\nProcessing: ${csvFile}`);
    const csvPath = path.join(CSV_DIR, csvFile);
    const csvData = parseCsv(csvPath);

    if (csvData.rows.length === 0) {
      console.log(`  ⚠️  Empty CSV file, skipping`);
      continue;
    }

    // Determine API endpoint based on CSV filename
    let apiEndpoint;
    if (csvFile.includes('material_stock')) {
      const params = csvFile.includes('byplant') ? '?byPlant=true' : '';
      apiEndpoint = `/materials/stock${params}`;
    } else if (csvFile.includes('sales_order')) {
      const matnr = csvFile.match(/mat(\w+)/i);
      apiEndpoint = `/sales-orders?matnr=${matnr ? matnr[1] : 'MAT001'}`;
    } else {
      console.log(`  ⚠️  Unknown CSV type, skipping`);
      continue;
    }

    try {
      console.log(`  Calling API: ${apiEndpoint}`);
      const apiResponse = await fetchApi(apiEndpoint);
      const javaRows = apiResponse.data || [];

      console.log(`  SAP rows: ${csvData.rows.length}, Java rows: ${javaRows.length}`);

      const results = compareRows(csvData.rows, javaRows);

      const reportName = csvFile.replace('.csv', '_diff.html');
      const reportPath = path.join(OUTPUT_DIR, reportName);

      generateHtmlReport(csvFile.replace('.csv', ''), results, reportPath);

      if (results.mismatches.length > 0 || results.missingJava.length > 0 || results.missingSap.length > 0) {
        console.log(`  ❌ FAIL - ${results.mismatches.length} mismatches, ${results.missingJava.length} missing in Java, ${results.missingSap.length} extra in Java`);
        allPassed = false;
      } else {
        console.log(`  ✅ PASS - ${results.matches}/${results.totalSap} rows match`);
      }
    } catch (err) {
      console.log(`  ❌ ERROR - ${err.message}`);
      allPassed = false;
    }
  }

  console.log('\n=== Cross-Validation Complete ===');
  console.log(allPassed ? '✅ All validations passed' : '❌ Some validations failed');
  process.exit(allPassed ? 0 : 1);
}

main().catch(err => {
  console.error('Fatal error:', err);
  process.exit(1);
});
