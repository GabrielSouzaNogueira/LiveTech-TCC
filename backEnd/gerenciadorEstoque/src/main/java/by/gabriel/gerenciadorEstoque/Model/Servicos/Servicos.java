package by.gabriel.gerenciadorEstoque.Model.Servicos;

import by.gabriel.gerenciadorEstoque.Enum.Servicos.ServicosStatus;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "servicos")
public class Servicos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long servicosId;

    @Column(nullable = false)
    private String descServico;

    @Column(nullable = false)
    private BigDecimal precoServico;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ServicosStatus status;

    public Servicos(){

    }

    public Servicos(String descServico, BigDecimal precoServico ,ServicosStatus status) {
        this.descServico = descServico;
        this.precoServico = precoServico;
        this.status = status;
    }

    public BigDecimal getPrecoServico() {
        return precoServico;
    }

    public void setPrecoServico(BigDecimal precoServico) {
        this.precoServico = precoServico;
    }

    public Long getServicosId() {
        return servicosId;
    }

    public void setServicosId(Long servicosId) {
        this.servicosId = servicosId;
    }

    public String getDescServico() {
        return descServico;
    }

    public void setDescServico(String descServico) {
        this.descServico = descServico;
    }

    public ServicosStatus getStatus() {
        return status;
    }

    public void setStatus(ServicosStatus status) {
        this.status = status;
    }
}
