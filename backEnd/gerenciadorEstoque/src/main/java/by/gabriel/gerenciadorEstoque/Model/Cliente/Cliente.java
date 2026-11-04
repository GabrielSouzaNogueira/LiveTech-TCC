package by.gabriel.gerenciadorEstoque.Model.Cliente;


import by.gabriel.gerenciadorEstoque.Enum.Cliente.ClienteStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   private String nome;

   private String sobrenome;

    @Column(nullable = true)
    private String email;

    @Enumerated(EnumType.STRING)
    private ClienteStatus status;

    @JsonIgnore
    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClienteTelefone> telefones = new ArrayList<>();

    public Cliente() {

    }

    public Cliente(String nome, String sobrenome, String email, ClienteStatus status) {
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.email = email;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public ClienteStatus getStatus() {
        return status;
    }

    public void setStatus(ClienteStatus status) {
        this.status = status;
    }

    public List<ClienteTelefone> getTelefones() {
        return telefones;
    }
}
