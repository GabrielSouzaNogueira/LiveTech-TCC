package by.gabriel.gerenciadorEstoque.Exception.Pedido;

public class QuantidadeMaiorEstoqueAtual extends RuntimeException {
    public QuantidadeMaiorEstoqueAtual(String message) {
        super(message);
    }
}
