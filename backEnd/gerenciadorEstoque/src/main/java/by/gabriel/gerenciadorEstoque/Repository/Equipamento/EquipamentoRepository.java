package by.gabriel.gerenciadorEstoque.Repository.Equipamento;

import by.gabriel.gerenciadorEstoque.Model.Equipamento.Equipamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {
}
