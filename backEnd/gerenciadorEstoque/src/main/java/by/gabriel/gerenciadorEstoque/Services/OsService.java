package by.gabriel.gerenciadorEstoque.Services;

import by.gabriel.gerenciadorEstoque.Api.DTO.OS.OsDTO;
import by.gabriel.gerenciadorEstoque.Api.DTO.OS.consultas.OsListDTO;
import by.gabriel.gerenciadorEstoque.Enum.OS.OsStatus;
import by.gabriel.gerenciadorEstoque.Exception.Cliente.ClienteNaoEncontrado;
import by.gabriel.gerenciadorEstoque.Exception.Os.OsNaoEncontradoException;
import by.gabriel.gerenciadorEstoque.Exception.Os.OsStatusInvalido;
import by.gabriel.gerenciadorEstoque.Exception.Produto.ProdNotFoundException;
import by.gabriel.gerenciadorEstoque.Exception.Servico.ServicoNaoEncontradoException;
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
        os.setStatus(OsStatus.ABERTA); // Ajustado para ABERTA

        BigDecimal descontoFinal = dto.desconto() != null ? dto.desconto() : BigDecimal.ZERO;
        os.setDesconto(descontoFinal);

        BigDecimal totalProdutos = BigDecimal.ZERO;
        BigDecimal totalServicos = BigDecimal.ZERO;

        if (dto.itensProduto() != null && !dto.itensProduto().isEmpty()) {
            for (var itemProdDto : dto.itensProduto()) {
                Produto produto = produtoRepository.findById(itemProdDto.produtoId())
                        .orElseThrow(() -> new ProdNotFoundException("Produto não encontrado!"));

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
                        .orElseThrow(() -> new ServicoNaoEncontradoException("Serviço não encontrado!"));

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
    public List<OsListDTO> listarTodas() {
        return osRepository.filtrarOS(null, null, null, null);
    }

    public OrdemServico buscarPorId(Long id) {
        return osRepository.findById(id)
                .orElseThrow(() -> new OsNaoEncontradoException("Ordem de Serviço não encontrada!"));
    }

    // --- ATUALIZAÇÃO ---
    @Transactional
    public OrdemServico atualizarOs(Long id, OsDTO dto) {
        OrdemServico os = buscarPorId(id);

        if (os.getStatus() == OsStatus.FINALIZADA || os.getStatus() == OsStatus.CANCELADA) {
            throw new OsStatusInvalido("Não é possível alterar os dados de uma Ordem de Serviço finalizada ou cancelada.");
        }

        if (dto.clienteId() != null) {
            Cliente novoCliente = clienteRepository.findById(dto.clienteId())
                    .orElseThrow(() -> new ClienteNaoEncontrado("Cliente não encontrado!"));
            os.setCliente(novoCliente);
        }

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

        if (dto.itensProduto() != null) {
            os.getItensProduto().clear();
            BigDecimal totalProdutos = BigDecimal.ZERO;

            for (var itemProdDto : dto.itensProduto()) {
                Produto produto = produtoRepository.findById(itemProdDto.produtoId())
                        .orElseThrow(() -> new ProdNotFoundException("Produto não encontrado!"));

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

        if (dto.itensServico() != null) {
            os.getItensServico().clear();
            BigDecimal totalServicos = BigDecimal.ZERO;

            for (var itemServDto : dto.itensServico()) {
                Servicos servico = servicosRepository.findById(itemServDto.servicoId())
                        .orElseThrow(() -> new ServicoNaoEncontradoException("Serviço não encontrado!"));

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

        BigDecimal totalGeral = os.getValorTotalProdutos()
                .add(os.getValorTotalServicos())
                .subtract(os.getDesconto());

        os.setValorTotalGeral(totalGeral);

        return osRepository.save(os);
    }

    // --- FINALIZAÇÃO ---
    @Transactional
    public OrdemServico finalizarOs(Long id) {
        OrdemServico os = buscarPorId(id);

        if (os.getStatus() == OsStatus.FINALIZADA) {
            throw new OsStatusInvalido("A Ordem de Serviço já se encontra finalizada.");
        }
        if (os.getStatus() == OsStatus.CANCELADA) {
            throw new OsStatusInvalido("Não é possível finalizar uma Ordem de Serviço cancelada.");
        }

        os.setStatus(OsStatus.FINALIZADA);
        os.setDataFinalizacao(LocalDateTime.now());

        return osRepository.save(os);
    }

    // --- DELEÇÃO ---
    @Transactional
    public void deletarOs(Long id) {
        OrdemServico os = buscarPorId(id);

        if (os.getStatus() == OsStatus.FINALIZADA) {
            throw new OsStatusInvalido("Não é possível excluir uma OS que já foi Finalizada.");
        }

        osRepository.delete(os);
    }
}