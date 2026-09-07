package by.gabriel.gerenciadorEstoque.Model.OS;

import by.gabriel.gerenciadorEstoque.Enum.OS.OsStatus;
import by.gabriel.gerenciadorEstoque.Model.Cliente.Cliente;
import by.gabriel.gerenciadorEstoque.Model.Usuario.Usuario;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class OrdemServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Usuario usuario;

    private String descricaoEquipamento;

    private String observacoes;

    private LocalDateTime dataAbertura;
    private LocalDateTime dataPrevisao;
    private LocalDateTime dataFinalizacao;

    private BigDecimal valorTotalServicos = BigDecimal.ZERO;
    private BigDecimal valorTotalProdutos = BigDecimal.ZERO;
    private BigDecimal desconto = BigDecimal.ZERO;
    private BigDecimal valorTotalGeral = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private OsStatus status;

    // A lista de peças utilizadas
    @OneToMany(mappedBy = "os", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItensOsProduto> itensProduto = new ArrayList<>();

    // A lista de mão de obra executada
    @OneToMany(mappedBy = "os", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItensOsServico> itensServico = new ArrayList<>();

    // Pagamentos (você pode reaproveitar a mesma lógica de PagPedido aqui, criando um PagOs)

    public OrdemServico() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getDescricaoEquipamento() {
        return descricaoEquipamento;
    }

    public void setDescricaoEquipamento(String descricaoEquipamento) {
        this.descricaoEquipamento = descricaoEquipamento;
    }

    public String getObservacoes() {
        return observacoes;
    }

    public void setObservacoes(String observacoes) {
        this.observacoes = observacoes;
    }

    public LocalDateTime getDataAbertura() {
        return dataAbertura;
    }

    public void setDataAbertura(LocalDateTime dataAbertura) {
        this.dataAbertura = dataAbertura;
    }

    public LocalDateTime getDataPrevisao() {
        return dataPrevisao;
    }

    public void setDataPrevisao(LocalDateTime dataPrevisao) {
        this.dataPrevisao = dataPrevisao;
    }

    public LocalDateTime getDataFinalizacao() {
        return dataFinalizacao;
    }

    public void setDataFinalizacao(LocalDateTime dataFinalizacao) {
        this.dataFinalizacao = dataFinalizacao;
    }

    public BigDecimal getValorTotalServicos() {
        return valorTotalServicos;
    }

    public void setValorTotalServicos(BigDecimal valorTotalServicos) {
        this.valorTotalServicos = valorTotalServicos;
    }

    public BigDecimal getValorTotalProdutos() {
        return valorTotalProdutos;
    }

    public void setValorTotalProdutos(BigDecimal valorTotalProdutos) {
        this.valorTotalProdutos = valorTotalProdutos;
    }

    public BigDecimal getDesconto() {
        return desconto;
    }

    public void setDesconto(BigDecimal desconto) {
        this.desconto = desconto;
    }

    public BigDecimal getValorTotalGeral() {
        return valorTotalGeral;
    }

    public void setValorTotalGeral(BigDecimal valorTotalGeral) {
        this.valorTotalGeral = valorTotalGeral;
    }

    public OsStatus getStatus() {
        return status;
    }

    public void setStatus(OsStatus status) {
        this.status = status;
    }

    public List<ItensOsProduto> getItensProduto() {
        return itensProduto;
    }

    public void setItensProduto(List<ItensOsProduto> itensProduto) {
        this.itensProduto = itensProduto;
    }

    public List<ItensOsServico> getItensServico() {
        return itensServico;
    }

    public void setItensServico(List<ItensOsServico> itensServico) {
        this.itensServico = itensServico;
    }
}