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
    if (!this.pedidoEmDetalhe) {
      this.authNotificacaoService.erro('Nenhum pedido selecionado para impressão.');
      return;
    }

    const pedido = this.pedidoEmDetalhe;
    const janela = window.open('', '', 'width=800,height=600');
    
    if (janela) {
      const dataAtual = new Date().toLocaleDateString('pt-BR');
      
      const dataPedido = pedido.dataVenda ? new Date(pedido.dataVenda).toLocaleDateString('pt-BR') : dataAtual;
      // Ajustado para ler o objeto cliente (nome + sobrenome)
      const nomeCliente = pedido.cliente ? `${pedido.cliente.nome} ${pedido.cliente.sobrenome}` : 'Consumidor Final';
      const valorTotal = pedido.valorTotal ? pedido.valorTotal.toFixed(2).replace('.', ',') : '0,00';
      const desconto = pedido.desconto ? pedido.desconto.toFixed(2).replace('.', ',') : '0,00';

      // Ajustado para ler 'itensVenda' e os atributos corretos do 'ItemPedidoDetalheDTO'
      let itensHtml = '';
      if (pedido.itensVenda && pedido.itensVenda.length > 0) {
        itensHtml = `
          <div class="section">
            <h3>Itens do Pedido</h3>
            <table style="width: 100%; border-collapse: collapse; margin-top: 10px; font-size: 14px;">
              <thead>
                <tr style="background-color: #f8f9fa; border-bottom: 2px solid #ddd;">
                  <th style="padding: 10px; text-align: left;">Produto</th>
                  <th style="padding: 10px; text-align: center;">Qtd</th>
                  <th style="padding: 10px; text-align: right;">Val. Unit.</th>
                  <th style="padding: 10px; text-align: right;">Subtotal</th>
                </tr>
              </thead>
              <tbody>
                ${pedido.itensVenda.map((item: any) => `
                  <tr style="border-bottom: 1px solid #eee;">
                    <td style="padding: 10px;">${item.produto ? item.produto.nome : 'Item #' + item.id}</td>
                    <td style="padding: 10px; text-align: center;">${item.quantidade}</td>
                    <td style="padding: 10px; text-align: right;">R$ ${item.precoUnitario ? item.precoUnitario.toFixed(2).replace('.', ',') : '0,00'}</td>
                    <td style="padding: 10px; text-align: right;">R$ ${item.precoVenda ? item.precoVenda.toFixed(2).replace('.', ',') : '0,00'}</td>
                  </tr>
                `).join('')}
              </tbody>
            </table>
          </div>
        `;
      }

      const html = `
        <!DOCTYPE html>
        <html>
          <head>
            <title>Impressão - Pedido #${pedido.id}</title>
            <style>
              body { font-family: 'Arial', sans-serif; color: #333; line-height: 1.6; padding: 30px; max-width: 800px; margin: 0 auto; }
              .header { text-align: center; border-bottom: 2px solid #2e7d32; padding-bottom: 15px; margin-bottom: 25px; }
              .header h1 { margin: 0; color: #2e7d32; font-size: 24px; text-transform: uppercase; }
              .header p { margin: 5px 0 0 0; font-size: 14px; color: #666; }
              .section { margin-bottom: 25px; }
              .section h3 { border-bottom: 1px solid #ddd; padding-bottom: 8px; margin-bottom: 15px; color: #444; font-size: 16px; text-transform: uppercase; }
              .row { display: flex; margin-bottom: 8px; font-size: 14px; }
              .label { font-weight: bold; min-width: 150px; color: #555; }
              .value { flex: 1; }
              .total-box { background: #f8f9fa; border: 1px solid #ddd; padding: 15px; text-align: right; border-radius: 6px; margin-top: 30px; }
              .total-box span { font-size: 20px; font-weight: bold; color: #2e7d32; }
              .desconto-texto { font-size: 14px; color: #d32f2f; margin-bottom: 5px; font-weight: normal; }
              .signatures { display: flex; justify-content: space-between; margin-top: 80px; }
              .signature-line { border-top: 1px solid #333; width: 45%; text-align: center; padding-top: 8px; font-size: 14px; }
              .footer { margin-top: 40px; text-align: center; font-size: 12px; color: #777; }
              
              @media print {
                body { padding: 0; }
                .total-box { -webkit-print-color-adjust: exact; background: #f8f9fa !important; }
              }
            </style>
          </head>
          <body>
            <div class="header">
              <h1>Recibo de Pedido #${pedido.id}</h1>
              <p>Data de Emissão: ${dataAtual}</p>
            </div>

            <div class="section">
              <h3>Dados do Cliente</h3>
              <div class="row"><div class="label">Nome:</div> <div class="value">${nomeCliente}</div></div>
            </div>

            <div class="section">
              <h3>Informações do Pedido</h3>
              <div class="row"><div class="label">Status:</div> <div class="value">${pedido.status}</div></div>
              <div class="row"><div class="label">Data do Pedido:</div> <div class="value">${dataPedido}</div></div>
            </div>

            ${itensHtml}

            <div class="total-box">
              ${pedido.desconto > 0 ? `<div class="desconto-texto">Desconto aplicado: R$ ${desconto}</div>` : ''}
              Total Geral: <span>R$ ${valorTotal}</span>
            </div>

            <div class="signatures">
              <div class="signature-line">Assinatura do Cliente</div>
              <div class="signature-line">Assinatura do Responsável</div>
            </div>

            <div class="footer">
              <p>Documento gerado pelo sistema de Gestão e Controle.</p>
            </div>
          </body>
        </html>
      `;
      
      janela.document.write(html);
      janela.document.close();
      
      setTimeout(() => {
        janela.print();
      }, 250);
    }
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