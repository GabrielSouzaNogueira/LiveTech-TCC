package by.gabriel.gerenciadorEstoque.Repository.Equipamento;

import by.gabriel.gerenciadorEstoque.Api.DTO.Equipamento.EquipamentoResumoDTO;
import by.gabriel.gerenciadorEstoque.Enum.Cliente.ClienteStatus;
import by.gabriel.gerenciadorEstoque.Enum.Equipamento.EquipamentoStatus;
import by.gabriel.gerenciadorEstoque.Model.Cliente.Cliente;
import by.gabriel.gerenciadorEstoque.Model.Equipamento.Equipamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EquipamentoRepository extends JpaRepository<Equipamento, Long> {

    @Query("SELECT e FROM Equipamento e WHERE e.status = :status ORDER BY e.modelo ASC")
    List<Equipamento> findByStatusCustomEntity(@Param("status") EquipamentoStatus status);

    Optional<Equipamento> findByNumeroSerieIgnoreCase(String numSerie);

    @Query("""
           SELECT new by.gabriel.gerenciadorEstoque.Api.DTO.Equipamento.EquipamentoResumoDTO(e.id, e.modelo, e.numeroSerie) 
           FROM Equipamento e 
           WHERE e.status = 'ATIVO' 
           AND (LOWER(e.modelo) LIKE LOWER(CONCAT('%', :termo, '%')) OR LOWER(e.numeroSerie) LIKE LOWER(CONCAT('%', :termo, '%')))
           """)
    List<EquipamentoResumoDTO> buscarParaModal(@Param("termo") String termo);

    @Query("""
           SELECT new by.gabriel.gerenciadorEstoque.Api.DTO.Equipamento.EquipamentoResumoDTO(e.id, e.modelo, e.numeroSerie) 
           FROM Equipamento e 
           WHERE e.status = 'ATIVO'
           """)
    List<EquipamentoResumoDTO> buscarTodosParaModal();
}
