import { Component, OnInit, ViewChild } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { MaterialStockService } from './material-stock.service';
import { MaterialStockDTO } from '../shared/models/material-stock.model';
import { ColDef, GridApi, GridReadyEvent } from 'ag-grid-community';

@Component({
  selector: 'app-material-stock',
  templateUrl: './material-stock.component.html',
  styleUrls: ['./material-stock.component.css']
})
export class MaterialStockComponent implements OnInit {

  form!: FormGroup;
  rowData: MaterialStockDTO[] = [];
  noDataMessage = '';
  reportHeader = '';
  private gridApi!: GridApi;

  /** AG Grid column definitions matching ALV columns from ZMAT_REPORT.PROG lines 196-230 */
  columnDefs: ColDef[] = [
    { field: 'matnr', headerName: 'Material Number', minWidth: 180 },
    { field: 'maktx', headerName: 'Material Description', minWidth: 200 },
    { field: 'mtart', headerName: 'Material Type', minWidth: 120 },
    { field: 'meins', headerName: 'Base Unit of Measure', minWidth: 150 },
    { field: 'werks', headerName: 'Plant', minWidth: 100 },
    { field: 'labst', headerName: 'Unrestricted-Use Stock', minWidth: 180, type: 'numericColumn',
      valueFormatter: (params: any) => params.value != null ? Number(params.value).toFixed(3) : '' }
  ];

  /** Replaces set_all(abap_true) at ZMAT_REPORT.PROG line 246 */
  defaultColDef: ColDef = {
    sortable: true,
    filter: true,
    resizable: true
  };

  pinnedBottomRowData: any[] = [];

  languages = [
    { value: 'E', label: 'English' },
    { value: 'D', label: 'German' },
    { value: 'F', label: 'French' },
    { value: 'S', label: 'Spanish' }
  ];

  constructor(
    private fb: FormBuilder,
    private stockService: MaterialStockService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      matnr: [''],
      mtart: [''],
      plant: [''],
      language: ['E'],
      byPlant: [false],
      top: [0]
    });
    this.updateReportHeader();
  }

  onGridReady(params: GridReadyEvent): void {
    this.gridApi = params.api;
  }

  onSubmit(): void {
    this.noDataMessage = '';
    const val = this.form.value;

    this.stockService.getStock({
      matnr: val.matnr,
      mtart: val.mtart,
      plant: val.plant,
      language: val.language,
      byPlant: val.byPlant,
      top: val.top
    }).subscribe({
      next: (response) => {
        this.rowData = response.data;
        this.updateReportHeader();

        if (response.data.length === 0) {
          this.noDataMessage = 'No data found for given selection';
          this.pinnedBottomRowData = [];
        } else {
          // Pinned bottom row with SUM of labst (replaces add_aggregation('LABST') at lines 233-237)
          const totalStock = response.data.reduce((sum, row) => sum + (row.labst || 0), 0);
          this.pinnedBottomRowData = [{
            matnr: 'Total',
            maktx: '',
            mtart: '',
            meins: '',
            werks: '',
            labst: totalStock
          }];
        }

        // Auto-size columns (replaces set_optimize at line 194)
        if (this.gridApi) {
          this.gridApi.autoSizeAllColumns();
        }
      },
      error: (err) => {
        this.noDataMessage = 'Error loading data: ' + (err.message || 'Unknown error');
        this.rowData = [];
        this.pinnedBottomRowData = [];
      }
    });
  }

  /** CSV export (replaces ALV export functionality) */
  exportCsv(): void {
    if (this.gridApi) {
      this.gridApi.exportDataAsCsv({
        fileName: 'material-stock-report.csv'
      });
    }
  }

  private updateReportHeader(): void {
    const now = new Date();
    const date = now.toLocaleDateString();
    const time = now.toLocaleTimeString();
    this.reportHeader = `Material Stock Report - ${date} ${time}`;
  }
}
