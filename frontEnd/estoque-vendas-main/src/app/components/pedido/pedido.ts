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

  // --- Controle da Busca de Cliente ---
  termoBuscaCliente: string = '';
  dropdownClienteAberto: boolean = false;
  clienteIdSelecionado: number | null = null;
  
  // --- Controle da Busca de Produto ---
  termoBuscaProduto: string = '';
  dropdownProdutoAberto: boolean = false;
  produtoSelecionadoObj: SelectAllProdDTO | null = null;

  descontoDigitado: number = 0;
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

  ngOnInit(): void {
    this.carregarClientes();
    this.carregarProdutos();
  }

  alternarAba(aba: 'cadastrar' | 'gerenciar'): void {
    this.abaAtiva = aba;
  }

  get valorTotalCarrinho(): number {
    const totalItens = this.itensCarrinho.reduce((soma, item) => soma + item.subtotal, 0);
    const total = totalItens - (this.descontoDigitado || 0);
    return total > 0 ? total : 0;
  }

  get podeSalvar(): boolean {
    return this.clienteIdSelecionado !== null && this.itensCarrinho.length > 0;
  }

  carregarClientes(): void {
    this.authGerenciadorClienteService.listarClientes().subscribe({
      next: (dados) => { this.clientes = dados; this.cdr.detectChanges(); },
      error: (e) => console.error(e)
    });
  }

  carregarProdutos(): void {
    this.authGerenciadorProdutoService.listarProdutos().subscribe({
      next: (dados) => { this.produtos = dados; this.cdr.detectChanges(); },
      error: (e) => console.error(e)
    });
  }

  // === LÓGICA DE BUSCA E SELEÇÃO (AUTOCOMPLETE) ===

  get clientesFiltrados(): ClienteSelectDTO[] {
    if (!this.termoBuscaCliente) return this.clientes;
    const termo = this.termoBuscaCliente.toLowerCase();
    return this.clientes.filter(c => 
      c.nome.toLowerCase().includes(termo) || c.sobrenome.toLowerCase().includes(termo)
    );
  }

 selecionarCliente(cliente: ClienteSelectDTO): void {
    // Usamos o Number() para forçar a conversão caso venha como string
    // e o || null para caso venha undefined
    this.clienteIdSelecionado = cliente.id ? Number(cliente.id) : null; 
    this.termoBuscaCliente = `${cliente.nome} ${cliente.sobrenome}`;
    this.dropdownClienteAberto = false;
  }

  limparCliente(): void {
    this.clienteIdSelecionado = null;
    this.termoBuscaCliente = '';
  }

  get produtosFiltrados(): SelectAllProdDTO[] {
    if (!this.termoBuscaProduto) return this.produtos;
    const termo = this.termoBuscaProduto.toLowerCase();
    return this.produtos.filter(p => p.nome.toLowerCase().includes(termo));
  }

  selecionarProdutoBusca(produto: SelectAllProdDTO): void {
    this.produtoSelecionadoObj = produto;
    this.termoBuscaProduto = `${produto.nome} — R$ ${produto.precoVenda.toFixed(2)}`;
    this.dropdownProdutoAberto = false;
  }

  limparProduto(): void {
    this.produtoSelecionadoObj = null;
    this.termoBuscaProduto = '';
  }

  fecharDropdowns(event: FocusEvent): void {
    // Timeout para dar tempo do evento 'mousedown' das listas ser disparado antes de esconder
    setTimeout(() => {
      this.dropdownClienteAberto = false;
      this.dropdownProdutoAberto = false;
    }, 150);
  }

  // === LÓGICA DO CARRINHO ===

  adicionarItem(): void {
    if (!this.produtoSelecionadoObj || this.quantidadeParaAdicionar <= 0) {
      this.authNotificacaoService.erro('Selecione um produto e uma quantidade válida.');
      return;
    }

    const produtoId = Number(this.produtoSelecionadoObj.prodId);
    const itemExistente = this.itensCarrinho.find(i => i.produtoId === produtoId);

    if (itemExistente) {
      itemExistente.quantidade += this.quantidadeParaAdicionar;
      itemExistente.subtotal = itemExistente.quantidade * itemExistente.precoVenda;
    } else {
      this.itensCarrinho.push({
        produtoId: produtoId,
        nome: this.produtoSelecionadoObj.nome,
        precoVenda: this.produtoSelecionadoObj.precoVenda,
        quantidade: this.quantidadeParaAdicionar,
        subtotal: this.produtoSelecionadoObj.precoVenda * this.quantidadeParaAdicionar
      });
    }

    this.limparProduto();
    this.quantidadeParaAdicionar = 1;
  }

  removerItem(index: number): void {
    this.itensCarrinho.splice(index, 1);
  }

  // === EDIÇÃO E SALVAMENTO ===

  carregarParaEdicao(pedido: PedidoDetalheDTO): void {
    this.pedidoIdEmEdicao = pedido.id;
    this.clienteIdSelecionado = pedido.cliente.id;
    this.termoBuscaCliente = `${pedido.cliente.nome} ${pedido.cliente.sobrenome}`;
    this.descontoDigitado = pedido.desconto || 0;
    
    // NOTA: mantive pedido.itensVenda conforme o seu DTO atual.
    this.itensCarrinho = pedido.itensVenda.map(item => ({
      produtoId: item.produto.prodId,
      nome: item.produto.nome,
      precoVenda: item.precoVenda,
      quantidade: item.quantidade,
      subtotal: item.precoVenda * item.quantidade
    }));
    this.abaAtiva = 'cadastrar';
  }

  cancelarEdicao(): void {
    this.limparFormulario();
  }

  private limparFormulario(): void {
    this.pedidoIdEmEdicao = null;
    this.limparCliente();
    this.descontoDigitado = 0;
    this.itensCarrinho = [];
    this.limparProduto();
    this.quantidadeParaAdicionar = 1;
  }

  salvarPedido(): void {
    if (this.carregando || !this.podeSalvar) return;
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

    const requisicao = this.pedidoIdEmEdicao
      ? this.authPedidoService.atualizarPedido(this.pedidoIdEmEdicao, dto, usuarioAtual)
      : this.authPedidoService.criarPedido(dto, usuarioAtual);

    requisicao.subscribe({
      next: (resposta) => {
        this.carregando = false;
        this.authNotificacaoService.sucesso(resposta?.mensagem || 'Pedido salvo!');
        this.limparFormulario();
      },
      error: (erro: HttpErrorResponse) => {
        this.carregando = false;
        const msg = this.authNotificacaoService.extrairMensagemErro(erro, 'Erro ao salvar o pedido.');
        this.authNotificacaoService.erro(msg);
      }
    });
  }
}