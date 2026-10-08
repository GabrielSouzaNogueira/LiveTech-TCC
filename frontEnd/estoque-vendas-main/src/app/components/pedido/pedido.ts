import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthGerenciadorCliente } from '../../services/auth-gerenciador-cliente';
import { AuthGerenciadorProduto } from '../../services/auth-gerenciador-produto';
import { AuthPedido } from '../../services/auth-pedido';
import { AuthNotificacaoService } from '../../services/auth-notificacao';
import { ClienteSelectDTO } from '../../models/cliente-select-dto';
import { SelectAllProdDTO } from '../../models/select-all-prod-dto';
import { PedidoDetalheDTO, PedidoRequestDTO } from '../../models/pedido-dto';
import { GerenciadorPedido } from '../gerenciador-pedido/gerenciador-pedido';

interface ItemCarrinho {
  produtoId: number;
  nome: string;
  precoVenda: number;
  quantidade: number;
  subtotal: number;
}

@Component({
  selector: 'app-pedido',
  standalone: true,
  imports: [CommonModule, FormsModule, GerenciadorPedido],
  templateUrl: './pedido.html',
  styleUrl: './pedido.css',
})
export class Pedido implements OnInit {
  abaAtiva: 'cadastrar' | 'gerenciar' = 'cadastrar';

  clientes: ClienteSelectDTO[] = [];
  produtos: SelectAllProdDTO[] = [];

  clienteIdSelecionado: number | null = null;
  descontoDigitado: number = 0;

  produtoIdParaAdicionar: number | null = null;
  quantidadeParaAdicionar: number = 1;

  itensCarrinho: ItemCarrinho[] = [];

  pedidoIdEmEdicao: number | null = null;

  carregando: boolean = false;

  constructor(
    private authGerenciadorClienteService: AuthGerenciadorCliente,
    private authGerenciadorProdutoService: AuthGerenciadorProduto,
    private authPedidoService: AuthPedido,
    private authNotificacaoService: AuthNotificacaoService,
    private cdr: ChangeDetectorRef
  ) {}

  /**
   * Ciclo de vida do Angular: executado ao inicializar o componente.
   * Carrega as listas iniciais de clientes e produtos.
   */
  ngOnInit(): void {
    this.carregarClientes();
    this.carregarProdutos();
  }

  /**
   * Alterna a visualização entre as abas 'cadastrar' e 'gerenciar'.
   */
  alternarAba(aba: 'cadastrar' | 'gerenciar'): void {
    this.abaAtiva = aba;
  }

  /**
   * Calcula o valor total do carrinho subtraindo o desconto digitado.
   * Garante que o valor final não seja menor que zero.
   */
  get valorTotalCarrinho(): number {
    const totalItens = this.itensCarrinho.reduce((soma, item) => soma + item.subtotal, 0);
    const total = totalItens - (this.descontoDigitado || 0);
    return total > 0 ? total : 0;
  }

  /**
   * Valida se o pedido pode ser salvo (exige cliente selecionado e ao menos um item no carrinho).
   */
  get podeSalvar(): boolean {
    return this.clienteIdSelecionado !== null && this.itensCarrinho.length > 0;
  }

  /**
   * Busca a lista de clientes no serviço backend e atualiza o estado local.
   */
  carregarClientes(): void {
    this.authGerenciadorClienteService.listarClientes().subscribe({
      next: (dados) => {
        this.clientes = dados;
        this.cdr.detectChanges();
      },
      error: (erro) => console.error('Erro ao buscar clientes', erro)
    });
  }

  /**
   * Busca a lista de produtos no serviço backend e atualiza o estado local.
   */
  carregarProdutos(): void {
    this.authGerenciadorProdutoService.listarProdutos().subscribe({
      next: (dados) => {
        this.produtos = dados;
        this.cdr.detectChanges();
      },
      error: (erro) => console.error('Erro ao buscar produtos', erro)
    });
  }

  /**
   * Adiciona o produto selecionado ao carrinho de compras.
   * Se o item já existir no carrinho, apenas incrementa a quantidade e recalcula o subtotal.
   */
  adicionarItem(): void {
    if (!this.produtoIdParaAdicionar || this.quantidadeParaAdicionar <= 0) {
      this.authNotificacaoService.erro('Selecione um produto e uma quantidade válida.');
      return;
    }

    const produto = this.produtos.find(p => Number(p.prodId) === Number(this.produtoIdParaAdicionar));
    if (!produto) {
      return;
    }

    const produtoId = Number(produto.prodId);
    const itemExistente = this.itensCarrinho.find(i => i.produtoId === produtoId);

    if (itemExistente) {
      itemExistente.quantidade += this.quantidadeParaAdicionar;
      itemExistente.subtotal = itemExistente.quantidade * itemExistente.precoVenda;
    } else {
      this.itensCarrinho.push({
        produtoId: produtoId,
        nome: produto.nome,
        precoVenda: produto.precoVenda,
        quantidade: this.quantidadeParaAdicionar,
        subtotal: produto.precoVenda * this.quantidadeParaAdicionar
      });
    }

    this.produtoIdParaAdicionar = null;
    this.quantidadeParaAdicionar = 1;
  }

  /**
   * Remove um item do carrinho com base no seu índice no array.
   */
  removerItem(index: number): void {
    this.itensCarrinho.splice(index, 1);
  }

  /**
   * Preenche o formulário com os dados de um pedido existente para alteração e redireciona para a aba de cadastro.
   */
  carregarParaEdicao(pedido: PedidoDetalheDTO): void {
    this.pedidoIdEmEdicao = pedido.id;
    this.clienteIdSelecionado = pedido.cliente.id;
    this.descontoDigitado = pedido.desconto || 0;
    this.itensCarrinho = pedido.itensVenda.map(item => ({
      produtoId: item.produto.prodId,
      nome: item.produto.nome,
      precoVenda: item.precoVenda,
      quantidade: item.quantidade,
      subtotal: item.precoVenda * item.quantidade
    }));
    this.abaAtiva = 'cadastrar';
  }

  /**
   * Cancela o processo de edição limpando os campos do formulário.
   */
  cancelarEdicao(): void {
    this.limparFormulario();
  }

  /**
   * Reseta o estado do formulário para os valores padrão.
   */
  private limparFormulario(): void {
    this.pedidoIdEmEdicao = null;
    this.clienteIdSelecionado = null;
    this.descontoDigitado = 0;
    this.itensCarrinho = [];
    this.produtoIdParaAdicionar = null;
    this.quantidadeParaAdicionar = 1;
  }

  /**
   * Envia a requisição para salvar o pedido no backend (cria um novo ou atualiza um existente se estiver em modo de edição).
   */
  salvarPedido(): void {
    if (this.carregando || !this.podeSalvar) {
      return;
    }
    this.carregando = true;

    const usuarioAtual = localStorage.getItem('usuarioLogado') || 'Sistema';

    const dto: PedidoRequestDTO = {
      clienteId: this.clienteIdSelecionado as number,
      desconto: this.descontoDigitado || 0,
      itensPedido: this.itensCarrinho.map(item => ({
        produtoId: item.produtoId,
        quantidade: item.quantidade
      }))
    };

    // Decide se chama a API de criar ou atualizar
    const requisicao = this.pedidoIdEmEdicao
      ? this.authPedidoService.atualizarPedido(this.pedidoIdEmEdicao, dto, usuarioAtual)
      : this.authPedidoService.criarPedido(dto, usuarioAtual);

    requisicao.subscribe({
      next: (resposta) => {
        this.carregando = false;
        this.authNotificacaoService.sucesso(resposta?.mensagem || 'Pedido salvo com sucesso!');
        this.limparFormulario();
        this.cdr.detectChanges();
      },
      error: (erro: HttpErrorResponse) => {
        this.carregando = false;
        const mensagem = this.authNotificacaoService.extrairMensagemErro(erro, 'Não foi possível salvar o pedido.');
        this.authNotificacaoService.erro(mensagem);
        this.cdr.detectChanges();
      }
    });
  }
}