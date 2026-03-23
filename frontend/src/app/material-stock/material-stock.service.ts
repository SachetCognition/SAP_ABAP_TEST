import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { MaterialStockResponse } from '../shared/models/material-stock.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class MaterialStockService {

  private readonly baseUrl = `${environment.apiBaseUrl}/materials/stock`;

  constructor(private http: HttpClient) {}

  getStock(params: {
    matnr?: string;
    mtart?: string;
    plant?: string;
    language?: string;
    byPlant?: boolean;
    top?: number;
  }): Observable<MaterialStockResponse> {
    let httpParams = new HttpParams();

    if (params.matnr) {
      params.matnr.split(',').map(s => s.trim()).filter(s => s)
        .forEach(m => httpParams = httpParams.append('matnr', m));
    }
    if (params.mtart) {
      params.mtart.split(',').map(s => s.trim()).filter(s => s)
        .forEach(m => httpParams = httpParams.append('mtart', m));
    }
    if (params.plant) {
      httpParams = httpParams.set('plant', params.plant);
    }
    if (params.language) {
      httpParams = httpParams.set('language', params.language);
    }
    if (params.byPlant !== undefined) {
      httpParams = httpParams.set('byPlant', String(params.byPlant));
    }
    if (params.top !== undefined && params.top > 0) {
      httpParams = httpParams.set('top', String(params.top));
    }

    return this.http.get<MaterialStockResponse>(this.baseUrl, { params: httpParams });
  }
}
