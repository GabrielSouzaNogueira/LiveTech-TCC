import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RelatorioUsuarioService } from '../../../services/auth-relatorio-usuario';
import { UsuariosInativadosDTO } from '../../../models/relatorio-usuarios-dto';

@Component({
  selector: 'app-relatorio-usuarios',
  templateUrl: './relatorio-usuarios.html',
  styleUrls: ['./relatorio-usuarios.css'],
  standalone: true,
  imports: [CommonModule]
})
export class RelatorioUsuariosComponent implements OnInit {
  listaInativos: UsuariosInativadosDTO[] = [];
  carregando: boolean = false;
  erroMsg: string = '';

  constructor(private relatorioService: RelatorioUsuarioService) {}

  ngOnInit(): void {
    this.carregarRelatorio();
  }

  carregarRelatorio(): void {
    this.carregando = true;
    this.relatorioService.listarUsuariosInativos().subscribe({
      next: (dados) => {
        this.listaInativos = dados;
        this.carregando = false;
      },
      error: (erro) => {
        console.error('Erro ao buscar relatório:', erro);
        this.erroMsg = 'Você não tem permissão ou ocorreu um erro ao carregar os dados.';
        this.carregando = false;
      }
    });
  }

  imprimirTabela(): void {
    window.print();
  }
}