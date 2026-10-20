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
  standalone: true
})
export class GerenciadorServicos implements OnInit {
  listaServicosGeral: ServicosDTO[] = [];
  listaServicosFiltrada: ServicosDTO[] = [];

  termoBusca: string = '';
  filtroStatus: string = 'TODOS';
  carregando: boolean = false;

  // Controle do Modal
  servicoEmEdicao: ServicosDTO | null = null;
  modalAberto: boolean = false;
  salvando: boolean = false;
  inativando: Set<string> = new Set();

  private readonly destroyRef = inject(DestroyRef);

  constructor(
    private AuthGerenciadorServicoService: AuthGerenciadorServicos, 
    private cdr: ChangeDetectorRef, 
    private authNotificacaoService: AuthNotificacaoService
  ) {}

  ngOnInit(): void {
    this.carregarListaServicos();
  }

  carregarListaServicos(): void {
    this.carregando = true;
    this.AuthGerenciadorServicoService.listarServicos()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (dados) => {
          this.listaServicosGeral = dados;
          this.aplicarFiltros();
          this.carregando = false;
          this.cdr.detectChanges();
        },
        error: (erro) => {
          console.error('Erro ao buscar serviços', erro);
          this.carregando = false;
          this.cdr.detectChanges();
        }
      });
  }

  aplicarFiltros(): void {
    let lista = this.listaServicosGeral;
    if (this.filtroStatus !== 'TODOS') {
      lista = lista.filter(s => s.status === this.filtroStatus);
    }
    if (this.termoBusca) {
      const termo = this.termoBusca.toLowerCase();
      lista = lista.filter(s => s.descServico.toLowerCase().includes(termo));
    }
    this.listaServicosFiltrada = lista;
  }

  abrirModalEdicao(servico: ServicosDTO): void {
    this.servicoEmEdicao = { ...servico };
    this.modalAberto = true;
  }

  fecharModal(): void {
    this.modalAberto = false;
    this.servicoEmEdicao = null;
  }

  inativarServicos(servico: ServicosDTO, event: Event): void {
    event.stopPropagation();
    if (this.inativando.has(servico.servicosId)) return;

    const confirmacao = confirm(`Deseja inativar o serviço: ${servico.descServico}?`);
    if (!confirmacao) return;

    this.inativando.add(servico.servicosId);
    const usuarioAtual = localStorage.getItem('usuarioLogado') || 'Sistema';

    this.AuthGerenciadorServicoService.inativarServicos(servico.servicosId, usuarioAtual)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (resposta) => {
          this.authNotificacaoService.sucesso(resposta?.mensagem || 'Serviço inativado!');
          this.inativando.delete(servico.servicosId);
          setTimeout(() => this.carregarListaServicos(), 300);
        },
        error: (erro: HttpErrorResponse) => {
          const msg = this.authNotificacaoService.extrairMensagemErro(erro, 'Erro ao inativar.');
          this.authNotificacaoService.erro(msg);
          this.inativando.delete(servico.servicosId);
        }
      });
  }

  isInativando(servico: ServicosDTO): boolean {
    return this.inativando.has(servico.servicosId);
  }

  executarGerenciadorServicos(): void {
    if (!this.servicoEmEdicao || this.salvando) return;
    this.salvando = true;

    const usuarioAtual = localStorage.getItem('usuarioLogado') || 'Sistema';

    this.AuthGerenciadorServicoService.atualizarServicos(
      this.servicoEmEdicao.servicosId, 
      this.servicoEmEdicao.descServico, 
      this.servicoEmEdicao.precoServico, 
      usuarioAtual
    ).subscribe({
      next: (resposta) => {
        this.salvando = false;
        this.authNotificacaoService.sucesso(resposta?.mensagem || 'Serviço atualizado com sucesso!');
        this.fecharModal();
        setTimeout(() => this.carregarListaServicos(), 300);
      },
      error: (erro: HttpErrorResponse) => {
        this.salvando = false;
        const msg = this.authNotificacaoService.extrairMensagemErro(erro, 'Erro ao atualizar.');
        this.authNotificacaoService.erro(msg);
      }
    });
  }
}