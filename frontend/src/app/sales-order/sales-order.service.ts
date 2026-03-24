import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SalesOrderResponse } from '../shared/models/sales-order.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class SalesOrderService {

  private readonly baseUrl = `${environment.apiBaseUrl}/sales-orders`;

  constructor(private http: HttpClient) {}

  getOrders(params: {
    matnr: string;
    vbeln?: string;
    language?: string;
    maxRows?: number;
  }): Observable<SalesOrderResponse> {
    let httpParams = new HttpParams().set('matnr', params.matnr);

    if (params.vbeln) {
      httpParams = httpParams.set('vbeln', params.vbeln);
    }
    if (params.language) {
      httpParams = httpParams.set('language', params.language);
    }
    if (params.maxRows !== undefined && params.maxRows > 0) {
      httpParams = httpParams.set('maxRows', String(params.maxRows));
    }

    return this.http.get<SalesOrderResponse>(this.baseUrl, { params: httpParams });
  }
}
