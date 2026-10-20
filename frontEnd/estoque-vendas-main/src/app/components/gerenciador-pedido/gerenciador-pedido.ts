import { ChangeDetectorRef, Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthPedido } from '../../services/auth-pedido'; // Tiramos o PedidoStatus daqui
import { AuthNotificacaoService } from '../../services/auth-notificacao';
import { PedidoDetalheDTO, PedidoListDTO, PedidoStatus } from '../../models/pedido-dto'; // E colocamos ele aqui
import { FinalizarPedido } from '../finalizar-pedido/finalizar-pedido';
@Component({
  selector: 'app-gerenciador-pedido',
  templateUrl: './gerenciador-pedido.html',
  styleUrl: './gerenciador-pedido.css',
  imports: [CommonModule, FormsModule, FinalizarPedido],
  standalone: true
})
export class GerenciadorPedido implements OnInit {
  @Output() editarPedido = new EventEmitter<PedidoDetalheDTO>();

  listaPedidosGeral: PedidoListDTO[] = [];
  listaPedidosFiltrada: PedidoListDTO[] = [];
  carregando: boolean = false;

  filtroStatus: PedidoStatus | 'TODOS' = 'TODOS';
  termoBuscaID: string = '';

  // --- Controle do Modal de Detalhes ---
  pedidoEmDetalhe: PedidoDetalheDTO | null = null;
  modalDetalhesAberto: boolean = false;

  // --- Controle da Finalização ---
  pedidoSelecionadoParaFinalizar: number | null = null;
  valorTotalParaFinalizar: number = 0;

  constructor(
    private authPedidoService: AuthPedido,
    private authNotificacaoService: AuthNotificacaoService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.carregarListaPedidos();
  }

  carregarListaPedidos(): void {
    this.carregando = true;
    this.authPedidoService.listarPedidos().subscribe({
      next: (dados: PedidoListDTO[]) => {
        this.listaPedidosGeral = dados;
        this.aplicarFiltros();
        this.carregando = false;
        this.cdr.detectChanges();
      },
      error: (erro: any) => {
        console.error('Erro ao buscar pedidos', erro);
        this.listaPedidosGeral = [];
        this.aplicarFiltros();
        this.carregando = false;
        this.cdr.detectChanges();
      }
    });
  }

  aplicarFiltros(): void {
    let lista = this.listaPedidosGeral;

    if (this.filtroStatus !== 'TODOS') {
      lista = lista.filter(p => p.status === this.filtroStatus);
    }

    if (this.termoBuscaID) {
      lista = lista.filter(p => p.id.toString().includes(this.termoBuscaID));
    }

    this.listaPedidosFiltrada = lista;
  }

  abrirDetalhes(pedido: PedidoListDTO): void {
    this.carregando = true;
    this.authPedidoService.buscarPedidoPorId(pedido.id).subscribe({
      next: (detalhe: PedidoDetalheDTO) => {
        this.pedidoEmDetalhe = detalhe;
        this.modalDetalhesAberto = true;
        this.carregando = false;
        this.cdr.detectChanges();
      },
      error: (erro: HttpErrorResponse) => {
        const mensagem = this.authNotificacaoService.extrairMensagemErro(erro, 'Não foi possível carregar os detalhes do pedido.');
        this.authNotificacaoService.erro(mensagem);
        this.carregando = false;
        this.cdr.detectChanges();
      }
    });
  }

  fecharDetalhes(): void {
    this.modalDetalhesAberto = false;
    this.pedidoEmDetalhe = null;
  }

  imprimirPedido(): void {
      window.print();
  }

  // --- AÇÕES DENTRO DO MODAL ---

  acaoEditar(): void {
    if (this.pedidoEmDetalhe) {
      this.editarPedido.emit(this.pedidoEmDetalhe);
      this.fecharDetalhes();
    }
  }

  acaoAbrirFinalizar(): void {
    if (this.pedidoEmDetalhe) {
      this.pedidoSelecionadoParaFinalizar = this.pedidoEmDetalhe.id;
      this.valorTotalParaFinalizar = this.pedidoEmDetalhe.valorTotal;
      this.fecharDetalhes();
    }
  }

  fecharFinalizar(): void {
    this.pedidoSelecionadoParaFinalizar = null;
  }

  aoFinalizar(): void {
    this.fecharFinalizar();
    this.carregarListaPedidos();
  }

  acaoCancelar(): void {
    if (!this.pedidoEmDetalhe) return;
    
    const confirmacao = confirm(`Tem certeza que deseja cancelar o pedido #${this.pedidoEmDetalhe.id}?`);
    if (!confirmacao) return;

    const usuarioAtual = localStorage.getItem('usuarioLogado') || 'Sistema';
    this.authPedidoService.cancelarPedido(this.pedidoEmDetalhe.id, usuarioAtual).subscribe({
      next: (mensagemTexto: any) => {
        this.authNotificacaoService.sucesso(mensagemTexto || 'Pedido cancelado com sucesso!');
        this.fecharDetalhes();
        this.carregarListaPedidos();
      },
      error: (erro: HttpErrorResponse) => {
        const mensagem = this.authNotificacaoService.extrairMensagemErro(erro, 'Não foi possível cancelar o pedido.');
        this.authNotificacaoService.erro(mensagem);
      }
    });
  }

  acaoDevolver(): void {
    if (!this.pedidoEmDetalhe) return;

    const confirmacao = confirm(`Confirma a devolução do pedido #${this.pedidoEmDetalhe.id}? O estoque dos produtos será restaurado.`);
    if (!confirmacao) return;

    const usuarioAtual = localStorage.getItem('usuarioLogado') || 'Sistema';
    this.authPedidoService.devolverPedido(this.pedidoEmDetalhe.id, usuarioAtual).subscribe({
      next: (resposta: any) => {
        this.authNotificacaoService.sucesso(resposta?.mensagem || 'Devolução concluída!');
        this.fecharDetalhes();
        this.carregarListaPedidos();
      },
      error: (erro: HttpErrorResponse) => {
        const mensagem = this.authNotificacaoService.extrairMensagemErro(erro, 'Não foi possível concluir a devolução.');
        this.authNotificacaoService.erro(mensagem);
      }
    });
  }
}