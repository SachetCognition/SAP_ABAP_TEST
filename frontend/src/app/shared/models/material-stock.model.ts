export interface MaterialStockDTO {
  matnr: string;
  maktx: string | null;
  mtart: string;
  meins: string;
  werks: string | null;
  labst: number;
}

export interface MaterialStockResponse {
  data: MaterialStockDTO[];
  count: number;
}
