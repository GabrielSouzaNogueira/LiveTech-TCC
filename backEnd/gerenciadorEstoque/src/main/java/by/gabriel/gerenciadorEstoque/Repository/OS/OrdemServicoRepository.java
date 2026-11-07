package by.gabriel.gerenciadorEstoque.Repository.OS;

import by.gabriel.gerenciadorEstoque.Api.DTO.OS.consultas.OsListDTO;
import by.gabriel.gerenciadorEstoque.Enum.OS.OsStatus;
import by.gabriel.gerenciadorEstoque.Model.OS.OrdemServico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrdemServicoRepository extends JpaRepository<OrdemServico, Long> {

    @Query("""
            SELECT new by.gabriel.gerenciadorEstoque.Api.DTO.OS.consultas.OsListDTO(
                o.id,
                o.cliente.nome,
                e.id,
                e.modelo,
                e.numeroSerie,
                o.valorTotalGeral,
                o.status,
                o.dataAbertura,
                o.dataPrevisao
            )
            FROM OrdemServico o
            LEFT JOIN o.equipamento e
            WHERE (:status IS NULL OR o.status = :status)
              AND (:nomeCliente IS NULL OR LOWER(o.cliente.nome) LIKE LOWER(CONCAT('%', :nomeCliente, '%')))
              AND (:equipamentoId IS NULL OR e.id = :equipamentoId)
              AND (:termoEquipamento IS NULL OR 
                   LOWER(e.modelo) LIKE LOWER(CONCAT('%', :termoEquipamento, '%')) OR 
                   LOWER(e.numeroSerie) LIKE LOWER(CONCAT('%', :termoEquipamento, '%')))
              AND (CAST(:dataInicio AS timestamp) IS NULL OR o.dataAbertura >= :dataInicio)
              AND (CAST(:dataFim AS timestamp) IS NULL OR o.dataAbertura <= :dataFim)
            ORDER BY o.dataAbertura DESC
            """)
    List<OsListDTO> filtrarOS(
            @Param("status") OsStatus status,
            @Param("nomeCliente") String nomeCliente,
            @Param("equipamentoId") Long equipamentoId,
            @Param("termoEquipamento") String termoEquipamento,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}