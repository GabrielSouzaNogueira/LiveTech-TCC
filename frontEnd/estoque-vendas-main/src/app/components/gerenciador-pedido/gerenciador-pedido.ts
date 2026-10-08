import { ChangeDetectorRef, Component, EventEmitter, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthPedido } from '../../services/auth-pedido';
import { AuthNotificacaoService } from '../../services/auth-notificacao';
import { PedidoDetalheDTO, PedidoListDTO } from '../../models/pedido-dto';
import { FinalizarPedido } from '../finalizar-pedido/finalizar-pedido';

@Component({
  selector: 'app-gerenciador-pedido',
  templateUrl: './gerenciador-pedido.html',
  styleUrl: './gerenciador-pedido.css',
  imports: [CommonModule, FinalizarPedido],
})
export class GerenciadorPedido implements OnInit {
  @Output() editarPedido = new EventEmitter<PedidoDetalheDTO>();

  listaPedidos: PedidoListDTO[] = [];
  carregando: boolean = false;

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
        this.listaPedidos = dados;
        this.carregando = false;
        this.cdr.detectChanges();
      },
      error: (erro: any) => {
        console.error('Erro ao buscar pedidos', erro);
        this.listaPedidos = [];
        this.carregando = false;
        this.cdr.detectChanges();
      }
    });
  }

  editar(pedido: PedidoListDTO): void {
    this.authPedidoService.buscarPedidoPorId(pedido.id).subscribe({
      next: (detalhe: any) => {
        this.editarPedido.emit(detalhe);
      },
      error: (erro: HttpErrorResponse) => {
        const mensagem = this.authNotificacaoService.extrairMensagemErro(erro, 'Não foi possível abrir esse pedido para edição.');
        this.authNotificacaoService.erro(mensagem);
      }
    });
  }

  abrirFinalizar(pedido: PedidoListDTO): void {
    this.pedidoSelecionadoParaFinalizar = pedido.id;
    this.valorTotalParaFinalizar = pedido.valorTotal;
  }

  fecharFinalizar(): void {
    this.pedidoSelecionadoParaFinalizar = null;
  }

  aoFinalizar(): void {
    this.fecharFinalizar();
    this.carregarListaPedidos();
  }

  cancelar(pedido: PedidoListDTO): void {
    const confirmacao = confirm(`Tem certeza que deseja cancelar o pedido #${pedido.id}?`);
    if (!confirmacao) {
      return;
    }

    const usuarioAtual = localStorage.getItem('usuarioLogado') || 'Sistema';
    this.authPedidoService.cancelarPedido(pedido.id, usuarioAtual).subscribe({
      next: (mensagemTexto: any) => {
        this.authNotificacaoService.sucesso(mensagemTexto || 'Pedido cancelado com sucesso!');
        this.carregarListaPedidos();
      },
      error: (erro: HttpErrorResponse) => {
        const mensagem = this.authNotificacaoService.extrairMensagemErro(erro, 'Não foi possível cancelar o pedido.');
        this.authNotificacaoService.erro(mensagem);
      }
    });
  }

  devolver(pedido: PedidoListDTO): void {
    const confirmacao = confirm(`Confirma a devolução do pedido #${pedido.id}? O estoque dos produtos será restaurado.`);
    if (!confirmacao) {
      return;
    }

    const usuarioAtual = localStorage.getItem('usuarioLogado') || 'Sistema';
    this.authPedidoService.devolverPedido(pedido.id, usuarioAtual).subscribe({
      next: (resposta: { mensagem: any; }) => {
        this.authNotificacaoService.sucesso(resposta?.mensagem || 'Devolução concluída!');
        this.carregarListaPedidos();
      },
      error: (erro: HttpErrorResponse) => {
        const mensagem = this.authNotificacaoService.extrairMensagemErro(erro, 'Não foi possível concluir a devolução.');
        this.authNotificacaoService.erro(mensagem);
      }
    });
  }
}