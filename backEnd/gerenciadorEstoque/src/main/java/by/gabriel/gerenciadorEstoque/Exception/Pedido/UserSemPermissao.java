package by.gabriel.gerenciadorEstoque.Exception.Pedido;

public class UserSemPermissao extends RuntimeException {
    public UserSemPermissao(String message) {
        super(message);
    }
}
