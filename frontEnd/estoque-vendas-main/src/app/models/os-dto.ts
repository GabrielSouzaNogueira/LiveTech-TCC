export type OsStatus = 'ABERTA' | 'FINALIZADA' | 'CANCELADA';

export interface ClienteResumoDTO { id: number; nome: string; sobrenome: string; }
export interface ProdutoResumoDTO { prodId: number; nome: string; }
export interface ServicoResumoDTO { servicosId: number; descServico: string; }

// NOVO: DTO para o Modal de Equipamento
export interface EquipamentoResumoDTO {
  id: number;
  modelo: string;
  numeroSerie: string;
}

export interface ItensOsProdutoDTO { produtoId: number; quantidade: number; }
export interface ItensOsServicoDTO { servicoId: number; quantidade: number; }

export interface OsRequestDTO {
  clienteId: number;
  equipamentoId: number; // Mudou de String para ID
  defeitoRelatado: string;
  desconto: number;
  dataPrevisao: string; 
  itensProduto: ItensOsProdutoDTO[];
  itensServico: ItensOsServicoDTO[];
}

export interface OsListDTO {
  id: number;
  nomeCliente: string;
  equipamentoId: number;          // Adicionado
  equipamentoModelo: string;      // Adicionado
  equipamentoNumeroSerie: string; // Adicionado
  observacoes: string;
  valorTotalGeral: number;
  status: OsStatus;
  dataAbertura: string;
  dataPrevisao: string;
  mensagem: string;
}

export interface ProdutoResumoDTO { 
  prodId: number; 
  nome: string; 
  precoVenda: number; // <- ADICIONADO
}

export interface ServicoResumoDTO { 
  servicosId: number; 
  descServico: string; 
  precoServico: number;      // <- ADICIONADO
}