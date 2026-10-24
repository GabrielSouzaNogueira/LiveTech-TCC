import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, DestroyRef, OnInit, inject } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { SelectFormPag } from '../../models/forma-pagamento-dto';
import { AuthFormaPagamento } from '../../services/auth-forma-pagamento';
import { AuthNotificacaoService } from '../../services/auth-notificacao';

@Component({
  selector: 'app-forma-pagamento',
  imports: [FormsModule, CommonModule],
  templateUrl: './forma-pagamento.html',
  styleUrl: './forma-pagamento.css',
  standalone: true
})
export class FormaPagamento implements OnInit {
  private readonly destroyRef = inject(DestroyRef);

  descricaoDigitado: string = '';
  listarFormaPag: SelectFormPag[] = [];
  carregando: boolean = false;
  inativando: Set<number> = new Set(); // Alterado para armazenar IDs

  // --- Controle de Edição Inline na Tabela ancorado em ID ---
  itemEmEdicaoId: number | null = null;
  descricaoOriginal: string = '';
  novaDescricaoEdit: string = '';
  salvandoEdicao: boolean = false;

  constructor(
    private authFormaPagamentoService: AuthFormaPagamento,
    private cdr: ChangeDetectorRef,
    private authNotificacaoService: AuthNotificacaoService
  ) {}

  ngOnInit(): void {
    this.carregarListaFormaPagamento();
  }

  carregarListaFormaPagamento(): void {
    this.authFormaPagamentoService
      .listarFormaPagamento()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (dados) => {
          this.listarFormaPag = dados;
          this.cdr.detectChanges();
        },
        error: (erro) => {
          console.error('Erro ao buscar as formas de pagamento', erro);
          this.listarFormaPag = []; 
          this.cdr.detectChanges();
        },
      });
  }

  // --- FUNÇÕES DE EDIÇÃO ---

  iniciarEdicao(item: SelectFormPag): void {
    const usuarioAtual = localStorage.getItem('usuarioLogado') || 'Sistema';

    // Busca o registro atualizado pelo ID no backend antes de abrir o campo
    this.authFormaPagamentoService
      .buscarPorId(item.id, usuarioAtual)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (dados) => {
          this.itemEmEdicaoId = dados.id;
          this.descricaoOriginal = dados.descricao;
          this.novaDescricaoEdit = dados.descricao;
          this.cdr.detectChanges();
        },
        error: (erro: HttpErrorResponse) => {
          const msg = this.authNotificacaoService.extrairMensagemErro(erro, 'Erro ao carregar dados para edição.');
          this.authNotificacaoService.erro(msg);
        }
      });
  }

  cancelarEdicao(): void {
    this.itemEmEdicaoId = null;
    this.descricaoOriginal = '';
    this.novaDescricaoEdit = '';
  }

  salvarEdicao(id: number): void {
    if (!this.novaDescricaoEdit.trim() || this.salvandoEdicao) return;
    
    if (this.novaDescricaoEdit.trim().toUpperCase() === this.descricaoOriginal.toUpperCase()) {
      this.cancelarEdicao();
      return;
    }

    this.salvandoEdicao = true;
    const usuarioAtual = localStorage.getItem('usuarioLogado') || 'Sistema';

    this.authFormaPagamentoService
      .atualizarFormaPagamento(id, this.novaDescricaoEdit.trim(), usuarioAtual)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (resposta) => {
          this.authNotificacaoService.sucesso(resposta?.mensagem || 'Forma de pagamento atualizada!');
          this.salvandoEdicao = false;
          this.cancelarEdicao();
          this.carregarListaFormaPagamento();
        },
        error: (erro: HttpErrorResponse) => {
          this.salvandoEdicao = false;
          const msg = this.authNotificacaoService.extrairMensagemErro(erro, 'Erro ao atualizar.');
          this.authNotificacaoService.erro(msg);
          this.cdr.detectChanges();
        }
      });
  }

  // --- FUNÇÕES DE INATIVAÇÃO ---

  inativarFormaPagamento(formaPagamento: SelectFormPag, event: Event): void {
    event.stopPropagation();

    if (this.inativando.has(formaPagamento.id)) {
      return;
    }

    const confirmacao = confirm(
      `Tem certeza que deseja inativar a forma de pagamento "${formaPagamento.descricao}"?`
    );

    if (!confirmacao) {
      return;
    }

    this.inativando.add(formaPagamento.id);
    const usuarioAtual = localStorage.getItem('usuarioLogado') || 'Sistema';

    this.authFormaPagamentoService
      .inativarFormaPagamento(formaPagamento.id, usuarioAtual)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (resposta) => {
          this.authNotificacaoService.sucesso(
            resposta?.mensagem || `Forma de pagamento "${formaPagamento.descricao}" inativada com sucesso!`
          );
          this.inativando.delete(formaPagamento.id);
          this.cdr.detectChanges();
          this.carregarListaFormaPagamento();
        },
        error: (erro: HttpErrorResponse) => {
          const mensagem = this.authNotificacaoService.extrairMensagemErro(erro, 'Não foi possível inativar');
          this.authNotificacaoService.erro(mensagem);
          this.inativando.delete(formaPagamento.id);
          this.cdr.detectChanges();
        },
      });
  }

  isInativando(formaPagamento: SelectFormPag): boolean {
    return this.inativando.has(formaPagamento.id);
  }

  // --- FUNÇÃO DE CADASTRO ---

  executarFormaPagamento(): void {
    if (this.carregando) return;

    const descricao = this.descricaoDigitado.trim();
    if (!descricao) return;

    this.carregando = true;
    const usuarioAtual = localStorage.getItem('usuarioLogado') || 'Sistema';

    this.authFormaPagamentoService
      .cadastroFormaPagamento(descricao, usuarioAtual)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (resposta) => {
          this.carregando = false;
          this.descricaoDigitado = '';
          this.cdr.detectChanges();
          this.authNotificacaoService.sucesso(resposta?.mensagem || 'Cadastro realizado com sucesso');
          this.carregarListaFormaPagamento();
        },
        error: (erro: HttpErrorResponse) => {
          this.carregando = false;
          this.cdr.detectChanges();
          const mensagem = this.authNotificacaoService.extrairMensagemErro(erro, 'Cadastro não foi possível');
          this.authNotificacaoService.erro(mensagem);
        },
      });
  }

  trackById(_index: number, item: SelectFormPag): number {
    return item.id;
  }
}