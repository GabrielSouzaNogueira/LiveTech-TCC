import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthGerenciadorProduto } from '../../services/auth-gerenciador-produto';
import { SelectAllProdDTO } from '../../models/select-all-prod-dto';
import { HttpErrorResponse } from '@angular/common/http';
// Importe o seu serviço de notificação aqui, se o tiver, ou use alerts temporários

@Component({
  selector: 'app-gerenciador-produto',
  templateUrl: './gerenciador-produto.html',
  styleUrl: './gerenciador-produto.css',
  imports: [CommonModule, FormsModule],
  standalone: true
})
export class GerenciadorProduto implements OnInit {
  // Variáveis para a Tabela e Filtros
  listaProdutosGeral: SelectAllProdDTO[] = [];
  listaProdutosFiltrada: SelectAllProdDTO[] = [];
  
  termoBuscaNome: string = '';
  filtroStatus: string = 'TODOS';
  carregandoLista: boolean = false;

  // Variáveis para o Modal Flutuante de Edição
  produtoEmEdicao: SelectAllProdDTO | null = null;
  modalAberto: boolean = false;
  salvando: boolean = false;

  constructor(
    private authProduto: AuthGerenciadorProduto,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.carregarProdutos();
  }

  carregarProdutos(): void {
    this.carregandoLista = true;
    this.authProduto.listarProdutos().subscribe({
      next: (dados: SelectAllProdDTO[]) => {
        this.listaProdutosGeral = dados;
        this.aplicarFiltros(); // Carrega a tabela assim que os dados chegam
        this.carregandoLista = false;
        this.cdr.detectChanges();
      },
      error: (e: any) => {
        console.error('Erro ao buscar produtos', e);
        this.carregandoLista = false;
      }
    });
  }

  // Função disparada ao digitar na barra de pesquisa ou mudar o dropdown
  aplicarFiltros(): void {
    let lista = this.listaProdutosGeral;

    if (this.filtroStatus !== 'TODOS') {
      lista = lista.filter(p => p.prodStatus === this.filtroStatus);
    }

    if (this.termoBuscaNome) {
      const termo = this.termoBuscaNome.toLowerCase();
      lista = lista.filter(p => 
        p.nome.toLowerCase().includes(termo) || 
        (p.codBarra && p.codBarra.includes(termo))
      );
    }

    this.listaProdutosFiltrada = lista;
  }

  // --- LÓGICA DO MODAL ---

  abrirModalEdicao(produto: SelectAllProdDTO): void {
    // Clonamos o objeto para que as alterações não reflitam na tabela de trás 
    // antes de clicarmos em "Salvar"
    this.produtoEmEdicao = { ...produto }; 
    this.modalAberto = true;
  }

  fecharModal(): void {
    this.modalAberto = false;
    this.produtoEmEdicao = null;
  }

  salvarAlteracoes(): void {
    if (!this.produtoEmEdicao || this.salvando) return;
    this.salvando = true;

    const usuarioAtual = localStorage.getItem('usuarioLogado') || 'Sistema';

    this.authProduto.atualizarProduto(
      this.produtoEmEdicao.prodId,
      this.produtoEmEdicao.nome,
      this.produtoEmEdicao.marca,
      this.produtoEmEdicao.codBarra,
      this.produtoEmEdicao.quantidade,
      this.produtoEmEdicao.precoCusto,
      this.produtoEmEdicao.precoVenda,
      usuarioAtual
    ).subscribe({
      next: (resposta: any) => {
        alert(resposta?.mensagem || 'Produto atualizado com sucesso!');
        this.fecharModal();
        this.carregarProdutos(); // Recarrega a tabela para ver a alteração
        this.salvando = false;
      },
      error: (erro: HttpErrorResponse) => {
        console.error(erro);
        alert('Erro ao atualizar produto.');
        this.salvando = false;
      }
    });
  }

  inativar(produto: SelectAllProdDTO, event: Event): void {
    event.stopPropagation(); // Evita que o Modal abra ao clicar em Inativar
    
    const confirmacao = confirm(`Deseja inativar o produto: ${produto.nome}?`);
    if (!confirmacao) return;

    const usuarioAtual = localStorage.getItem('usuarioLogado') || 'Sistema';
    
    this.authProduto.inativarProduto(produto.prodId, usuarioAtual).subscribe({
      next: () => {
        alert('Produto inativado com sucesso!');
        this.carregarProdutos(); // Recarrega a tabela para ver o novo status
      },
      error: (e: any) => {
        console.error(e);
        alert('Erro ao inativar produto.');
      }
    });
  }
}