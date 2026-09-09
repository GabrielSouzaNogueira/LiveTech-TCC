import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ServicosDTO } from '../../models/servicos-dto';
import { AuthServicos } from '../../services/auth-servicos';
import { AuthNotificacaoService } from '../../services/auth-notificacao';
import { HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { GerenciadorServicos } from "../gerenciador-servicos/gerenciador-servicos";

@Component({
  selector: 'app-servicos',
  imports: [FormsModule, CommonModule, GerenciadorServicos],
  templateUrl: './servicos.html',
  styleUrl: './servicos.css',
})
export class Servicos {
  abaAtiva: 'cadastrar' | 'atualizar' = 'cadastrar';

  descServicoDigitado: string = '';
  precoServicoDigitado: string = '';

  carregando: boolean = false;

  alternarAba(aba: 'cadastrar' | 'atualizar'): void {
    this.abaAtiva = aba;
  }

  constructor(
    private authServicosService: AuthServicos,
    private router: Router,
    private cdr: ChangeDetectorRef,
    private authNotificacaoService: AuthNotificacaoService
  ) {}

  executarCadastroServicos(): void {
    if (this.carregando) return;

    const descricao = this.descServicoDigitado.trim();
    if (!descricao) return;

    // Converte vírgula para ponto e transforma em número
    let precoFormatado = this.precoServicoDigitado.replace(',', '.');
    
    // Validação extra opcional
    if (isNaN(Number(precoFormatado))) {
        this.authNotificacaoService.erro('Por favor, digite um preço válido.');
        return;
    }

    this.carregando = true;
    const usuarioAtual = localStorage.getItem('usuarioLogado') || 'Sistema';

    this.authServicosService
      .cadastroServicos(descricao, precoFormatado, usuarioAtual)
      .subscribe({
          next: (resposta) => {
          this.carregando = false;
          this.cdr.detectChanges();
          this.authNotificacaoService.sucesso(resposta?.mensagem || 'Cadastro realizado com sucesso');
        },
        error: (erro: HttpErrorResponse) => {
          this.carregando = false;
          this.cdr.detectChanges();
          const mensagem = this.authNotificacaoService.extrairMensagemErro(erro, 'Cadastro não foi possível');
          this.authNotificacaoService.erro(mensagem);
        }
      })
  }
  trackByDescricao(_index: number, item: ServicosDTO): string {
    return item.descServico;
  }
}
