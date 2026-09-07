package by.gabriel.gerenciadorEstoque.Repository.OS;

import by.gabriel.gerenciadorEstoque.Model.OS.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {
}