package by.gabriel.gerenciadorEstoque.Api.DTO.Relatorio.Usuario;

import by.gabriel.gerenciadorEstoque.Enum.Usuario.UserCargo;
import by.gabriel.gerenciadorEstoque.Enum.Usuario.UserStatus;

import java.util.UUID;

public record UsuariosInativadosDTO(
        UUID userId,
        String email,
        String nome,
        String telefone,
        UserCargo userCargo,
        UserStatus userStatus
) {
    // Construtor Compacto: intercepta os dados criados pelo Hibernate
    public UsuariosInativadosDTO {
        if (email == null || email.isBlank()) {
            email = "NÃO INFORMADO";
        }
        if (telefone == null || telefone.isBlank()) {
            telefone = "NÃO INFORMADO";
        }
    }
}