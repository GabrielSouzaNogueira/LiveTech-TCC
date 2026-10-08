package by.gabriel.gerenciadorEstoque.Services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import by.gabriel.gerenciadorEstoque.Api.DTO.Pedido.Consultas.PedidoListDTO;
import by.gabriel.gerenciadorEstoque.Enum.Movimentacao.AcaoMovimentacao;
import by.gabriel.gerenciadorEstoque.Enum.Movimentacao.TipoEntidade;
import by.gabriel.gerenciadorEstoque.Enum.Usuario.UserCargo;
import by.gabriel.gerenciadorEstoque.Enum.Usuario.UserStatus;
import by.gabriel.gerenciadorEstoque.Exception.Cliente.ClienteNaoEncontrado;
import by.gabriel.gerenciadorEstoque.Exception.FormaPag.FormPagNotExistException;
import by.gabriel.gerenciadorEstoque.Exception.Pedido.*;
import by.gabriel.gerenciadorEstoque.Exception.Produto.CostOrSellBellowZeroException;
import by.gabriel.gerenciadorEstoque.Exception.Usuario.UserNotPermission;
import by.gabriel.gerenciadorEstoque.Model.Cliente.Cliente;
import by.gabriel.gerenciadorEstoque.Model.Movimentacao.Movimentacao;
import by.gabriel.gerenciadorEstoque.Repository.Cliente.ClienteRepository;
import by.gabriel.gerenciadorEstoque.Repository.Movimentacao.MovimentacaoRepository;
import org.springframework.stereotype.Service;

import by.gabriel.gerenciadorEstoque.Api.DTO.Pedido.PagPedidoDTO;
import by.gabriel.gerenciadorEstoque.Api.DTO.Pedido.PedidoDTO;
import by.gabriel.gerenciadorEstoque.Exception.Usuario.UserNotFoundException;
import by.gabriel.gerenciadorEstoque.Model.FormaPag.FormaPagto;
import by.gabriel.gerenciadorEstoque.Model.Produto.Produto;
import by.gabriel.gerenciadorEstoque.Model.Usuario.Usuario;
import by.gabriel.gerenciadorEstoque.Model.Pedido.ItensPedido;
import by.gabriel.gerenciadorEstoque.Model.Pedido.PagPedido;
import by.gabriel.gerenciadorEstoque.Model.Pedido.Pedido;
import by.gabriel.gerenciadorEstoque.Enum.Pedido.PedidoStatus;
import by.gabriel.gerenciadorEstoque.Repository.FormPagRespository.FormPagRepository;
import by.gabriel.gerenciadorEstoque.Repository.Produto.ProdutoRepository;
import by.gabriel.gerenciadorEstoque.Repository.Usuario.UserRepository;
import by.gabriel.gerenciadorEstoque.Repository.Pedido.PedidoRepository;
import jakarta.transaction.Transactional;

@Service
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final FormPagRepository formaPagtoRepository;
    private final UserRepository userRepository;
    private final ProdutoRepository produtoRepository;
    private final ClienteRepository clienteRepository; // <-- NOVO

    public PedidoService(PedidoRepository pedidoRepository, ProdutoRepository produtoRepository,
                         UserRepository userRepository, FormPagRepository formaPagtoRepository,
                         ClienteRepository clienteRepository, MovimentacaoRepository movimentacaoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.userRepository = userRepository;
        this.produtoRepository = produtoRepository;
        this.formaPagtoRepository = formaPagtoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Transactional
    public Pedido criarPedidoAberta(PedidoDTO vDto, String usuarioLogado) {

        Usuario userLogado = userRepository.findByNomeIgnoreCase(usuarioLogado)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado: " + usuarioLogado));

        Cliente cliente = clienteRepository.findById(vDto.clienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com o ID: " + vDto.clienteId()));

        Pedido venda = new Pedido();
        venda.setUsuario(userLogado);
        venda.setCliente(cliente); // <-- Associa o objeto Cliente
        venda.setDataVenda(LocalDateTime.now());
        venda.setStatus(PedidoStatus.ABERTA);
        venda.setDesconto(vDto.desconto() != null ? vDto.desconto() : BigDecimal.ZERO);

        BigDecimal valorTotalCalculado = BigDecimal.ZERO;
        List<ItensPedido> listaItens = new ArrayList<>();

        for (var itemDto : vDto.itensPedido()) {

            Produto produto = produtoRepository.findById(itemDto.produtoId())
                    .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

            if (itemDto.quantidade() <= 0) {
                throw new QuantidadeMenorIgualZero("A Quantidade do produto não pode estar zerada!");
            }

            if (itemDto.quantidade() > produto.getQuantidade()) {
                throw new QuantidadeMaiorEstoqueAtual("A quantidade do produto não pode ser maior que o estoque atual");
            }

            if (produto.getPrecoCusto().compareTo(BigDecimal.ZERO) == 0) {
                throw new CostOrSellBellowZeroException("O preço de custo do produto está zerado, ajuste o cadastro do produto para continuar");
            }

            if (produto.getPrecoVenda().compareTo(BigDecimal.ZERO) == 0) {
                throw new CostOrSellBellowZeroException("O preço de venda do produto está zerado, ajuste o cadastro do produto para continuar");
            }

            ItensPedido itemVenda = new ItensPedido();
            itemVenda.setVenda(venda);
            itemVenda.setProduto(produto);
            itemVenda.setQuantidade(itemDto.quantidade());
            itemVenda.setPrecoUnitario(produto.getPrecoCusto());
            itemVenda.setPrecoVenda(produto.getPrecoVenda());

            listaItens.add(itemVenda);

            BigDecimal subtotal = produto.getPrecoVenda().multiply(BigDecimal.valueOf(itemDto.quantidade()));
            valorTotalCalculado = valorTotalCalculado.add(subtotal);
        }

        valorTotalCalculado = valorTotalCalculado.subtract(venda.getDesconto());
        venda.setValorTotal(valorTotalCalculado);
        venda.setItensVenda(listaItens);

        Pedido vendaSalva = pedidoRepository.save(venda);

        return vendaSalva;
    }

    @Transactional
    public Pedido finalizarPedido(Long vendaId, List<PagPedidoDTO> pagamentosDto, String usuarioLogado) {

        Usuario userLogado = userRepository.findByNomeIgnoreCase(usuarioLogado)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado: " + usuarioLogado));

        Pedido venda = pedidoRepository.findById(vendaId)
                .orElseThrow(() -> new PedidoNaoEncontrado("Pedido não encontrada"));

        if (venda.getStatus() != PedidoStatus.ABERTA) {
            throw new PedidoComStatusInvalido("Este pedido não está ABERTO para finalização.");
        }

        for (var itemVenda : venda.getItensVenda()) {
            Produto produto = itemVenda.getProduto();

            if (produto.getQuantidade() < itemVenda.getQuantidade()) {
                throw new QuantidadeMaiorEstoqueAtual("Estoque insuficiente para o produto: " + produto.getNome());
            }

            produto.setQuantidade(produto.getQuantidade() - itemVenda.getQuantidade());
            produtoRepository.save(produto);
        }

        if (venda.getPagVenda() == null) {
            venda.setPagVenda(new ArrayList<>());
        } else {
            venda.getPagVenda().clear();
        }

        List<PagPedido> listaPagamentos = venda.getPagVenda();
        BigDecimal totalPago = BigDecimal.ZERO;

        for (var pagDto : pagamentosDto) {
            FormaPagto formapagto = formaPagtoRepository.findById(pagDto.formaPagId())
                    .orElseThrow(() -> new FormPagNotExistException("Forma de pagamento não encontrada"));

            PagPedido pagVenda = new PagPedido();
            pagVenda.setVenda(venda);
            pagVenda.setFormaPagto(formapagto);
            pagVenda.setValorPago(pagDto.valorPago());

            listaPagamentos.add(pagVenda);
            totalPago = totalPago.add(pagDto.valorPago());
        }

        if (totalPago.compareTo(venda.getValorTotal()) < 0) {
            throw new TotalPagoMenorQueValorDaVenda("O valor total pago é menor que o valor total do pedido.");
        }

        venda.setStatus(PedidoStatus.FINALIZADA);
        Pedido vendaFinalizada = pedidoRepository.save(venda);


        return vendaFinalizada;
    }

    // --- 1. ATUALIZAR VENDA ABERTA ---
    @Transactional
    public Pedido atualizarPedidoAberta(Long vendaId, PedidoDTO vDto, String usuarioLogado) {

        Usuario userLogado = userRepository.findByNomeIgnoreCase(usuarioLogado)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado: " + usuarioLogado));

        if (userLogado.getUserCargo() != UserCargo.ADMINISTRADOR || userLogado.getUserCargo() != UserCargo.DEV || userLogado.getUserCargo() != UserCargo.GERENTE) {
            throw new UserNotPermission("Usuario sem permissão para realizar esta ação");
        }

        Pedido pedido = pedidoRepository.findById(vendaId)
                .orElseThrow(() -> new PedidoNaoEncontrado("Pedido não encontrado"));

        if (pedido.getStatus() != PedidoStatus.ABERTA) {
            throw new PedidoComStatusInvalido("Apenas pedidos ABERTOS podem ser alteradas.");
        }

        Cliente cliente = clienteRepository.findById(vDto.clienteId())
                .orElseThrow(() -> new ClienteNaoEncontrado("Cliente não encontrado com o ID: " + vDto.clienteId()));

        pedido.setCliente(cliente);
        pedido.setDesconto(vDto.desconto() != null ? vDto.desconto() : BigDecimal.ZERO);

        pedido.getItensVenda().clear();

        BigDecimal valorTotalCalculado = BigDecimal.ZERO;

        if (vDto.itensPedido() != null) {
            for (var itemDto : vDto.itensPedido()) {
                Produto produto = produtoRepository.findById(itemDto.produtoId())
                        .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

                if (itemDto.quantidade() <= 0) {
                    throw new QuantidadeMenorIgualZero("A Quantidade do produto não pode estar zerada!");
                }

                if (itemDto.quantidade() > produto.getQuantidade()) {
                    throw new QuantidadeMaiorEstoqueAtual("A quantidade do produto não pode ser maior que o estoque atual");
                }

                if (produto.getPrecoCusto().compareTo(BigDecimal.ZERO) == 0) {
                    throw new CostOrSellBellowZeroException("O preço de custo do produto está zerado, ajuste o cadastro do produto para continuar");
                }

                if (produto.getPrecoVenda().compareTo(BigDecimal.ZERO) == 0) {
                    throw new CostOrSellBellowZeroException("O preço do pedido do produto está zerado, ajuste o cadastro do produto para continuar");
                }

                ItensPedido itemVenda = new ItensPedido();
                itemVenda.setVenda(pedido);
                itemVenda.setProduto(produto);
                itemVenda.setQuantidade(itemDto.quantidade());
                itemVenda.setPrecoUnitario(produto.getPrecoCusto());
                itemVenda.setPrecoVenda(produto.getPrecoVenda());

                // No criar usa listaItens.add(), no atualizar usa venda.getItensVenda().add()
                pedido.getItensVenda().add(itemVenda);

                BigDecimal subtotal = produto.getPrecoVenda().multiply(BigDecimal.valueOf(itemDto.quantidade()));
                valorTotalCalculado = valorTotalCalculado.add(subtotal);
            }
        }

        valorTotalCalculado = valorTotalCalculado.subtract(pedido.getDesconto());
        pedido.setValorTotal(valorTotalCalculado);

        return pedidoRepository.save(pedido);
    }

    // --- 2. CANCELAR/EXCLUIR VENDA ABERTA (Desistência) ---
    @Transactional
    public void cancelarPedidoAberto(Long vendaId, String usuarioLogado) {

        userRepository.findByNomeIgnoreCase(usuarioLogado)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        Pedido pedido = pedidoRepository.findById(vendaId)
                .orElseThrow(() -> new PedidoNaoEncontrado("pedido não encontrada"));

        if (pedido.getStatus() != PedidoStatus.ABERTA) {
            throw new PedidoComStatusInvalido("Apenas pedidos ABERTAS podem ser canceladas por desistência.");
        }

        pedido.setStatus(PedidoStatus.CANCELADA);
        pedidoRepository.save(pedido);
    }

    // --- 3. DEVOLUÇÃO DE VENDA FINALIZADA (Estorno) ---
    @Transactional
    public Pedido devolverPedidoFinalizada(Long vendaId, String usuarioLogado) {

        Usuario userLogado = userRepository.findByNomeIgnoreCase(usuarioLogado)
                .orElseThrow(() -> new UserNotFoundException("Usuário não encontrado"));

        Pedido pedido = pedidoRepository.findById(vendaId)
                .orElseThrow(() -> new PedidoNaoEncontrado("Pedido não encontrada"));

        if (userLogado.getUserCargo() != UserCargo.ADMINISTRADOR || userLogado.getUserCargo() != UserCargo.DEV || userLogado.getUserCargo() != UserCargo.GERENTE) {
            throw new UserNotPermission("Usuario sem permissão para realizar esta ação");
        }

        if (pedido.getStatus() != PedidoStatus.FINALIZADA) {
            throw new PedidoComStatusInvalido("Apenas vendas FINALIZADAS podem ser devolvidas.");
        }

        for (ItensPedido item : pedido.getItensVenda()) {
            Produto produto = item.getProduto();
            produto.setQuantidade(produto.getQuantidade() + item.getQuantidade());
            produtoRepository.save(produto);
        }

        // Estorna o status da venda para manter o histórico, mas inutilizar os totais
        pedido.setStatus(PedidoStatus.DEVOLVIDA);

        return pedidoRepository.save(pedido);
    }

    // --- 6. LISTAR TODAS AS VENDAS (Resumo para a Tabela) ---
    public List<PedidoListDTO> listarTodosOsPedidos() {

        List<Pedido> vendas = pedidoRepository.findAll();

        return vendas.stream().map(venda -> new PedidoListDTO(
                venda.getId(),
                venda.getCliente().getNome(),
                venda.getValorTotal(),
                venda.getStatus(),
                venda.getDataVenda()
        )).toList();
    }

    public Pedido buscarVendaPorId(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new PedidoNaoEncontrado("Pedido não encontrada com o ID: " + id));
    }
}