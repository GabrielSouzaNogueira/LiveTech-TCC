import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { AuthGerenciadorUsuario } from '../../services/auth-gerenciador-usuario';
import { CommonModule } from '@angular/common';
import { UserSelectDTO } from '../../models/user-select.dto';
import { NgxMaskDirective } from 'ngx-mask';
import { AuthNotificacaoService } from '../../services/auth-notificacao';

@Component({
  selector: 'app-gerenciador-usuario',
  standalone: true,
  imports: [FormsModule, CommonModule, NgxMaskDirective],
  templateUrl: './gerenciador-usuario.html',
  styleUrl: './gerenciador-usuario.css',
})
export class GerenciadorUsuario implements OnInit {
  listaUsuariosGeral: UserSelectDTO[] = [];
  listaUsuariosFiltrada: UserSelectDTO[] = [];

  termoBusca: string = '';
  filtroStatus: string = 'TODOS';
  carregandoLista: boolean = false;

  // Controle do Modal
  usuarioEmEdicao: UserSelectDTO | null = null;
  senhaDigitada: string = ''; // Mantemos a senha separada pois ela não vem da busca
  modalAberto: boolean = false;
  salvando: boolean = false;

  constructor(
    private AuthGerenciadorUsuarioService: AuthGerenciadorUsuario,
    private cdr: ChangeDetectorRef,
    private AuthNotificacaoService: AuthNotificacaoService
  ) {}

  ngOnInit(): void {
    this.carregarListaUsuarios();
  }

  carregarListaUsuarios(): void {
    this.carregandoLista = true;
    this.AuthGerenciadorUsuarioService.listarUsuarios().subscribe({
      next: (dados) => {
        this.listaUsuariosGeral = dados;
        this.aplicarFiltros();
        this.carregandoLista = false;
        this.cdr.detectChanges();
      },
      error: (erro) => {
        console.error('Erro ao buscar usuários', erro);
        this.carregandoLista = false;
      }
    });
  }

  aplicarFiltros(): void {
    let lista = this.listaUsuariosGeral;

    if (this.filtroStatus !== 'TODOS') {
      lista = lista.filter(u => u.status === this.filtroStatus);
    }

    if (this.termoBusca) {
      const termo = this.termoBusca.toLowerCase();
      lista = lista.filter(u => 
        u.nome.toLowerCase().includes(termo) || 
        (u.email && u.email.toLowerCase().includes(termo))
      );
    }

    this.listaUsuariosFiltrada = lista;
  }

  abrirModalEdicao(usuario: UserSelectDTO): void {
    this.usuarioEmEdicao = { ...usuario };
    // Ajusta o cargo para corresponder aos values do select (maiúsculas)
    if (this.usuarioEmEdicao.cargo) {
      this.usuarioEmEdicao.cargo = this.usuarioEmEdicao.cargo.toUpperCase();
    }
    this.senhaDigitada = ''; // Limpa a senha ao abrir
    this.modalAberto = true;
  }

  fecharModal(): void {
    this.modalAberto = false;
    this.usuarioEmEdicao = null;
    this.senhaDigitada = '';
  }

  inativarUsuario(usuario: UserSelectDTO, event: Event): void {
    event.stopPropagation();
    const confirmacao = confirm(`Tem certeza que deseja inativar o usuário ${usuario.nome}?`);
    if (!confirmacao) return;

    const usuarioAtual = localStorage.getItem('usuarioLogado') || 'Sistema';
    this.AuthGerenciadorUsuarioService.inativarUsuario(usuario.userId, usuarioAtual).subscribe({
      next: (resposta) => {
        this.AuthNotificacaoService.sucesso(resposta?.mensagem || `Usuário inativado!`);
        setTimeout(() => this.carregarListaUsuarios(), 300);
      },
      error: (erro: HttpErrorResponse) => {
        const mensagem = this.AuthNotificacaoService.extrairMensagemErro(erro, 'Erro ao inativar usuário.');
        this.AuthNotificacaoService.erro(mensagem);
      }
    });
  }

  executarGerenciadorUsuario(): void {
    if (!this.usuarioEmEdicao || this.salvando) return;
    this.salvando = true;

    const usuarioAtual = localStorage.getItem('usuarioLogado') || 'Sistema';

    this.AuthGerenciadorUsuarioService.atualizarUsuario(
      this.usuarioEmEdicao.userId, 
      this.usuarioEmEdicao.nome, 
      this.senhaDigitada, 
      this.usuarioEmEdicao.email, 
      this.usuarioEmEdicao.telefone,
      this.usuarioEmEdicao.cargo, 
      usuarioAtual
    ).subscribe({
      next: (resposta) => {
        this.salvando = false;
        this.AuthNotificacaoService.sucesso(resposta?.mensagem || 'Usuário atualizado!');
        this.fecharModal();
        setTimeout(() => this.carregarListaUsuarios(), 300);
      },
      error: (erro: HttpErrorResponse) => {
        this.salvando = false;
        const msg = this.AuthNotificacaoService.extrairMensagemErro(erro, 'Erro ao atualizar.');
        this.AuthNotificacaoService.erro(msg);
      }
    });
  }
}