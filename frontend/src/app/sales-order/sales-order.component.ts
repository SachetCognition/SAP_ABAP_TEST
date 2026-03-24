import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SalesOrderService } from './sales-order.service';
import { SalesOrderDTO, ApiMessage } from '../shared/models/sales-order.model';
import { ColDef, GridApi, GridReadyEvent } from 'ag-grid-community';

@Component({
  selector: 'app-sales-order',
  templateUrl: './sales-order.component.html',
  styleUrls: ['./sales-order.component.css']
})
export class SalesOrderComponent implements OnInit {

  form!: FormGroup;
  rowData: SalesOrderDTO[] = [];
  messages: ApiMessage[] = [];
  private gridApi!: GridApi;

  /** Column definitions matching ty_out from LZFG_MAT_SOTOP.txt lines 6-21 */
  columnDefs: ColDef[] = [
    { field: 'vbeln', headerName: 'Sales Order', minWidth: 120 },
    { field: 'posnr', headerName: 'Item Number', minWidth: 110 },
    { field: 'auart', headerName: 'Sales Doc Type', minWidth: 130 },
    { field: 'matnr', headerName: 'Material', minWidth: 180 },
    { field: 'maktx', headerName: 'Material Description', minWidth: 200 },
    { field: 'meins', headerName: 'Base UoM', minWidth: 100 },
    { field: 'kwmeng', headerName: 'Order Quantity', minWidth: 130, type: 'numericColumn',
      valueFormatter: (params: any) => params.value != null ? Number(params.value).toFixed(3) : '' },
    { field: 'vrkme', headerName: 'Sales Unit', minWidth: 100 },
    { field: 'pstyv', headerName: 'Item Category', minWidth: 120 },
    { field: 'vkorg', headerName: 'Sales Org', minWidth: 100 },
    { field: 'vtweg', headerName: 'Distribution Channel', minWidth: 160 },
    { field: 'spart', headerName: 'Division', minWidth: 100 },
    { field: 'erdat', headerName: 'Created On', minWidth: 120 },
    { field: 'ernam', headerName: 'Created By', minWidth: 120 }
  ];

  defaultColDef: ColDef = {
    sortable: true,
    filter: true,
    resizable: true
  };

  languages = [
    { value: 'E', label: 'English' },
    { value: 'D', label: 'German' },
    { value: 'F', label: 'French' },
    { value: 'S', label: 'Spanish' }
  ];

  constructor(
    private fb: FormBuilder,
    private salesService: SalesOrderService
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      matnr: ['', Validators.required],
      vbeln: [''],
      language: ['E'],
      maxRows: [0]
    });
  }

  onGridReady(params: GridReadyEvent): void {
    this.gridApi = params.api;
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.messages = [];
    const val = this.form.value;

    this.salesService.getOrders({
      matnr: val.matnr,
      vbeln: val.vbeln || undefined,
      language: val.language,
      maxRows: val.maxRows
    }).subscribe({
      next: (response) => {
        this.rowData = response.data;
        this.messages = response.messages || [];

        if (this.gridApi) {
          this.gridApi.autoSizeAllColumns();
        }
      },
      error: (err) => {
        this.rowData = [];
        if (err.error && err.error.messages) {
          this.messages = err.error.messages;
        } else {
          this.messages = [{
            type: 'E',
            id: 'CLIENT',
            number: '000',
            message: 'Error: ' + (err.message || 'Unknown error')
          }];
        }
      }
    });
  }
}
