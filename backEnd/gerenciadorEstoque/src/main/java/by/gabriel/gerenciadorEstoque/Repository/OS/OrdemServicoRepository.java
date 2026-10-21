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
            o.descricaoEquipamento,
            o.valorTotalGeral,
            o.status,
            o.dataAbertura,
            o.dataPrevisao
        )
        FROM OrdemServico o
        WHERE (:status IS NULL OR o.status = :status)
          AND (:nomeCliente IS NULL OR LOWER(o.cliente.nome) LIKE LOWER(CONCAT('%', :nomeCliente, '%')))
          AND (CAST(:dataInicio AS timestamp) IS NULL OR o.dataAbertura >= :dataInicio)
          AND (CAST(:dataFim AS timestamp) IS NULL OR o.dataAbertura <= :dataFim)
        ORDER BY o.dataAbertura DESC
        """)
    List<OsListDTO> filtrarOS(
            @Param("status") OsStatus status,
            @Param("nomeCliente") String nomeCliente,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}