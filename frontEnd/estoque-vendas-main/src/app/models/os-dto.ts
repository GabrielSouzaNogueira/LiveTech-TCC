// --- Tipos e Resumos ---
export type OsStatus = 'ABERTA' | 'FINALIZADA' | 'CANCELADA';

export interface ClienteResumoDTO {
  id: number;
  nome: string;
  sobrenome: string;
}

export interface ProdutoResumoDTO {
  prodId: number; 
  nome: string;
}

export interface ServicoResumoDTO {
  servicosId: number;
  descServico: string;
}

// --- Request (Criar / Atualizar OS) ---
export interface ItensOsProdutoDTO {
  produtoId: number;
  quantidade: number;
}

export interface ItensOsServicoDTO {
  servicoId: number;
  quantidade: number;
}

export interface OsRequestDTO {
  clienteId: number;
  equipamento: string;
  defeitoRelatado: string;
  desconto: number;
  dataPrevisao: string; 
  itensProduto: ItensOsProdutoDTO[];
  itensServico: ItensOsServicoDTO[];
}

// --- Response (Listagem/Tabela) ---
export interface OsListDTO {
  id: number;
  nomeCliente: string;
  descricaoEquipamento: string;
  observacoes: string;
  valorTotalGeral: number;
  status: OsStatus;
  dataAbertura: string;
  dataPrevisao: string;
  mensagem: string;
}