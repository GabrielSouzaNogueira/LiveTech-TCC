import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { NgxMaskDirective } from 'ngx-mask';
import { ClienteSelectDTO } from '../../models/cliente-select-dto';
import { AuthGerenciadorCliente } from '../../services/auth-gerenciador-cliente';
import { AuthNotificacaoService } from '../../services/auth-notificacao';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-gerenciador-cliente',
  imports: [FormsModule, CommonModule, NgxMaskDirective],
  templateUrl: './gerenciador-cliente.html',
  styleUrl: './gerenciador-cliente.css',
  standalone: true
})
export class GerenciadorCliente implements OnInit {
  listaClientesGeral: ClienteSelectDTO[] = [];
  listaClientesFiltrada: ClienteSelectDTO[] = [];

  termoBusca: string = '';
  carregandoLista: boolean = false;

  // Controle do Modal
  clienteEmEdicao: ClienteSelectDTO | null = null;
  modalAberto: boolean = false;
  salvando: boolean = false;

  constructor(
    private AuthGerenciadorClienteService: AuthGerenciadorCliente,
    private cdr: ChangeDetectorRef,
    private AuthNotificacaoService: AuthNotificacaoService
  ) {}

  ngOnInit(): void {
    this.carregarListaClientes();
  }

  carregarListaClientes(): void {
    this.carregandoLista = true;
    this.AuthGerenciadorClienteService.listarClientes().subscribe({
      next: (dados) => {
        this.listaClientesGeral = dados;
        this.aplicarFiltros();
        this.carregandoLista = false;
        this.cdr.detectChanges();
      },
      error: (erro) => {
        console.error('Erro ao buscar clientes', erro);
        this.carregandoLista = false;
      }
    });
  }

  aplicarFiltros(): void {
    let lista = this.listaClientesGeral;

    if (this.termoBusca) {
      const termo = this.termoBusca.toLowerCase();
      lista = lista.filter(c => 
        c.nome.toLowerCase().includes(termo) || 
        (c.sobrenome && c.sobrenome.toLowerCase().includes(termo)) ||
        (c.email && c.email.toLowerCase().includes(termo))
      );
    }
    this.listaClientesFiltrada = lista;
  }

  abrirModalEdicao(cliente: ClienteSelectDTO): void {
    this.clienteEmEdicao = { ...cliente };
    this.modalAberto = true;
  }

  fecharModal(): void {
    this.modalAberto = false;
    this.clienteEmEdicao = null;
  }

  inativarCliente(cliente: ClienteSelectDTO, event: Event): void {
    event.stopPropagation();
    const confirmacao = confirm(`Tem certeza que deseja inativar o cliente ${cliente.nome}?`);
    if (!confirmacao) return;

    const usuarioAtual = localStorage.getItem('usuarioLogado') || 'Sistema';
    this.AuthGerenciadorClienteService.inativarCliente(cliente.id, usuarioAtual).subscribe({
      next: (resposta) => {
        this.AuthNotificacaoService.sucesso(resposta?.mensagem || `Cliente inativado com sucesso!`);
        setTimeout(() => this.carregarListaClientes(), 300);
      },
      error: (erro: HttpErrorResponse) => {
        const mensagem = this.AuthNotificacaoService.extrairMensagemErro(erro, 'Erro ao inativar cliente.');
        this.AuthNotificacaoService.erro(mensagem);
      }
    });
  }

  executarGerenciadorCliente(): void {
    if (!this.clienteEmEdicao || this.salvando) return;
    this.salvando = true;

    const usuarioAtual = localStorage.getItem('usuarioLogado') || 'Sistema';

    this.AuthGerenciadorClienteService.atualizarCliente(
      this.clienteEmEdicao.id, 
      this.clienteEmEdicao.nome, 
      this.clienteEmEdicao.sobrenome, 
      this.clienteEmEdicao.email, 
      this.clienteEmEdicao.telefone, 
      usuarioAtual
    ).subscribe({
      next: (resposta) => {
        this.salvando = false;
        this.AuthNotificacaoService.sucesso(resposta?.mensagem || 'Cliente atualizado com sucesso!');
        this.fecharModal();
        setTimeout(() => this.carregarListaClientes(), 300);
      },
      error: (erro: HttpErrorResponse) => {
        this.salvando = false;
        const msg = this.AuthNotificacaoService.extrairMensagemErro(erro, 'Erro ao atualizar cliente.');
        this.AuthNotificacaoService.erro(msg);
      }
    });
  }
}