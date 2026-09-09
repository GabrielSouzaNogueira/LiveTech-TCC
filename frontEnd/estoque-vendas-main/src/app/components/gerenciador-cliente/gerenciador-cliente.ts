import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgxMaskDirective } from 'ngx-mask';
import { ClienteSelectDTO } from '../../models/cliente-select-dto';
import { AuthGerenciadorCliente } from '../../services/auth-gerenciador-cliente';
import { AuthNotificacaoService } from '../../services/auth-notificacao';
import { Router } from '@angular/router';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-gerenciador-cliente',
  imports: [FormsModule, CommonModule, NgxMaskDirective],
  templateUrl: './gerenciador-cliente.html',
  styleUrl: './gerenciador-cliente.css',
})
export class GerenciadorCliente implements OnInit{

  idDigitado: string = '';
  nomeDigitado: string = '';
  sobrenomeDigitado: string = '';
  emailDigitado: string = '';
  telefoneDigitado: string = '';

  listarClientes: ClienteSelectDTO[] = [];

  carregando: boolean = false;

  constructor(
    private AuthGerenciadorClienteService: AuthGerenciadorCliente,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private AuthNotificacaoService: AuthNotificacaoService
  ) {}

  get podeSalvar(): boolean {
    return(
      !!this.idDigitado &&
      this.nomeDigitado.trim().length > 0
    )
  }

  ngOnInit(): void {
    this.carregarListaClientes();
  }

  carregarListaClientes(): void {
    this.AuthGerenciadorClienteService.listarClientes().subscribe({
      next: (dados) => {
        this.listarClientes = dados;
        this.cdr.detectChanges();
      },
      error: (erro) => console.error('Erro ao buscar clientes', erro)
    });
  }

  selecionarCliente(cliente: ClienteSelectDTO): void {
    this.idDigitado = cliente.id;
    this.nomeDigitado = cliente.nome;
    this.sobrenomeDigitado = cliente.sobrenome;
    this.emailDigitado = cliente.email;
    this.telefoneDigitado = cliente.telefone || '';
  }

  inativarCliente(cliente: ClienteSelectDTO, event: Event): void {
    event.stopPropagation();

    const confirmacao = confirm(`Tem certeza que deseja inativar o cliente ${cliente.nome}?`);

    if (!confirmacao) {
      return;
    }

    const usuarioAtual = localStorage.getItem('usuarioLogado') || 'Sistema';
    this.AuthGerenciadorClienteService.inativarCliente(cliente.id, usuarioAtual).subscribe({
      next: (resposta) => {
        this.AuthNotificacaoService.sucesso(resposta?.mensagem || `Cliente ${cliente.nome} inativado com sucesso!`);
          setTimeout(() => {
          this.carregarListaClientes();
        }, 300);
      },
      error: (erro: HttpErrorResponse) => {
        const mensagem = this.AuthNotificacaoService.extrairMensagemErro(erro, 'Não foi possível inativar o cliente.');
        this.AuthNotificacaoService.erro(mensagem);
      }
    });
  }

  executarGerenciadorCliente(): void {
    if (this.carregando) {
      return;
    }
    if (!this.podeSalvar) {
      this.AuthNotificacaoService.erro('Selecione um usuário na lista e preencha os campos obrigatórios antes de salvar.');
      return;
    }
    this.carregando = true;

    const usuarioAtual = localStorage.getItem('usuarioLogado') || 'Sistema';

    this.AuthGerenciadorClienteService.atualizarCliente(
      this.idDigitado, this.nomeDigitado, this.sobrenomeDigitado, this.emailDigitado, this.telefoneDigitado, usuarioAtual
    ).subscribe({
        next: (resposta) => {
        this.carregando = false;
        this.cdr.detectChanges();
        this.AuthNotificacaoService.sucesso(resposta?.mensagem || 'Atualização realizada com sucesso');
        setTimeout(() => {
          this.carregarListaClientes();
        }, 300);
      },
      error: (erro: HttpErrorResponse) => {
        this.carregando = false;
        this.cdr.detectChanges();
        const mensagem = this.AuthNotificacaoService.extrairMensagemErro(erro, 'Atualização de usuário não foi possível');
        this.AuthNotificacaoService.erro(mensagem);
      }
    });
  }
}
