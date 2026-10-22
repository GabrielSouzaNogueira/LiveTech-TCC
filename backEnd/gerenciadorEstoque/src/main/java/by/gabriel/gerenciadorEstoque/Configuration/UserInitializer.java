package by.gabriel.gerenciadorEstoque.Configuration;

import by.gabriel.gerenciadorEstoque.Model.Usuario.Usuario;
import by.gabriel.gerenciadorEstoque.Enum.Usuario.UserCargo;
import by.gabriel.gerenciadorEstoque.Enum.Usuario.UserStatus;
import by.gabriel.gerenciadorEstoque.Repository.Usuario.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserInitializer {

    @Bean
    public CommandLineRunner initDatabase(UserRepository usuarioRepository) {
        return args -> {
            // 1. Verifica se a tabela de usuários está vazia
            if (usuarioRepository.count() == 0) {
                System.out.println("--------------------------------------------------");
                System.out.println("⚠️ Nenhum usuário encontrado no banco de dados!");
                System.out.println("Criando usuário administrador padrão de segurança...");

                // 2. Instancia o usuário padrão
                // O construtor já criptografa a senha "admin123" usando o BCrypt automaticamente
                Usuario adminPadrao = new Usuario(
                        "Gabriel",
                        "123",
                        "EmailTeste@email.com",
                        null,
                        UserCargo.DEV,
                        UserStatus.ATIVO
                );

                usuarioRepository.save(adminPadrao);

                System.out.println("✅ Usuário padrão criado com sucesso!");
                System.out.println("🔑 Credenciais: Usuário [ Gabriel ] | Senha [ 123 ]");
                System.out.println("--------------------------------------------------");
            } else {
                System.out.println(">> Banco de dados já possui usuários cadastrados. Inicialização padrão pulada.");
            }
        };
    }


}
