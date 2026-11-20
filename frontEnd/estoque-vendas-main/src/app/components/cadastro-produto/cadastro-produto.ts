import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { AuthCadastroProduto } from '../../services/auth-cadastro-produto';
import { GerenciadorProduto } from '../gerenciador-produto/gerenciador-produto';
import { AuthNotificacaoService } from '../../services/auth-notificacao';

@Component({
  selector: 'app-cadastro-produto',
  imports: [FormsModule, CommonModule, GerenciadorProduto],
  templateUrl: './cadastro-produto.html',
  styleUrl: './cadastro-produto.css',
})
export class CadastroProduto {
  abaAtiva: 'cadastrar' | 'atualizar' = 'cadastrar';

  nomeDigitado: string = '';
  marcaDigitada: string = '';
  codBarraDigitada: string = '';
  quantidadeDigitada: number = 0;
  precoCustoDigitado: number = 0;
  precoVendaDigitado: number = 0;

  carregando: boolean = false;

  alternarAba(aba: 'cadastrar' | 'atualizar'): void {
    this.abaAtiva = aba;
  }

  constructor(
    private cadastroProdutoService: AuthCadastroProduto,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private AuthNotificacaoService: AuthNotificacaoService
  ) {}

  executarCadastroProduto(): void {
    if (this.carregando) {
      return;
    }
    this.carregando = true;

    const usuarioAtual = localStorage.getItem('usuarioLogado') || 'Sistema';

    this.cadastroProdutoService
      .cadastroProduto(
        this.nomeDigitado,
        this.marcaDigitada,
        this.codBarraDigitada,
        this.quantidadeDigitada,
        this.precoCustoDigitado,
        this.precoVendaDigitado,
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
          const mensagem = this.AuthNotificacaoService.extrairMensagemErro(erro, 'Cadastro do produto não foi possível');
          this.AuthNotificacaoService.erro(mensagem);
        }
      });
  }

  limparCampos(): void {
    this.nomeDigitado = '';
    this.marcaDigitada = '';
    this.codBarraDigitada = '';
    this.quantidadeDigitada = 0;
    this.precoCustoDigitado = 0;
    this.precoVendaDigitado = 0;
  }
}