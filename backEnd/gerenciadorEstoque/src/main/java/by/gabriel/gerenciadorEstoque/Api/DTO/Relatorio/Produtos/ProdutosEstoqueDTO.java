package by.gabriel.gerenciadorEstoque.Api.DTO.Relatorio.Produtos;

import by.gabriel.gerenciadorEstoque.Enum.Produto.ProdStatus;

import java.math.BigDecimal;

public record ProdutosEstoqueDTO(

        Long id,
        String nome,
        String marca,
        String codBarra,
        Integer quantidade,
        BigDecimal precoCusto,
        BigDecimal precoVenda,
        ProdStatus status


) {


}
