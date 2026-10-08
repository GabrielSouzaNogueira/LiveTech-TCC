export enum FormaPagStatus {
  ATIVO = 'ATIVO',
  DESATIVADO = 'DESATIVADO'
}

export interface SelectFormPag{
  id: number;
  descricao: string;
  status: string;
}