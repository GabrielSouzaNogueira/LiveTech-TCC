import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { movimentacaoDTO } from '../../models/movimentacaoDTO'; 
import { AuthMovimentacao } from '../../services/auth-movimentacao';

@Component({
  selector: 'app-movimentacao',
  standalone: true,
  imports: [FormsModule, CommonModule],
  templateUrl: './movimentacao.html',
  styleUrl: './movimentacao.css',
})
export class Movimentacao implements OnInit {
  carregando: boolean = false;
  buscaRealizada: boolean = false;

  listaMovimentacoes: movimentacaoDTO[] = [];
  movimentacoesExibidas: movimentacaoDTO[] = [];

  dataInicio: string = '';       
  dataFim: string = ''; 
  filtroTipo: string = '';              
  filtroAcao: string = '';       
  filtroRegistro: string = '';        
  filtroResponsavel: string = ''; 

  constructor(
    private authMovimentacaoService: AuthMovimentacao,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {}

  limparFiltro(): void {
    this.dataInicio = '';
    this.dataFim = '';
    this.filtroTipo = '';         
    this.filtroAcao = '';
    this.filtroRegistro = '';     
    this.filtroResponsavel = '';

    this.buscaRealizada = false;
    this.listaMovimentacoes = [];
    this.movimentacoesExibidas = [];
  }

  buscarMovimentacoes(): void {
    this.carregando = true;

    const filtros: any = {};
    if (this.filtroTipo) filtros.tipo = this.filtroTipo;                    
    if (this.dataInicio) filtros.dataInicio = this.dataInicio;
    if (this.dataFim) filtros.dataFim = this.dataFim;
    if (this.filtroAcao) filtros.acao = this.filtroAcao;
    if (this.filtroRegistro) filtros.registroAfetado = this.filtroRegistro; 
    if (this.filtroResponsavel) filtros.responsavel = this.filtroResponsavel;

    this.authMovimentacaoService.filtrarMovimentacoes(filtros).subscribe({
      next: (dados) => {
        this.listaMovimentacoes = dados;
        this.movimentacoesExibidas = dados; 
        this.buscaRealizada = true;
        this.carregando = false;
        this.cdr.detectChanges();
      },
      error: (erro) => {
        console.error('Erro ao buscar movimentações:', erro);
        this.movimentacoesExibidas = [];
        this.buscaRealizada = true;
        this.carregando = false;
        this.cdr.detectChanges();
      }
    });
  }

  formatarModulo(tipo: string): string {
    const mapa: Record<string, string> = {
      PRODUTO: 'Produto',
      USUARIO: 'Usuário',
      CLIENTE: 'Cliente',
      SERVICO: 'Serviço',
      FORMA_PAGAMENTO: 'Forma de Pagamento',
      EQUIPAMENTO: 'Equipamento',
      OS: 'Ordem de Serviço',
      PEDIDO: 'Pedido',
    };
    return mapa[tipo] || tipo;
  }

  imprimirMovimentacoes(): void {
    if (this.movimentacoesExibidas.length === 0) {
      alert('Não há movimentações para imprimir.');
      return;
    }

    const janela = window.open('', '', 'width=1050,height=700');
    if (!janela) {
      alert('A impressão foi bloqueada pelo navegador. Permita os pop-ups para este site.');
      return;
    }

    const dataEmissao = new Date().toLocaleString('pt-BR');

    const periodo = (this.dataInicio || this.dataFim)
      ? `${this.dataInicio ? this.dataInicio.split('-').reverse().join('/') : 'Início'} até ${this.dataFim ? this.dataFim.split('-').reverse().join('/') : 'Hoje'}`
      : 'Todo o período';

    const moduloLabel = this.filtroTipo ? this.formatarModulo(this.filtroTipo) : 'Todos';
    const acaoLabel = this.filtroAcao || 'Todas';
    const registroLabel = this.filtroRegistro || 'Todos';
    const responsavelLabel = this.filtroResponsavel || 'Todos';

    const linhasHtml = this.movimentacoesExibidas.map(mov => {
      const idFormatado = mov.tipoEntidade === 'USUARIO' && mov.registroAfetadoId
        ? `${String(mov.registroAfetadoId).slice(0, 8)}...`
        : (mov.registroAfetadoId || 'N/A');

      const dataFormatada = mov.dataMov
        ? new Date(mov.dataMov).toLocaleString('pt-BR', {
            day: '2-digit', month: '2-digit', year: 'numeric',
            hour: '2-digit', minute: '2-digit'
          })
        : '-';

      const acaoClass = mov.acaoMov ? `acao-${mov.acaoMov.toLowerCase()}` : '';

      return `
        <tr>
          <td>#${mov.movId}</td>
          <td><span class="badge-modulo">${this.formatarModulo(mov.tipoEntidade)}</span></td>
          <td><strong>${mov.responsavel || '-'}</strong></td>
          <td class="mono">${idFormatado}</td>
          <td>${mov.nomeRegistroAfetado || '-'}</td>
          <td><span class="badge-acao ${acaoClass}">${mov.acaoMov || '-'}</span></td>
          <td>${mov.campoAfetado || '-'}</td>
          <td>${dataFormatada}</td>
        </tr>
      `;
    }).join('');

    const html = `
      <!DOCTYPE html>
      <html>
        <head>
          <title>Relatório de Movimentações</title>
          <style>
            @page { size: landscape; margin: 12mm; }
            body { font-family: 'Segoe UI', Arial, sans-serif; color: #1e293b; margin: 0; padding: 20px; }
            .header { display: flex; justify-content: space-between; align-items: flex-end; border-bottom: 2px solid #2563eb; padding-bottom: 12px; margin-bottom: 18px; }
            .header h1 { margin: 0; font-size: 20px; color: #2563eb; text-transform: uppercase; }
            .header p { margin: 4px 0 0 0; font-size: 12px; color: #64748b; }
            .meta { text-align: right; font-size: 12px; color: #475569; }
            
            .filtros-resumo { background: #f8fafc; border: 1px solid #e2e8f0; border-radius: 6px; padding: 10px 14px; margin-bottom: 18px; display: flex; flex-wrap: wrap; gap: 18px; font-size: 12px; }
            .filtros-resumo span strong { color: #334155; }

            table { width: 100%; border-collapse: collapse; font-size: 12px; }
            th { background-color: #f1f5f9; color: #334155; text-align: left; padding: 8px 10px; border-bottom: 2px solid #cbd5e1; text-transform: uppercase; font-size: 11px; }
            td { padding: 8px 10px; border-bottom: 1px solid #e2e8f0; vertical-align: middle; }
            tr:nth-child(even) { background-color: #fafafa; }

            .mono { font-family: monospace; color: #475569; }
            .badge-modulo { background: #e0f2fe; color: #0369a1; padding: 2px 8px; border-radius: 99px; font-weight: 600; font-size: 11px; }
            .badge-acao { padding: 2px 8px; border-radius: 99px; font-weight: 600; font-size: 11px; background: #e2e8f0; color: #334155; }
            .acao-criacao { background: #dcfce7; color: #15803d; }
            .acao-atualizacao { background: #fef3c7; color: #b45309; }
            .acao-exclusao { background: #fee2e2; color: #b91c1c; }

            .footer { margin-top: 24px; text-align: center; font-size: 11px; color: #94a3b8; border-top: 1px solid #e2e8f0; padding-top: 10px; }

            @media print {
              body { padding: 0; }
              th, .filtros-resumo, .badge-modulo, .badge-acao, tr:nth-child(even) {
                -webkit-print-color-adjust: exact;
                print-color-adjust: exact;
              }
            }
          </style>
        </head>
        <body>
          <div class="header">
            <div>
              <h1>Relatório de Histórico de Movimentações</h1>
              <p>Auditoria de ações realizadas no sistema</p>
            </div>
            <div class="meta">
              <div><strong>Emissão:</strong> ${dataEmissao}</div>
              <div><strong>Total de registros:</strong> ${this.movimentacoesExibidas.length}</div>
            </div>
          </div>

          <div class="filtros-resumo">
            <span><strong>Período:</strong> ${periodo}</span>
            <span><strong>Módulo:</strong> ${moduloLabel}</span>
            <span><strong>Ação:</strong> ${acaoLabel}</span>
            <span><strong>Registro:</strong> ${registroLabel}</span>
            <span><strong>Responsável:</strong> ${responsavelLabel}</span>
          </div>

          <table>
            <thead>
              <tr>
                <th>#</th>
                <th>Módulo</th>
                <th>Responsável</th>
                <th>ID Registro</th>
                <th>Registro Afetado</th>
                <th>Ação</th>
                <th>Campo Afetado</th>
                <th>Data e Hora</th>
              </tr>
            </thead>
            <tbody>
              ${linhasHtml}
            </tbody>
          </table>

          <div class="footer">
            Documento gerado automaticamente pelo sistema de Gestão e Controle.
          </div>
        </body>
      </html>
    `;

    janela.document.write(html);
    janela.document.close();

    setTimeout(() => {
      janela.print();
    }, 250);
  }
}