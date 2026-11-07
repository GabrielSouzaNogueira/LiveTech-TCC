package by.gabriel.gerenciadorEstoque.Model.Equipamento;

import by.gabriel.gerenciadorEstoque.Enum.Equipamento.EquipamentoStatus;
import by.gabriel.gerenciadorEstoque.Model.OS.OrdemServico;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class Equipamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "modelo", nullable = false)
    private String modelo;

    @Column(name = "numeroSerie", nullable = false)
    private String numeroSerie;

    @Enumerated(EnumType.STRING)
    private EquipamentoStatus status;

    @JsonIgnore
    @OneToMany(mappedBy = "equipamento")
    private List<OrdemServico> historicoOS;

    public Equipamento() {

    }

    public Equipamento(String modelo, String numeroSerie, EquipamentoStatus status) {
        this.modelo = modelo;
        this.numeroSerie = numeroSerie;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public EquipamentoStatus getStatus() {
        return status;
    }

    public void setStatus(EquipamentoStatus status) {
        this.status = status;
    }
}
