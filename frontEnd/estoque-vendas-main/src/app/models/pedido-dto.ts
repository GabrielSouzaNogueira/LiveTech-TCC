export type PedidoStatus = 'ABERTA' | 'FINALIZADA' | 'DEVOLVIDA' | 'CANCELADA';

// --- Envio (criar / atualizar) ---
export interface ItemPedidoDTO {
  produtoId: number;
  quantidade: number;
}

export interface PedidoRequestDTO {
  clienteId: number;
  desconto: number;
  itensPedido: ItemPedidoDTO[];
}

// --- Pagamento (finalizar) ---
export interface PagPedidoRequestDTO {
  formaPagId: number;
  valorPago: number;
}

// --- Listagem (tabela) ---
export interface PedidoListDTO {
  id: number;
  nomeCliente: string;
  valorTotal: number;
  status: PedidoStatus;
  dataVenda: string;
}

// --- Detalhe (buscar por id) ---
// Reflete a entidade crua que o back-end devolve hoje.
// Propositalmente NÃO declarei o campo "usuario" que vem junto:
// ele hoje inclui o hash da senha do usuário logado (ver aviso no chat).
export interface ClienteResumoDTO {
  id: number;
  nome: string;
  sobrenome: string;
}

export interface ProdutoResumoDTO {
  prodId: number;
  nome: string;
}

export interface ItemPedidoDetalheDTO {
  id: number;
  produto: ProdutoResumoDTO;
  quantidade: number;
  precoUnitario: number;
  precoVenda: number;
}

export interface FormaPagResumoDTO {
  id: number;
  descricao: string;
}

export interface PagPedidoDetalheDTO {
  id: number;
  formaPagto: FormaPagResumoDTO;
  valorPago: number;
}

export interface PedidoDetalheDTO {
  id: number;
  cliente: ClienteResumoDTO;
  valorTotal: number;
  desconto: number;
  dataVenda: string;
  status: PedidoStatus;
  itensVenda: ItemPedidoDetalheDTO[];
  pagVenda: PagPedidoDetalheDTO[];
}