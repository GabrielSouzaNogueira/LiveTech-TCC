import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { cadastroUsuario } from "../../services/cadastro-usuario";
import { CommonModule } from '@angular/common';
import { GerenciadorUsuario } from '../gerenciador-usuario/gerenciador-usuario';
import { NgxMaskDirective } from 'ngx-mask';
import { AuthNotificacaoService } from '../../services/auth-notificacao';

@Component({
  selector: 'app-cadastro-usuario',
  standalone: true,
  imports: [FormsModule, CommonModule, GerenciadorUsuario, NgxMaskDirective],
  templateUrl: './cadastro-usuario.html',
  styleUrl: './cadastro-usuario.css',
})
export class CadastroUsuario {
  abaAtiva: 'cadastrar' | 'atualizar' = 'cadastrar';

  nomeDigitado: string = '';
  senhaDigitada: string = '';
  emailDigitado: string = '';
  telefoneDigitado: string = '';
  cargoDigitado: string = '';

  carregando: boolean = false;

  alternarAba(aba: 'cadastrar' | 'atualizar'): void {
    this.abaAtiva = aba;
  }

  constructor(
    private cadastroUsuarioService: cadastroUsuario,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private AuthNotificacaoService: AuthNotificacaoService
  ) {}

  executarCadastroUsuario(): void {
    if (this.carregando) {
      return;
    }
    this.carregando = true;

    const usuarioAtual = localStorage.getItem('usuarioLogado') || 'Sistema';

    this.cadastroUsuarioService
      .cadastroUsuario(
        this.nomeDigitado,
        this.senhaDigitada,
        this.emailDigitado,
        this.telefoneDigitado,
        this.cargoDigitado,
        usuarioAtual
      )
      .subscribe({
        next: (resposta) => {
          this.carregando = false;
          this.limparCampos();
          this.cdr.detectChanges();
          this.AuthNotificacaoService.sucesso(resposta?.mensagem || 'Cadastro realizado com sucesso');
        },
        error: (erro: HttpErrorResponse) => {
          this.carregando = false;
          this.cdr.detectChanges();
          const mensagem = this.AuthNotificacaoService.extrairMensagemErro(erro, 'Cadastro não foi possível');
          this.AuthNotificacaoService.erro(mensagem);
        }
      });
  }
  
  limparCampos(): void {
    this.nomeDigitado = '';
    this.senhaDigitada = '';
    this.emailDigitado = '';
    this.telefoneDigitado = '';
    this.cargoDigitado = '';
  }
}