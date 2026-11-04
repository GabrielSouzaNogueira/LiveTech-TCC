package by.gabriel.gerenciadorEstoque.Repository.Cliente;

import by.gabriel.gerenciadorEstoque.Api.DTO.Cliente.Consultas.ClienteSelectDTO;
import by.gabriel.gerenciadorEstoque.Enum.Cliente.ClienteStatus;
import by.gabriel.gerenciadorEstoque.Model.Cliente.Cliente;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmailIgnoreCase(String email);

    // O EntityGraph traz a lista de telefones junto com o cliente sem dar erro
    @EntityGraph(attributePaths = "telefones")
    @Query("SELECT c FROM Cliente c WHERE c.status = :status ORDER BY c.nome ASC")
    List<Cliente> findByStatusCustomEntity(@Param("status") ClienteStatus status);
}