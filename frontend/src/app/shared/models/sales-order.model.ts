export interface SalesOrderDTO {
  vbeln: string;
  posnr: string;
  auart: string;
  matnr: string;
  maktx: string | null;
  meins: string;
  kwmeng: number;
  vrkme: string;
  pstyv: string;
  vkorg: string;
  vtweg: string;
  spart: string;
  erdat: string;
  ernam: string;
}

export interface ApiMessage {
  type: string;
  id: string;
  number: string;
  message: string;
}

export interface SalesOrderResponse {
  data: SalesOrderDTO[];
  count: number;
  messages: ApiMessage[];
}
