package by.gabriel.gerenciadorEstoque.Services;

import by.gabriel.gerenciadorEstoque.Api.DTO.OS.OsDTO;
import by.gabriel.gerenciadorEstoque.Enum.OS.OsStatus;
import by.gabriel.gerenciadorEstoque.Exception.Usuario.UserNameNotNullException;
import by.gabriel.gerenciadorEstoque.Model.Cliente.Cliente;
import by.gabriel.gerenciadorEstoque.Model.OS.ItensOsProduto;
import by.gabriel.gerenciadorEstoque.Model.OS.ItensOsServico;
import by.gabriel.gerenciadorEstoque.Model.OS.OrdemServico;
import by.gabriel.gerenciadorEstoque.Model.Produto.Produto;
import by.gabriel.gerenciadorEstoque.Model.Servicos.Servicos;
import by.gabriel.gerenciadorEstoque.Model.Usuario.Usuario;
import by.gabriel.gerenciadorEstoque.Repository.Cliente.ClienteRepository;
import by.gabriel.gerenciadorEstoque.Repository.OS.OrdemServicoRepository;
import by.gabriel.gerenciadorEstoque.Repository.Produto.ProdutoRepository;
import by.gabriel.gerenciadorEstoque.Repository.Servicos.ServicosRepository;
import by.gabriel.gerenciadorEstoque.Repository.Usuario.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OsService {

    private final OrdemServicoRepository osRepository;
    private final ClienteRepository clienteRepository;
    private final UserRepository usuarioRepository;
    private final ProdutoRepository produtoRepository;
    private final ServicosRepository servicosRepository;

    public OsService(OrdemServicoRepository osRepository, ClienteRepository clienteRepository,
                     UserRepository usuarioRepository, ProdutoRepository produtoRepository,
                     ServicosRepository servicosRepository) {
        this.osRepository = osRepository;
        this.clienteRepository = clienteRepository;
        this.usuarioRepository = usuarioRepository;
        this.produtoRepository = produtoRepository;
        this.servicosRepository = servicosRepository;
    }

    // --- CRIAÇÃO ---
    @Transactional
    public OrdemServico criarOs(OsDTO dto, String usuarioLogado) {
        Cliente cliente = clienteRepository.findById(dto.clienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado!"));

        Usuario usuario = usuarioRepository.findByNomeIgnoreCase(usuarioLogado)
                .orElseThrow(() -> new UserNameNotNullException("Usuario Logado não encontrado"));

        OrdemServico os = new OrdemServico();
        os.setCliente(cliente);
        os.setUsuario(usuario);
        os.setDescricaoEquipamento(dto.equipamento());
        os.setObservacoes(dto.defeitoRelatado());
        os.setDataAbertura(LocalDateTime.now());
        os.setDataPrevisao(dto.dataPrevisao());
        os.setStatus(OsStatus.ORCAMENTO);

        BigDecimal descontoFinal = dto.desconto() != null ? dto.desconto() : BigDecimal.ZERO;
        os.setDesconto(descontoFinal);

        BigDecimal totalProdutos = BigDecimal.ZERO;
        BigDecimal totalServicos = BigDecimal.ZERO;

        if (dto.itensProduto() != null && !dto.itensProduto().isEmpty()) {
            for (var itemProdDto : dto.itensProduto()) {
                Produto produto = produtoRepository.findById(itemProdDto.produtoId())
                        .orElseThrow(() -> new RuntimeException("Produto não encontrado!"));

                ItensOsProduto itemProduto = new ItensOsProduto();
                itemProduto.setOs(os);
                itemProduto.setProduto(produto);
                itemProduto.setQuantidade(itemProdDto.quantidade());
                itemProduto.setPrecoUnitario(produto.getPrecoVenda());

                os.getItensProduto().add(itemProduto);

                BigDecimal subtotalProd = produto.getPrecoVenda().multiply(BigDecimal.valueOf(itemProdDto.quantidade()));
                totalProdutos = totalProdutos.add(subtotalProd);
            }
        }

        if (dto.itensServico() != null && !dto.itensServico().isEmpty()) {
            for (var itemServDto : dto.itensServico()) {
                Servicos servico = servicosRepository.findById(itemServDto.servicoId())
                        .orElseThrow(() -> new RuntimeException("Serviço não encontrado!"));

                ItensOsServico itemServico = new ItensOsServico();
                itemServico.setOs(os);
                itemServico.setServico(servico);
                itemServico.setQuantidade(itemServDto.quantidade());
                itemServico.setPrecoUnitario(servico.getPrecoServico());

                os.getItensServico().add(itemServico);

                BigDecimal subtotalServ = servico.getPrecoServico().multiply(BigDecimal.valueOf(itemServDto.quantidade()));
                totalServicos = totalServicos.add(subtotalServ);
            }
        }

        os.setValorTotalProdutos(totalProdutos);
        os.setValorTotalServicos(totalServicos);
        os.setValorTotalGeral(totalProdutos.add(totalServicos).subtract(descontoFinal));

        return osRepository.save(os);
    }

    // --- LISTAGEM E BUSCA ---
    public List<OrdemServico> listarTodas() {
        return osRepository.findAll();
    }

    public OrdemServico buscarPorId(Long id) {
        return osRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ordem de Serviço não encontrada!"));
    }

    // --- ATUALIZAÇÃO (Atualização Parcial Inteligente) ---
    @Transactional
    public OrdemServico atualizarOs(Long id, OsDTO dto) {
        OrdemServico os = buscarPorId(id);

        if (os.getStatus() == OsStatus.FINALIZADA) {
            throw new RuntimeException("Não é possível alterar os dados de uma Ordem de Serviço já finalizada.");
        }

        // 1. Só atualiza o cliente se você mandar o ID no JSON
        if (dto.clienteId() != null) {
            Cliente novoCliente = clienteRepository.findById(dto.clienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado!"));
            os.setCliente(novoCliente);
        }

        // 2. Só atualiza os textos e datas se eles vierem na requisição
        if (dto.equipamento() != null) {
            os.setDescricaoEquipamento(dto.equipamento());
        }
        if (dto.defeitoRelatado() != null) {
            os.setObservacoes(dto.defeitoRelatado());
        }
        if (dto.dataPrevisao() != null) {
            os.setDataPrevisao(dto.dataPrevisao());
        }
        if (dto.desconto() != null) {
            os.setDesconto(dto.desconto());
        }

        // 3. Só mexe nas peças se você mandar a matriz "itensProduto" no JSON
        if (dto.itensProduto() != null) {
            os.getItensProduto().clear(); // Limpa as antigas
            BigDecimal totalProdutos = BigDecimal.ZERO;

            for (var itemProdDto : dto.itensProduto()) {
                Produto produto = produtoRepository.findById(itemProdDto.produtoId())
                        .orElseThrow(() -> new RuntimeException("Produto não encontrado!"));

                ItensOsProduto itemProduto = new ItensOsProduto();
                itemProduto.setOs(os);
                itemProduto.setProduto(produto);
                itemProduto.setQuantidade(itemProdDto.quantidade());
                itemProduto.setPrecoUnitario(produto.getPrecoVenda());

                os.getItensProduto().add(itemProduto);

                BigDecimal subtotalProd = produto.getPrecoVenda().multiply(BigDecimal.valueOf(itemProdDto.quantidade()));
                totalProdutos = totalProdutos.add(subtotalProd);
            }
            os.setValorTotalProdutos(totalProdutos);
        }

        // 4. Só mexe nos serviços se você mandar a matriz "itensServico" no JSON
        if (dto.itensServico() != null) {
            os.getItensServico().clear(); // Limpa os antigos
            BigDecimal totalServicos = BigDecimal.ZERO;

            for (var itemServDto : dto.itensServico()) {
                Servicos servico = servicosRepository.findById(itemServDto.servicoId())
                        .orElseThrow(() -> new RuntimeException("Serviço não encontrado!"));

                ItensOsServico itemServico = new ItensOsServico();
                itemServico.setOs(os);
                itemServico.setServico(servico);
                itemServico.setQuantidade(itemServDto.quantidade());
                itemServico.setPrecoUnitario(servico.getPrecoServico());

                os.getItensServico().add(itemServico);

                BigDecimal subtotalServ = servico.getPrecoServico().multiply(BigDecimal.valueOf(itemServDto.quantidade()));
                totalServicos = totalServicos.add(subtotalServ);
            }
            os.setValorTotalServicos(totalServicos);
        }

        // 5. Recalcula o total geral com os valores que ficaram na OS
        BigDecimal totalGeral = os.getValorTotalProdutos()
                .add(os.getValorTotalServicos())
                .subtract(os.getDesconto());

        os.setValorTotalGeral(totalGeral);

        return osRepository.save(os);
    }

    // --- DELEÇÃO ---
    @Transactional
    public void deletarOs(Long id) {
        OrdemServico os = buscarPorId(id);

        if (os.getStatus() == OsStatus.FINALIZADA || os.getStatus() == OsStatus.APROVADA) {
            throw new RuntimeException("Não é possível excluir uma OS que já foi Aprovada ou Finalizada.");
        }

        osRepository.delete(os);
    }
}