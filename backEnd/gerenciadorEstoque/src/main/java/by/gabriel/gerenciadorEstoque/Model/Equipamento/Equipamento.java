package by.gabriel.gerenciadorEstoque.Model.Equipamento;

import by.gabriel.gerenciadorEstoque.Enum.Equipamento.EquipamentoStatus;
import jakarta.persistence.*;

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

    public Equipamento() {

    }

    public Equipamento(String modelo, String numeroSerie) {
        this.modelo = modelo;
        this.numeroSerie = numeroSerie;
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
}
