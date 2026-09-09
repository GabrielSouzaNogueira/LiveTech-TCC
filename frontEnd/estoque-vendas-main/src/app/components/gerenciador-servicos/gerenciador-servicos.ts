import { ChangeDetectorRef, Component, DestroyRef, inject, OnInit } from '@angular/core';
import { ServicosDTO } from '../../models/servicos-dto';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthGerenciadorServicos } from '../../services/auth-gerenciador-servicos';
import { AuthNotificacaoService } from '../../services/auth-notificacao';
import { Router } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-gerenciador-servicos',
  imports: [FormsModule, CommonModule],
  templateUrl: './gerenciador-servicos.html',
  styleUrl: './gerenciador-servicos.css',
})
export class GerenciadorServicos implements OnInit{
  servicosIdSelecionado: string = '';
  descServicoSelecionado: string = '';
  precoServicoSelecionado: string = '';
  
  ListarServicos: ServicosDTO[] = [];

  carregando: boolean = false;

  constructor(private AuthGerenciadorServicoService: AuthGerenciadorServicos, 
    private router: Router, 
    private cdr: ChangeDetectorRef, 
    private authNotificacaoService: AuthNotificacaoService) {}

  inativando: Set<string> = new Set();

  get podeSalvar(): boolean {
    return (
      !!this.servicosIdSelecionado &&
      this.descServicoSelecionado.trim().length > 0 &&
      this.precoServicoSelecionado.trim().length > 0
    );
  }
  
  private readonly destroyRef = inject(DestroyRef);
  
  ngOnInit(): void {
    this.carregarListaServicos();
  }

  carregarListaServicos(): void {
    this.AuthGerenciadorServicoService
      .listarServicos()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (dados) => {
          this.ListarServicos = dados;
          this.cdr.detectChanges();
        },
        error: (erro) => {
          console.error('Erro ao buscar os servicos', erro);
          this.ListarServicos = []; // evita manter dados desatualizados na tela em caso de erro
          this.cdr.detectChanges();
        },
      });
  }

    selecionarServicos(servico: ServicosDTO): void {
      this.servicosIdSelecionado = servico.servicosId;
      this.descServicoSelecionado = servico.descServico;
      this.precoServicoSelecionado = servico.precoServico;
    }

  inativarServicos(servicos: ServicosDTO, event: Event): void {
    event.stopPropagation();

    if (this.inativando.has(servicos.servicosId)) {
      return;
    }

    const confirmacao = confirm(
      `Tem certeza que quer inativar essa forma de pagamento ${servicos.descServico}?`
    );

    if (!confirmacao) {
      return;
    }

    this.inativando.add(servicos.servicosId);

    const usuarioAtual = localStorage.getItem('usuarioLogado') || 'Sistema';

    this.AuthGerenciadorServicoService
      .inativarServicos(servicos.servicosId, usuarioAtual)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
          next: (resposta) => {
            this.authNotificacaoService.sucesso(resposta?.mensagem || `O serviço ${servicos.servicosId} inativada com sucesso!`);
            this.inativando.delete(servicos.servicosId);
            this.cdr.detectChanges();
            // dá tempo do back-end concluir antes de recarregar a lista
            setTimeout(() => {
              this.cdr.detectChanges();
            }, 300);
            setTimeout(() => {
              this.carregarListaServicos();
            }, 300);
          },
        error: (erro: HttpErrorResponse) => {
          const mensagem = this.authNotificacaoService.extrairMensagemErro(erro, 'Não foi possível inativar');
          this.authNotificacaoService.erro(mensagem);
          this.inativando.delete(servicos.servicosId);
          this.cdr.detectChanges();
        },
      });
  }

  isInativando(servicos: ServicosDTO): boolean {
    return this.inativando.has(servicos.servicosId);
  }

  executarGerenciadorServicos(): void {
    if (this.carregando) {
      return;
    }
    if (!this.podeSalvar) {
      this.authNotificacaoService.erro('Selecione um serviço na lista e preencha os campos obrigatórios antes de salvar.');
      return;
    }
    this.carregando = true;

    const usuarioAtual = localStorage.getItem('usuarioLogado') || 'Sistema';

    this.AuthGerenciadorServicoService.atualizarServicos(
      this.servicosIdSelecionado, this.descServicoSelecionado, this.precoServicoSelecionado, usuarioAtual).subscribe({
      next: (resposta) => {
        this.carregando = false;
        this.cdr.detectChanges();
        this.authNotificacaoService.sucesso(resposta?.mensagem || 'Atualização de servço realizada com sucesso');
        setTimeout(() => {
          this.carregarListaServicos();
        }, 300);
      },
      error: (erro: HttpErrorResponse) => {
        this.carregando = false;
        this.cdr.detectChanges();
        const mensagem = this.authNotificacaoService.extrairMensagemErro(erro, 'Atualização de serviço não foi possível');
        this.authNotificacaoService.erro(mensagem);
      }
    });
  }
}
