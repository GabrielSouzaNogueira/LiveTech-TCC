package by.gabriel.gerenciadorEstoque.Exception.Pedido;

public class TotalPagoMenorQueValorDaVenda extends RuntimeException {
    public TotalPagoMenorQueValorDaVenda(String message) {
        super(message);
    }
}
