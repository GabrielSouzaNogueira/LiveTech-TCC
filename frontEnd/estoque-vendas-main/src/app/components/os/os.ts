import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { AuthOS } from '../../services/auth-os';
import { 
  OsRequestDTO, OsListDTO, 
  ClienteResumoDTO, ProdutoResumoDTO, ServicoResumoDTO, EquipamentoResumoDTO 
} from '../../models/os-dto';

@Component({
  selector: 'app-os',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './os.html',
  styleUrl: './os.css'
})
export class OsComponent implements OnInit {
  abaAtiva: 'abrir' | 'gestao' = 'abrir';
  usuarioLogado: string = localStorage.getItem('usuarioLogado') || '';
  
  clientesLista: ClienteResumoDTO[] = [];
  produtosLista: ProdutoResumoDTO[] = [];
  servicosLista: ServicoResumoDTO[] = [];
  listaOS: OsListDTO[] = [];

  // Variáveis do Modal de Equipamento
  mostrarModalEquipamento: boolean = false;
  termoBuscaEquipamento: string = '';
  equipamentosPesquisa: EquipamentoResumoDTO[] = [];
  equipamentoSelecionadoNome: string = '';

  editandoId: number | null = null;
  novaOs: OsRequestDTO = this.resetarFormulario();

  produtoSelecionadoId: number = 0;
  produtoQtd: number = 1;
  servicoSelecionadoId: number = 0;
  servicoQtd: number = 1;

  constructor(private osService: AuthOS) {}

  ngOnInit(): void {
    this.carregarOS();
    this.carregarDadosParaFormulario();
  }

  carregarDadosParaFormulario() {
    this.osService.listarClientes().subscribe(res => this.clientesLista = res);
    this.osService.listarProdutos().subscribe(res => this.produtosLista = res);
    this.osService.listarServicos().subscribe(res => this.servicosLista = res);
  }

  mudarAba(aba: 'abrir' | 'gestao') {
    this.abaAtiva = aba;
    if (aba === 'gestao') this.carregarOS();
  }

  resetarFormulario(): OsRequestDTO {
    this.editandoId = null;
    this.equipamentoSelecionadoNome = '';
    return {
      clienteId: 0, equipamentoId: 0, defeitoRelatado: '', desconto: 0,
      dataPrevisao: '', itensProduto: [], itensServico: []
    };
  }

  // --- LÓGICA DO MODAL ---
abrirModalEquipamento() {
    this.mostrarModalEquipamento = true;
    this.termoBuscaEquipamento = '';

    this.buscarEquipamentoModal(); 
  }

  fecharModalEquipamento() {
    this.mostrarModalEquipamento = false;
  }

 buscarEquipamentoModal() {
    this.osService.pesquisarEquipamentosModal(this.termoBuscaEquipamento).subscribe(res => {
      this.equipamentosPesquisa = res;
    });
  }

  selecionarEquipamento(eq: EquipamentoResumoDTO) {
    this.novaOs.equipamentoId = eq.id;
    this.equipamentoSelecionadoNome = `${eq.modelo} (SN: ${eq.numeroSerie})`;
    this.fecharModalEquipamento();
  }
  // ----------------------

  cancelarEdicao() {
    this.novaOs = this.resetarFormulario();
    this.mudarAba('gestao');
  }

  adicionarProduto() {
    if (this.produtoSelecionadoId > 0 && this.produtoQtd > 0) {
      this.novaOs.itensProduto.push({ produtoId: Number(this.produtoSelecionadoId), quantidade: this.produtoQtd });
      this.produtoSelecionadoId = 0; this.produtoQtd = 1;
    }
  }

  adicionarServico() {
    if (this.servicoSelecionadoId > 0 && this.servicoQtd > 0) {
      this.novaOs.itensServico.push({ servicoId: Number(this.servicoSelecionadoId), quantidade: this.servicoQtd });
      this.servicoSelecionadoId = 0; this.servicoQtd = 1;
    }
  }

  getNomeProduto(id: number): string { 
    return this.produtosLista.find(p => p.prodId === id || (p as any).id === id)?.nome || `Produto #${id}`; 
  }
  getNomeServico(id: number): string { 
    return this.servicosLista.find(s => s.servicosId === id)?.descServico || `Serviço #${id}`; 
  }

  salvarOS() {
    // 1. TRAVA DE ESQUECIMENTO: Verifica se tem algo no select que não foi adicionado
    if (this.produtoSelecionadoId > 0) {
      alert("Você selecionou um Produto, mas esqueceu de clicar no botão ➕ para adicionar à lista!");
      return;
    }
    if (this.servicoSelecionadoId > 0) {
      alert("Você selecionou um Serviço, mas esqueceu de clicar no botão ➕ para adicionar à lista!");
      return;
    }

    // 2. Validação dos campos principais obrigatórios
    if(!this.novaOs.clienteId || !this.novaOs.equipamentoId || !this.novaOs.dataPrevisao) {
        alert("Preencha os campos obrigatórios (Cliente, Equipamento e Previsão).");
        return;
    }

    // 3. REGRA DE NEGÓCIO: O Serviço é OBRIGATÓRIO (Produto é opcional)
    if (this.novaOs.itensServico.length === 0) {
        alert("É obrigatório adicionar pelo menos um Serviço para salvar a Ordem de Serviço.");
        return;
    }

    // Prepara o payload para envio
    const payload = { 
      ...this.novaOs, 
      dataPrevisao: this.novaOs.dataPrevisao.includes('T') ? this.novaOs.dataPrevisao : this.novaOs.dataPrevisao + "T00:00:00" 
    };

    // Dispara para o Back-End
    if (this.editandoId) {
      this.osService.atualizarOS(this.editandoId, payload, this.usuarioLogado).subscribe({
        next: () => { alert('OS atualizada com sucesso!'); this.cancelarEdicao(); },
        error: () => alert('Erro ao atualizar OS.')
      });
    } else {
      this.osService.criarOS(payload, this.usuarioLogado).subscribe({
        next: () => { alert('OS criada com sucesso!'); this.cancelarEdicao(); },
        error: () => alert('Erro ao criar OS.')
      });
    }
  }

  carregarOS() {
    this.osService.listarTodas().subscribe(res => this.listaOS = res);
  }

  editarOS(os: OsListDTO) {
    if (os.status === 'FINALIZADA' || os.status === 'CANCELADA') {
      alert('Não é possível editar uma OS Finalizada ou Cancelada.');
      return;
    }
    this.editandoId = os.id;
    const cliente = this.clientesLista.find(c => c.nome + ' ' + c.sobrenome === os.nomeCliente || c.nome === os.nomeCliente);
    
    this.equipamentoSelecionadoNome = `${os.equipamentoModelo} (SN: ${os.equipamentoNumeroSerie})`;
    
    this.novaOs = {
      clienteId: cliente ? cliente.id : 0,
      equipamentoId: os.equipamentoId,
      defeitoRelatado: os.observacoes || '',
      desconto: 0, 
      dataPrevisao: os.dataPrevisao ? os.dataPrevisao.substring(0, 10) : '',
      itensProduto: [], itensServico: []  
    };
    this.mudarAba('abrir');
  }

  finalizarOS(id: number) {
    if(confirm('Deseja realmente finalizar esta OS? Esta ação é irreversível.')) {
      this.osService.finalizarOS(id, this.usuarioLogado).subscribe({
        next: () => {
          alert('OS Finalizada com sucesso!');
          // Recarrega a lista de OS do banco de dados para atualizar o status na tela
          this.carregarOS();
        },
        error: () => alert('Erro ao finalizar a OS.')
      });
    }
  }

  excluirOS(id: number) {
    if(confirm('Deseja realmente excluir esta OS?')) {
      this.osService.deletarOS(id).subscribe({
        next: () => this.carregarOS(),
        error: () => alert('Erro ao excluir a OS.')
      });
    }
  }

  imprimirOS(os: OsListDTO) {
    const janela = window.open('', '', 'width=800,height=600');
    if (janela) {
      const dataAtual = new Date().toLocaleDateString('pt-BR');
      const dataPrev = os.dataPrevisao ? new Date(os.dataPrevisao).toLocaleDateString('pt-BR') : 'Não informada';
      const obs = os.observacoes ? os.observacoes : 'Nenhuma observação registrada.';
      const valorTotal = os.valorTotalGeral ? os.valorTotalGeral.toFixed(2).replace('.', ',') : '0,00';

      // Tratando a exibição do equipamento com os novos campos de Modelo e Série
      const equipamentoPrint = os.equipamentoModelo 
        ? `${os.equipamentoModelo} (SN: ${os.equipamentoNumeroSerie})` 
        : 'Equipamento não especificado';

      const html = `
        <!DOCTYPE html>
        <html>
          <head>
            <title>Impressão - OS #${os.id}</title>
            <style>
              body { font-family: 'Arial', sans-serif; color: #333; line-height: 1.6; padding: 30px; max-width: 800px; margin: 0 auto; }
              .header { text-align: center; border-bottom: 2px solid #2563eb; padding-bottom: 15px; margin-bottom: 25px; }
              .header h1 { margin: 0; color: #2563eb; font-size: 24px; text-transform: uppercase; }
              .header p { margin: 5px 0 0 0; font-size: 14px; color: #666; }
              .section { margin-bottom: 25px; }
              .section h3 { border-bottom: 1px solid #ddd; padding-bottom: 8px; margin-bottom: 15px; color: #444; font-size: 16px; text-transform: uppercase; }
              .row { display: flex; margin-bottom: 8px; font-size: 14px; }
              .label { font-weight: bold; min-width: 150px; color: #555; }
              .value { flex: 1; }
              .total-box { background: #f8f9fa; border: 1px solid #ddd; padding: 15px; text-align: right; border-radius: 6px; margin-top: 30px; }
              .total-box span { font-size: 20px; font-weight: bold; color: #10b981; }
              .signatures { display: flex; justify-content: space-between; margin-top: 80px; }
              .signature-line { border-top: 1px solid #333; width: 45%; text-align: center; padding-top: 8px; font-size: 14px; }
              .footer { margin-top: 40px; text-align: center; font-size: 12px; color: #777; }
              
              @media print {
                body { padding: 0; }
                .total-box { -webkit-print-color-adjust: exact; background: #f8f9fa !important; }
              }
            </style>
          </head>
          <body>
            <div class="header">
              <h1>Ordem de Serviço #${os.id}</h1>
              <p>Data de Emissão: ${dataAtual}</p>
            </div>

            <div class="section">
              <h3>Dados do Cliente</h3>
              <div class="row"><div class="label">Nome:</div> <div class="value">${os.nomeCliente}</div></div>
            </div>

            <div class="section">
              <h3>Detalhes do Equipamento</h3>
              <div class="row"><div class="label">Equipamento:</div> <div class="value">${equipamentoPrint}</div></div>
              <div class="row"><div class="label">Defeito/Obs:</div> <div class="value">${obs}</div></div>
            </div>

            <div class="section">
              <h3>Informações do Serviço</h3>
              <div class="row"><div class="label">Status Atual:</div> <div class="value">${os.status}</div></div>
              <div class="row"><div class="label">Data de Previsão:</div> <div class="value">${dataPrev}</div></div>
            </div>

            <div class="total-box">
              Total Geral: <span>R$ ${valorTotal}</span>
            </div>

            <div class="signatures">
              <div class="signature-line">Assinatura do Cliente</div>
              <div class="signature-line">Assinatura do Responsável</div>
            </div>

            <div class="footer">
              <p>Documento gerado pelo sistema de Gestão e Controle.</p>
            </div>
          </body>
        </html>
      `;
      
      janela.document.write(html);
      janela.document.close();
      
      setTimeout(() => {
        janela.print();
      }, 250);
    } else {
      alert("A impressão foi bloqueada pelo navegador. Permita os pop-ups para este site.");
    }
  }

  // --- LÓGICA DE CÁLCULO DE TOTAIS ---
  
  // Puxa o PREÇO DO PRODUTO (Certifique-se que o DTO tem 'precoVenda')
  getPrecoProduto(id: number): number {
    const prod = this.produtosLista.find(p => p.prodId === id || (p as any).id === id);
    return prod?.precoVenda || 0;
  }

  // Puxa o VALOR DO SERVIÇO
  getValorServico(id: number): number {
    const serv = this.servicosLista.find(s => s.servicosId === id || (s as any).id === id);
    // Se o seu backend devolve com outro nome (ex: preco), troque aqui!
    return serv?.precoServico || 0; // <-- MUDOU AQUI
  }

  get subtotalOS(): number {
    // Calcula o total apenas dos produtos
    const totalProdutos = this.novaOs.itensProduto.reduce((acc, item) => {
      return acc + (this.getPrecoProduto(item.produtoId) * item.quantidade);
    }, 0);

    // Calcula o total apenas dos serviços
    const totalServicos = this.novaOs.itensServico.reduce((acc, item) => {
      return acc + (this.getValorServico(item.servicoId) * item.quantidade);
    }, 0);

    // Soma as duas categorias
    return totalProdutos + totalServicos;
  }

  get totalGeralOS(): number {
    const desc = this.novaOs.desconto || 0;
    const total = this.subtotalOS - desc;
    return total > 0 ? total : 0; 
  }
}