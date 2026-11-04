package by.gabriel.gerenciadorEstoque.Repository.Cliente;

import by.gabriel.gerenciadorEstoque.Model.Cliente.ClienteTelefone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteTelefoneRepository extends JpaRepository<ClienteTelefone, Long> {

    boolean existsByClienteIdAndTelefone(Long clienteId, String telefone);

}
