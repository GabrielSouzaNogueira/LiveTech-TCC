import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgxMaskDirective } from 'ngx-mask';
import { AuthCadastroCliente } from '../../services/auth-cadastro-cliente';
import { Route, Router } from '@angular/router';
import { AuthNotificacaoService } from '../../services/auth-notificacao';
import { HttpErrorResponse } from '@angular/common/http';
import { GerenciadorCliente } from "../gerenciador-cliente/gerenciador-cliente";

@Component({
  selector: 'app-cadastro-cliente',
  imports: [FormsModule, CommonModule, NgxMaskDirective, GerenciadorCliente],
  templateUrl: './cadastro-cliente.html',
  styleUrl: './cadastro-cliente.css',
})
export class CadastroCliente {
  abaAtiva: 'cadastrar' | 'atualizar' = 'cadastrar';

  nomeDigitado: string = '';
  sobrenomeDigitado: string = '';
  emailDigitado: string = '';
  telefoneDigitado: string = '';
  
  carregando: boolean = false;

  alternarAba(aba: 'cadastrar' | 'atualizar'): void {
    this.abaAtiva = aba;
  }

  constructor(
    private authCadastroClienteService: AuthCadastroCliente,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private AuthNotificacaoService: AuthNotificacaoService
  ){}

  executarCadastroCliente(): void {
    if (this.carregando) {
      return;
    }
    this.carregando = true;

    const usuarioAtual = localStorage.getItem('usuarioLogado') || 'Sistema';

    this.authCadastroClienteService.cadastroCliente(this.nomeDigitado, this.sobrenomeDigitado, this.emailDigitado, this.telefoneDigitado, usuarioAtual).subscribe({
        next: (resposta) => {
          this.carregando = false;
          this.cdr.detectChanges();
          this.AuthNotificacaoService.sucesso(resposta?.mensagem || 'Cadastro realizado com sucesso');
        },
        error: (erro: HttpErrorResponse) => {
          this.carregando = false;
          this.cdr.detectChanges();
          const mensagem = this.AuthNotificacaoService.extrairMensagemErro(erro, 'Cadastro não foi possível');
          this.AuthNotificacaoService.erro(mensagem);
        }
    })
  }
}
