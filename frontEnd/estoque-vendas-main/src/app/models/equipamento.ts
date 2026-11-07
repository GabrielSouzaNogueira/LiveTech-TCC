export interface Equipamento {
  id?: number;
  modelo: string;
  numeroSerie: string;
  status: string; // Você pode tipar com 'ATIVO' | 'INATIVO' se preferir
}