package by.gabriel.gerenciadorEstoque.Repository.Equipamento;

import by.gabriel.gerenciadorEstoque.Enum.Cliente.ClienteStatus;
import by.gabriel.gerenciadorEstoque.Enum.Equipamento.EquipamentoStatus;
import by.gabriel.gerenciadorEstoque.Model.Cliente.Cliente;
import by.gabriel.gerenciadorEstoque.Model.Equipamento.Equipamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {

    @Query("SELECT e FROM Equipamento e WHERE e.status = :status ORDER BY e.modelo ASC")
    List<Equipamento> findByStatusCustomEntity(@Param("status") EquipamentoStatus status);

}
