import { ChangeDetectorRef, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthFormaPagamento } from '../../services/auth-forma-pagamento';
import { AuthPedido } from '../../services/auth-pedido';
import { AuthNotificacaoService } from '../../services/auth-notificacao';
import { SelectFormPag } from '../../models/forma-pagamento-dto';
import { PagPedidoRequestDTO } from '../../models/pedido-dto';

interface LinhaPagamento {
  formaPagId: number | null;
  valorPago: number | null;
}

@Component({
  selector: 'app-finalizar-pedido',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './finalizar-pedido.html',
  styleUrl: './finalizar-pedido.css',
})
export class FinalizarPedido implements OnInit {
  @Input() pedidoId!: number;
  @Input() valorTotal: number = 0;

  @Output() finalizado = new EventEmitter<void>();
  @Output() fechar = new EventEmitter<void>();

  formasPagamento: SelectFormPag[] = [];
  linhasPagamento: LinhaPagamento[] = [{ formaPagId: null, valorPago: null }];

  carregando: boolean = false;

  constructor(
    private authFormaPagamentoService: AuthFormaPagamento,
    private authPedidoService: AuthPedido,
    private authNotificacaoService: AuthNotificacaoService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.authFormaPagamentoService.listarFormaPagamento().subscribe({
      next: (dados) => {
        this.formasPagamento = dados;
        this.cdr.detectChanges();
      },
      error: (erro) => console.error('Erro ao buscar formas de pagamento', erro)
    });
  }

  get totalPago(): number {
    return this.linhasPagamento.reduce((soma, linha) => soma + (linha.valorPago || 0), 0);
  }

  get faltante(): number {
    const diferenca = this.valorTotal - this.totalPago;
    return diferenca > 0 ? diferenca : 0;
  }

  adicionarLinha(): void {
    this.linhasPagamento.push({ formaPagId: null, valorPago: null });
  }

  removerLinha(index: number): void {
    if (this.linhasPagamento.length > 1) {
      this.linhasPagamento.splice(index, 1);
    }
  }

  confirmarPagamento(): void {
    const linhasValidas = this.linhasPagamento.every(l => l.formaPagId !== null && l.valorPago !== null && l.valorPago > 0);

    if (!linhasValidas) {
      this.authNotificacaoService.erro('Preencha a forma de pagamento e o valor em todas as linhas.');
      return;
    }

    if (this.totalPago < this.valorTotal) {
      this.authNotificacaoService.erro('O valor pago é menor que o total do pedido.');
      return;
    }

    this.carregando = true;
    const usuarioAtual = localStorage.getItem('usuarioLogado') || 'Sistema';

    const pagamentos: PagPedidoRequestDTO[] = this.linhasPagamento.map(l => ({
      formaPagId: l.formaPagId as number,
      valorPago: l.valorPago as number
    }));

    this.authPedidoService.finalizarPedido(this.pedidoId, pagamentos, usuarioAtual).subscribe({
      next: (resposta) => {
        this.carregando = false;
        this.authNotificacaoService.sucesso(resposta?.mensagem || 'Pedido finalizado com sucesso!');
        this.finalizado.emit();
      },
      error: (erro: HttpErrorResponse) => {
        this.carregando = false;
        const mensagem = this.authNotificacaoService.extrairMensagemErro(erro, 'Não foi possível finalizar o pedido.');
        this.authNotificacaoService.erro(mensagem);
        this.cdr.detectChanges();
      }
    });
  }

  fecharPainel(): void {
    this.fechar.emit();
  }
}