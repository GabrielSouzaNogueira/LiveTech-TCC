package by.gabriel.gerenciadorEstoque.Model.Usuario;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import by.gabriel.gerenciadorEstoque.Enum.Usuario.UserCargo;
import by.gabriel.gerenciadorEstoque.Enum.Usuario.UserStatus;

@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID userId;

    @Column(nullable = false, unique = true) // campo obrigatório
    private String nome;

    @Column(nullable = false)
    private String senha; // será armazenada criptografada

    @Column(nullable = true)
    private String email;

    @Enumerated(EnumType.STRING) // salva o enum como texto
    private UserCargo userCargo;

    @JsonIgnore
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UsuarioTelefone> telefones = new ArrayList<>();

    @Enumerated(EnumType.STRING) // salva o enum como texto
    private UserStatus userStatus;


    public Usuario() {
    }

    // Construtor com todos os campos
    public Usuario(String nome, String senha, String email, UserCargo userCargo,UserStatus userStatus) {
        this.nome = nome;
        setSenhaCriptografada(senha); //Senha já é criptografada no momento da criação do Usuario

        // Normaliza email: se vier vazio, vira null
        this.email = (email == null  || email.isBlank()) ? null : email;

        this.userCargo = userCargo;
        this.userStatus = userStatus;
    }

    // Criptografa a senha antes de salvar
    public void setSenhaCriptografada(String senhaPura) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        this.senha = encoder.encode(senhaPura);
    }

    // Verifica se a senha digitada confere com a armazenada
    public boolean validarSenha(String senhaDigitada) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(senhaDigitada, this.senha);
    }

    // ... (Getters e Setters mantidos iguais)
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public UserCargo getUserCargo() { return userCargo; }
    public void setUserCargo(UserCargo userCargo) { this.userCargo = userCargo; }
    public UserStatus getUserStatus() { return userStatus; }
    public void setUserStatus(UserStatus userStatus) { this.userStatus = userStatus; }
    public List<UsuarioTelefone> getTelefones() {return telefones;}
}