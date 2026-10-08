package by.gabriel.gerenciadorEstoque.Api.DTO.Pedido;

import java.math.BigDecimal;
import java.util.List;

public record PedidoDTO(

        Long clienteId, // <-- Agora recebemos o ID numérico do cliente
        BigDecimal desconto,
        List<ItensPedidoDTO> itensPedido,
        List<PagPedidoDTO> pagVenda
) {}