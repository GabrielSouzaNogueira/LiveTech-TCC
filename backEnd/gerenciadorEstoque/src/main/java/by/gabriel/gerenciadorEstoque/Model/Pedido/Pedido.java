package by.gabriel.gerenciadorEstoque.Model.Pedido;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import by.gabriel.gerenciadorEstoque.Model.Cliente.Cliente; // <-- Importação do Cliente
import by.gabriel.gerenciadorEstoque.Model.Usuario.Usuario;
import by.gabriel.gerenciadorEstoque.Enum.Pedido.PedidoStatus;
import jakarta.persistence.*;

@Entity
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Usuario usuario;

    // --- AQUI ESTÁ A MUDANÇA PRINCIPAL ---
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    private BigDecimal valorTotal;

    private BigDecimal desconto;

    private LocalDateTime dataVenda;

    @Enumerated(EnumType.STRING)
    private PedidoStatus status;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItensPedido> itensVenda = new ArrayList<>();

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PagPedido> pagVenda = new ArrayList<>();

    public Pedido() {
    }

    public Pedido(Usuario usuario, Cliente cliente, BigDecimal valorTotal, BigDecimal desconto, LocalDateTime dataVenda, PedidoStatus status) {
        this.cliente = cliente;
        this.dataVenda = dataVenda;
        this.desconto = desconto;
        this.status = status;
        this.usuario = usuario;
        this.valorTotal = valorTotal;
    }

    // --- GETTERS E SETTERS ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    // Atualizado para retornar o Objeto Cliente
    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }

    public BigDecimal getDesconto() { return desconto; }
    public void setDesconto(BigDecimal desconto) { this.desconto = desconto; }

    public LocalDateTime getDataVenda() { return dataVenda; }
    public void setDataVenda(LocalDateTime dataVenda) { this.dataVenda = dataVenda; }

    public PedidoStatus getStatus() { return status; }
    public void setStatus(PedidoStatus status) { this.status = status; }

    public List<ItensPedido> getItensVenda() { return itensVenda; }
    public void setItensVenda(List<ItensPedido> itensVenda) { this.itensVenda = itensVenda; }

    public List<PagPedido> getPagVenda() { return pagVenda; }
    public void setPagVenda(List<PagPedido> pagVenda) { this.pagVenda = pagVenda; }
}