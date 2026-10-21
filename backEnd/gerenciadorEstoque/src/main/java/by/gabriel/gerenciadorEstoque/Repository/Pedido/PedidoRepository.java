package by.gabriel.gerenciadorEstoque.Repository.Pedido;

import by.gabriel.gerenciadorEstoque.Api.DTO.Pedido.Consultas.PedidoListDTO;
import by.gabriel.gerenciadorEstoque.Enum.Pedido.PedidoStatus;
import by.gabriel.gerenciadorEstoque.Model.Pedido.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    @Query("""
        SELECT new by.gabriel.gerenciadorEstoque.Api.DTO.Pedido.Consultas.PedidoListDTO(
            p.id,
            p.cliente.nome,
            p.valorTotal,
            p.status,
            p.dataVenda
        )
        FROM Pedido p
        WHERE (:status IS NULL OR p.status = :status)
          AND (:nomeCliente IS NULL OR LOWER(p.cliente.nome) LIKE LOWER(CONCAT('%', :nomeCliente, '%')))
          AND (CAST(:dataInicio AS timestamp) IS NULL OR p.dataVenda >= :dataInicio)
          AND (CAST(:dataFim AS timestamp) IS NULL OR p.dataVenda <= :dataFim)
        ORDER BY p.dataVenda DESC
        """)
    List<PedidoListDTO> filtrarPedidos(
            @Param("status") PedidoStatus status,
            @Param("nomeCliente") String nomeCliente,
            @Param("dataInicio") LocalDateTime dataInicio,
            @Param("dataFim") LocalDateTime dataFim
    );
}