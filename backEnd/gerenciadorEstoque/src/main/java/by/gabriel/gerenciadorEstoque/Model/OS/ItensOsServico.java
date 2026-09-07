package by.gabriel.gerenciadorEstoque.Model.OS;

import by.gabriel.gerenciadorEstoque.Model.Servicos.Servicos;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
public class ItensOsServico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "os_id", nullable = false)
    @JsonIgnore
    private OrdemServico os;

    @ManyToOne
    @JoinColumn(name = "servico_id", nullable = false)
    private Servicos servico;

    private Integer quantidade;
    private BigDecimal precoUnitario;

    public ItensOsServico() {}

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public OrdemServico getOs() { return os; }
    public void setOs(OrdemServico os) { this.os = os; }
    public Servicos getServico() { return servico; }
    public void setServico(Servicos servico) { this.servico = servico; }
    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public void setPrecoUnitario(BigDecimal precoUnitario) { this.precoUnitario = precoUnitario; }
}