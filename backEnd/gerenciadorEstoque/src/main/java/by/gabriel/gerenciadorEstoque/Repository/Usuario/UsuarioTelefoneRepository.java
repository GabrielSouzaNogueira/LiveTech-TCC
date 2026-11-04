package by.gabriel.gerenciadorEstoque.Repository.Usuario;

import by.gabriel.gerenciadorEstoque.Model.Usuario.UsuarioTelefone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UsuarioTelefoneRepository extends JpaRepository<UsuarioTelefone, Long> {

    boolean existsByUsuarioUserIdAndTelefone(UUID userId, String telefone); // Correto

}
