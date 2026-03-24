import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { MaterialStockComponent } from './material-stock/material-stock.component';
import { SalesOrderComponent } from './sales-order/sales-order.component';

const routes: Routes = [
  { path: 'materials/stock', component: MaterialStockComponent },
  { path: 'sales-orders', component: SalesOrderComponent },
  { path: '', redirectTo: '/materials/stock', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
